package com.example.englishelearning.listening;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishelearning.LoginActivity;
import com.example.englishelearning.R;
import com.example.englishelearning.model.Question;
import com.example.englishelearning.model.Topic;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExerciseActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String userId, level;
    private Topic topic;
    private MediaPlayer mediaPlayer;
    private TextView topicTitle, transcriptText;
    private Button playButton, showTranscriptButton, submitButton;
    private LinearLayout questionsContainer;
    private List<RadioGroup> radioGroups = new ArrayList<>();
    private boolean isTranscriptVisible = false;
    private ImageView backButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        level = getIntent().getStringExtra("LEVEL");
        topic = (Topic) getIntent().getSerializableExtra("TOPIC");
        if (topic == null || level == null || topic.questions == null) {
            Toast.makeText(this, "Error: Invalid topic or level", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        topicTitle = findViewById(R.id.topicTitle);
        transcriptText = findViewById(R.id.transcriptText);
        playButton = findViewById(R.id.playButton);
        showTranscriptButton = findViewById(R.id.showTranscriptButton);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
        questionsContainer = findViewById(R.id.questionsContainer);

        // Set topic title
        topicTitle.setText(topic.title);

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Play audio
        mediaPlayer = new MediaPlayer();
        playButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playButton.setText("Play Audio");
            } else {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(topic.audio);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    playButton.setText("Pause Audio");
                } catch (IOException e) {
                    Toast.makeText(this, "Error playing audio: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Show/hide transcript
        transcriptText.setText(topic.transcript);
        showTranscriptButton.setOnClickListener(v -> {
            if (transcriptText.getVisibility() == View.VISIBLE) {
                transcriptText.setVisibility(View.GONE);
                showTranscriptButton.setText("Show Transcript");
            } else {
                transcriptText.setVisibility(View.VISIBLE);
                showTranscriptButton.setText("Hide Transcript");
            }
        });

        // Display questions
        displayQuestions();

        // Submit button
        submitButton.setOnClickListener(v -> {
            int correctCount = 0;
            for (int i = 0; i < topic.questions.size(); i++) {
                RadioGroup radioGroup = radioGroups.get(i);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedButton = findViewById(selectedId);
                String selectedAnswer = selectedButton.getText().toString();
                Question question = topic.questions.get(i);
                if (selectedAnswer.equals(question.answer)) {
                    correctCount++;
                }
            }

            final int finalCorrectCount = correctCount;
            int topicProgress = (correctCount * 100) / topic.questions.size();
            String userId = currentUser.getUid();
            DatabaseReference topicRef = mDatabase.child(userId).child("progress").child("listening")
                    .child(level).child("topics").child(String.valueOf(topic.id));
            topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long previousProgress = dataSnapshot.getValue(Long.class);
                    if (previousProgress == null || topicProgress > previousProgress) {
                        topicRef.setValue(topicProgress)
                                .addOnSuccessListener(aVoid -> {
                                    updateLevelProgress(userId);
                                    Intent intent = new Intent(ExerciseActivity.this, ResultActivity.class);
                                    intent.putExtra("CORRECT_COUNT", finalCorrectCount);
                                    intent.putExtra("TOTAL_QUESTIONS", topic.questions.size());
                                    intent.putExtra("TOPIC_PROGRESS", topicProgress);
                                    intent.putExtra("LEVEL", level);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(ExerciseActivity.this,
                                        "Error saving topic progress: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    } else {
                        updateLevelProgress(userId);
                        Intent intent = new Intent(ExerciseActivity.this, ResultActivity.class);
                        intent.putExtra("CORRECT_COUNT", finalCorrectCount);
                        intent.putExtra("TOTAL_QUESTIONS", topic.questions.size());
                        intent.putExtra("TOPIC_PROGRESS", topicProgress);
                        intent.putExtra("LEVEL", level);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ExerciseActivity.this, "Error checking topic progress: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            Toast.makeText(this, "Topic Score: " + topicProgress + "%", Toast.LENGTH_LONG).show();
        });
    }

    private void updateLevelProgress(String userId) {
        DatabaseReference topicsRef = mDatabase.child(userId).child("progress").child("listening")
                .child(level).child("topics");
        topicsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    android.util.Log.w("ExerciseActivity", "No topics found for level " + level);
                    return;
                }
                long totalProgress = 0;
                int topicCount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Long progress = snapshot.getValue(Long.class);
                    if (progress != null) {
                        totalProgress += progress;
                        topicCount++;
                    } else {
                        android.util.Log.w("ExerciseActivity", "Invalid progress for topic " + snapshot.getKey());
                    }
                }
                if(topicCount > 0){
                    long levelProgress = totalProgress / topicCount;
                    mDatabase.child(userId).child("progress").child("listening").child(level)
                            .child("levelProgress").setValue(levelProgress)
                            .addOnSuccessListener(aVoid -> android.util.Log.d("ExerciseActivity", "Level progress updated: " + levelProgress))
                            .addOnFailureListener(e -> Toast.makeText(ExerciseActivity.this,
                                    "Error saving level progress", Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ExerciseActivity.this, "Error updating level progress: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayQuestions() {
        questionsContainer.removeAllViews();
        radioGroups.clear();

        for (int i = 0; i < topic.questions.size(); i++) {
            Question question = topic.questions.get(i);

            // Question text
            TextView questionText = new TextView(this);
            questionText.setText((i + 1) + ". " + question.question);
            questionText.setTextSize(14);
            questionText.setTextColor(getResources().getColor(android.R.color.black));
            questionText.setPadding(0, 16, 0, 8);
            questionsContainer.addView(questionText);

            // Options
            RadioGroup radioGroup = new RadioGroup(this);
            radioGroup.setOrientation(RadioGroup.VERTICAL);
            radioGroups.add(radioGroup);

            for (String option : question.options) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(option);
                radioButton.setTextSize(14);
                radioButton.setTextColor(getResources().getColor(android.R.color.black));
                radioGroup.addView(radioButton);
            }

            questionsContainer.addView(radioGroup);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playButton.setText("Play Audio");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}