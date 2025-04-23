package com.example.englishelearning.speaking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ExerciseSpeakingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String level;
    private TopicSpeaking topicSpeaking;
    private TextView topicTitle, transcriptText;
    private ImageView backButton;
    private Button playButton, showTranscriptButton, testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_listening);
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

        if (topicSpeaking.key_phrases != null) {
            for (String phrase : topicSpeaking.key_phrases) {
                Log.d("ExerciseActivity", "Key Phrase: " + phrase);
            }
        } else {
            Log.d("ExerciseActivity", "Key Phrases is null");
        }

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
    }
}