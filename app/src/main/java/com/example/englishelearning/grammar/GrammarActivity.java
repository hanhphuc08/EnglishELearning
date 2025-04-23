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
import com.example.englishelearning.adapter.GrammarTopicAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GrammarActivity extends AppCompatActivity {
    RecyclerView grammarTopicRecycler;
    DatabaseReference grammarRef;
    List<GrammarTopic> topicList = new ArrayList<>();
    GrammarTopicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar);
        ImageView backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());
        grammarTopicRecycler = findViewById(R.id.grammarTopicRecycler);
        grammarTopicRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GrammarTopicAdapter(topicList, this::onTopicClick);
        grammarTopicRecycler.setAdapter(adapter);

        grammarRef = FirebaseDatabase.getInstance().getReference("grammar");

        grammarRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topicList.clear();
                for (DataSnapshot topicSnap : snapshot.getChildren()) {
                    GrammarTopic topic = topicSnap.getValue(GrammarTopic.class);
                    topic.setKey(topicSnap.getKey()); // để lấy key gửi qua activity tiếp theo
                    topicList.add(topic);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GrammarActivity.this, "Failed to load topics", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onTopicClick(GrammarTopic topic) {
        if (topic == null || topic.getKey() == null) {
            Toast.makeText(GrammarActivity.this, "Error: No topic selected", Toast.LENGTH_SHORT).show();
            return;
        }
        // Handle topic click to load levels
        Intent intent = new Intent(this, GrammarLevelActivity.class);
        intent.putExtra("topicKey", topic.getKey());
        startActivity(intent);
    }

}


