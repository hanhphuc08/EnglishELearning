package com.example.englishelearning.speaking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.adapter.TopicSpeakingAdapter;
import com.example.englishelearning.model.TopicSpeaking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TopicsSpeakingActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String level;
    private RecyclerView topicsRecyclerView;
    private TopicSpeakingAdapter adapter;
    private List<TopicSpeaking> topicSpeakingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_topics_speaking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mDatabase = FirebaseDatabase.getInstance().getReference("speaking");
        level = getIntent().getStringExtra("LEVEL");

        if (level == null) {
            Toast.makeText(this, "Error: Level not specified", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        TextView topicsTitle = findViewById(R.id.topicsTitle);
        topicsTitle.setText("Topics for Level " + level);

        topicsRecyclerView = findViewById(R.id.topicsRecyclerView);
        topicsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TopicSpeakingAdapter(topicSpeakingList,this::startExerciseActivity);
        topicsRecyclerView.setAdapter(adapter);

        mDatabase.child(level).child("topics").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topicSpeakingList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TopicSpeaking topicSpeaking = snapshot.getValue(TopicSpeaking.class);
                        if (topicSpeaking != null) {
                            topicSpeakingList.add(topicSpeaking);
                            android.util.Log.d("TopicsActivity", "Loaded topic: " + topicSpeaking.title);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (topicSpeakingList.isEmpty()) {
                        Toast.makeText(TopicsSpeakingActivity.this, "No topics found for " + level, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(TopicsSpeakingActivity.this, "No data available for " + level, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TopicsSpeakingActivity.this, "Error loading topics: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void startExerciseActivity(TopicSpeaking topicSpeaking) {
        Intent intent = new Intent(this, ExerciseSpeakingActivity.class);
        intent.putExtra("LEVEL", level);
        intent.putExtra("TOPIC", topicSpeaking);
        startActivity(intent);
    }
}