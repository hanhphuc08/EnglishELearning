package com.example.englishelearning;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
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

import com.example.englishelearning.adapter.TopicAdapter;
import com.example.englishelearning.model.Topic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TopicsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String level;
    private RecyclerView topicsRecyclerView;
    private List<Topic> topicList = new ArrayList<>();
    private TopicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        mDatabase = FirebaseDatabase.getInstance().getReference("listening");
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
        adapter = new TopicAdapter(topicList,this::startExerciseActivity);
        topicsRecyclerView.setAdapter(adapter);

        mDatabase.child(level).child("topics").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topicList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Topic topic = snapshot.getValue(Topic.class);
                        if (topic != null) {
                            topicList.add(topic);
                            android.util.Log.d("TopicsActivity", "Loaded topic: " + topic.title);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (topicList.isEmpty()) {
                        Toast.makeText(TopicsActivity.this, "No topics found for " + level, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(TopicsActivity.this, "No data available for " + level, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TopicsActivity.this, "Error loading topics: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
    private void startExerciseActivity(Topic topic) {
        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.putExtra("LEVEL", level);
        intent.putExtra("TOPIC", topic);
        startActivity(intent);
    }

}