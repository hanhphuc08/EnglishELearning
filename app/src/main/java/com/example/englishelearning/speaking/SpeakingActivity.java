package com.example.englishelearning.speaking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishelearning.R;
import com.example.englishelearning.listening.TopicsActivity;

public class SpeakingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_speaking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        findViewById(R.id.cardA1).setOnClickListener(v -> startTopicsActivity("A1"));
        findViewById(R.id.cardA2).setOnClickListener(v -> startTopicsActivity("A2"));
        findViewById(R.id.cardB1).setOnClickListener(v -> startTopicsActivity("B1"));
        findViewById(R.id.cardB2).setOnClickListener(v -> startTopicsActivity("B2"));
    }

    private void startTopicsActivity(String level) {
        Intent intent = new Intent(this, TopicsSpeakingActivity.class);
        intent.putExtra("LEVEL", level);
        startActivity(intent);
    }
}