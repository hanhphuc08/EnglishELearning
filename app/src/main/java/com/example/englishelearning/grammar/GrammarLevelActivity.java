package com.example.englishelearning.grammar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.adapter.GrammarLevelAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GrammarLevelActivity extends AppCompatActivity {
    private RecyclerView levelRecyclerView;
    private GrammarLevelAdapter adapter;
    private List<GrammarLevel> levelList = new ArrayList<>();
    private DatabaseReference grammarRef;
    private String topicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_level);
        ImageView backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());
        levelRecyclerView = findViewById(R.id.levelRecyclerView);
        levelRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        topicKey = getIntent().getStringExtra("topicKey");
        if (topicKey == null) {
            Toast.makeText(this, "Error: No topic selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        grammarRef = FirebaseDatabase.getInstance().getReference("grammar").child(topicKey);

        grammarRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GrammarTopic topic = snapshot.getValue(GrammarTopic.class);
                if (topic != null && topic.getLevels() != null && !topic.getLevels().isEmpty()) {
                    levelList.addAll(topic.getLevels());
                    adapter = new GrammarLevelAdapter(levelList, topicKey, GrammarLevelActivity.this::onLevelClick);
                    levelRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(GrammarLevelActivity.this, "No levels available", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GrammarLevelActivity.this, "Failed to load levels", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onLevelClick(GrammarLevel level) {
        Intent intent = new Intent(this, GrammarQuestionActivity.class);
        intent.putExtra("level", level.getLevel());
        intent.putExtra("topicKey", topicKey);
        startActivity(intent);
    }
}