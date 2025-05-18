package com.example.englishelearning.vocabulary;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.adapter.GameScoreAdapter;
import com.example.englishelearning.model.GameScore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameHistoryActivity extends AppCompatActivity {
    private RecyclerView rvScores;
    private GameScoreAdapter adapter;
    private DatabaseReference scoresRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);

        setupViews();
        loadScores();
    }

    private void setupViews() {
        rvScores = findViewById(R.id.rv_scores);
        rvScores.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameScoreAdapter(new ArrayList<>());
        rvScores.setAdapter(adapter);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void loadScores() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        scoresRef = FirebaseDatabase.getInstance().getReference("game_scores")
                .child(userId);

        scoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<GameScore> scores = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GameScore score = snapshot.getValue(GameScore.class);
                    if (score != null) {
                        scores.add(score);
                    }
                }
                Collections.sort(scores, (s1, s2) -> s2.getPlayedAt().compareTo(s1.getPlayedAt()));
                adapter.updateScores(scores);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GameHistoryActivity.this, 
                    "Error loading scores: " + databaseError.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
} 