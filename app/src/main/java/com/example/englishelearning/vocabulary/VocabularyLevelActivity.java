package com.example.englishelearning.vocabulary;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.adapter.OnTopicClickListener;
import com.example.englishelearning.adapter.VocabularyLevelAdapter;
import com.example.englishelearning.model.VocabularyLevel;
import com.example.englishelearning.model.VocabularyTopic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VocabularyLevelActivity extends AppCompatActivity implements OnTopicClickListener {

    private RecyclerView recyclerView;
    private VocabularyLevelAdapter adapter;
    private DatabaseReference databaseRef;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_level);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("vocabulary/levels");

        recyclerView = findViewById(R.id.levelRecyclerView);
        btnBack = findViewById(R.id.btn_back);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VocabularyLevelAdapter(this);
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        loadLevelsFromFirebase();
    }

    private void loadLevelsFromFirebase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<VocabularyLevel> levels = new ArrayList<>();
                for (DataSnapshot levelSnapshot : dataSnapshot.getChildren()) {
                    VocabularyLevel level = levelSnapshot.getValue(VocabularyLevel.class);
                    if (level != null) {
                        levels.add(level);
                    }
                }
                
                if (levels.isEmpty()) {
                    Toast.makeText(VocabularyLevelActivity.this, 
                        "No levels available", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.setLevels(levels);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(VocabularyLevelActivity.this,
                    "Failed to load levels", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTopicClick(VocabularyTopic topic) {
        startActivity(VocabularyWordActivity.newIntent(this, topic));
    }
} 