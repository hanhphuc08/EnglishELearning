package com.example.englishelearning.speaking;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishelearning.LoginActivity;
import com.example.englishelearning.R;
import com.example.englishelearning.model.TopicSpeaking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExerciseSpeakingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String level;
    private TopicSpeaking topicSpeaking;
    private TextView topicTitle, transcriptText;
    private ImageView backButton;
    private Button playButton, showTranscriptButton, testButton;
    private VideoView videoView;
    private ProgressBar videoLoadingProgress;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_speaking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        level = getIntent().getStringExtra("LEVEL");

        topicSpeaking = (TopicSpeaking) getIntent().getSerializableExtra("TOPIC");

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        topicTitle = findViewById(R.id.topicTitle);
        topicTitle.setText(topicSpeaking.title);

        transcriptText = findViewById(R.id.transcriptText);
        transcriptText.setText(topicSpeaking.transcript);
        showTranscriptButton = findViewById(R.id.showTranscriptButton);
        showTranscriptButton.setOnClickListener(v -> {
            if (transcriptText.getVisibility() == View.VISIBLE) {
                transcriptText.setVisibility(View.GONE);
                showTranscriptButton.setText("Show Transcript");
            } else {
                transcriptText.setVisibility(View.VISIBLE);
                showTranscriptButton.setText("Hide Transcript");
            }
        });

        videoView = findViewById(R.id.videoView);
        videoLoadingProgress = findViewById(R.id.videoLoadingProgress);

        // Setup media controller for video controls
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Set video completion listener
        videoView.setOnCompletionListener(mp -> {
            Toast.makeText(ExerciseSpeakingActivity.this, "Video completed", Toast.LENGTH_SHORT).show();
            playButton.setText("Play Again");
        });

        // Set error listener
        videoView.setOnErrorListener((mp, what, extra) -> {
            videoLoadingProgress.setVisibility(View.GONE);
            Toast.makeText(ExerciseSpeakingActivity.this, "Error playing video", Toast.LENGTH_LONG).show();
            return true;
        });

        // Set prepared listener to hide progress bar when video is ready
        videoView.setOnPreparedListener(mp -> {
            videoLoadingProgress.setVisibility(View.GONE);
            mp.start();
            playButton.setText("Pause");
        });

        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                playButton.setText("Play");
            } else {
                if (videoView.getCurrentPosition() > 0) {
                    // Resume video if it was paused
                    videoView.start();
                    playButton.setText("Pause");
                } else {
                    // Start fresh video
                    playButton.setText("Loading...");
                    videoLoadingProgress.setVisibility(View.VISIBLE);

                    // Convert Google Drive URL to a playable URL
                    String videoUrl = topicSpeaking.video;
                    if (videoUrl != null && !videoUrl.isEmpty()) {
                        new FetchGoogleDriveVideoUrlTask().execute(videoUrl);
                    } else {
                        Toast.makeText(ExerciseSpeakingActivity.this, "Video URL not available", Toast.LENGTH_LONG).show();
                        videoLoadingProgress.setVisibility(View.GONE);
                        playButton.setText("Play");
                    }
                }
            }
        });

        testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(v -> {
            Intent intent = new Intent(ExerciseSpeakingActivity.this, TestSpeakingActivity.class);
            intent.putStringArrayListExtra("KEY_PHRASES", new ArrayList<>(topicSpeaking.key_phrases));
            intent.putExtra("LEVEL", level);
            intent.putExtra("TOPIC", topicSpeaking);
            startActivity(intent);
        });
    }

    // AsyncTask to extract direct video URL from Google Drive link
    private class FetchGoogleDriveVideoUrlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String googleDriveUrl = params[0];
            String fileId = extractFileIdFromUrl(googleDriveUrl);
            if (fileId != null) {
                // Using direct download URL format for Google Drive
                return "https://drive.google.com/uc?export=download&id=" + fileId;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String directUrl) {
            if (directUrl != null) {
                try {
                    // Configure VideoView with the direct URL
                    videoView.setVideoURI(Uri.parse(directUrl));
                    videoView.requestFocus();
                } catch (Exception e) {
                    Log.e("VideoPlayer", "Error setting video URI: " + e.getMessage());
                    Toast.makeText(ExerciseSpeakingActivity.this, "Error loading video", Toast.LENGTH_LONG).show();
                    videoLoadingProgress.setVisibility(View.GONE);
                    playButton.setText("Play");
                }
            } else {
                Toast.makeText(ExerciseSpeakingActivity.this, "Invalid video URL", Toast.LENGTH_LONG).show();
                videoLoadingProgress.setVisibility(View.GONE);
                playButton.setText("Play");
            }
        }
    }

    // Extract file ID from Google Drive URL
    private String extractFileIdFromUrl(String url) {
        try {
            // Pattern for file view URL: https://drive.google.com/file/d/FILE_ID/view
            Pattern pattern = Pattern.compile("/d/([a-zA-Z0-9_-]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }

            // Check if it's already in the correct format
            Uri uri = Uri.parse(url);
            String id = uri.getQueryParameter("id");
            if (id != null) {
                return id;
            }
        } catch (Exception e) {
            Log.e("VideoPlayer", "Error extracting file ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
            playButton.setText("Play");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}