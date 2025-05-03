package com.example.englishelearning.speaking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishelearning.R;
import com.example.englishelearning.model.TopicSpeaking;

public class ResultActivity extends AppCompatActivity {
    private TextView scoreText, percentageText;
    private ProgressBar resultProgressBar;
    private Button backToTopicsButton;
    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        scoreText = findViewById(R.id.scoreText);
        percentageText = findViewById(R.id.percentageText);
        resultProgressBar = findViewById(R.id.resultProgressBar);
        backToTopicsButton = findViewById(R.id.backToTopicsButton);

        int correctCount = getIntent().getIntExtra("CORRECT_COUNT", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);
        int topicProgress = getIntent().getIntExtra("TOPIC_PROGRESS", 0);
        Log.d("ResultActivity", "topicProgress: " + topicProgress);


        level = getIntent().getStringExtra("LEVEL");
        scoreText.setText("Score: " + correctCount + "/" + totalQuestions);
        percentageText.setText(topicProgress + "%");
        resultProgressBar.setProgress(topicProgress);

        backToTopicsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TopicsSpeakingActivity.class);
            intent.putExtra("LEVEL", level);
            startActivity(intent);
            finish();
        });
    }
}