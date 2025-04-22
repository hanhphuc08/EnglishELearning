    package com.example.englishelearning.listening;

    import android.content.Intent;
    import android.os.Bundle;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import com.example.englishelearning.LoginActivity;
    import com.example.englishelearning.R;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    public class ListeningActivity extends AppCompatActivity {

        private FirebaseAuth mAuth;
        private DatabaseReference mDatabase;
        private TextView progressA1, progressA2, progressB1, progressB2, progressC1;
        private TextView progressA1Card, progressA2Card, progressB1Card, progressB2Card, progressC1Card;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_listening);

            try {
                mAuth = FirebaseAuth.getInstance();
                mDatabase = FirebaseDatabase.getInstance().getReference("users");
            } catch (IllegalStateException e) {

                Toast.makeText(this, "Firebase initialization failed", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            ImageView backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> finish());
            progressA1 = findViewById(R.id.progressA1);
            progressA2 = findViewById(R.id.progressA2);
            progressB1 = findViewById(R.id.progressB1);
            progressB2 = findViewById(R.id.progressB2);
            progressC1 = findViewById(R.id.progressC1);
            progressA1Card = findViewById(R.id.progressA1Card);
            progressA2Card = findViewById(R.id.progressA2Card);
            progressB1Card = findViewById(R.id.progressB1Card);
            progressB2Card = findViewById(R.id.progressB2Card);
            progressC1Card = findViewById(R.id.progressC1Card);

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                mDatabase.child(userId).child("progress").child("listening")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                updateProgress(dataSnapshot, "A1", progressA1, progressA1Card);
                                updateProgress(dataSnapshot, "A2", progressA2, progressA2Card);
                                updateProgress(dataSnapshot, "B1", progressB1, progressB1Card);
                                updateProgress(dataSnapshot, "B2", progressB2, progressB2Card);
                                updateProgress(dataSnapshot, "C1", progressC1, progressC1Card);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(ListeningActivity.this, "Error loading progress: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else{
                Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            findViewById(R.id.cardA1).setOnClickListener(v -> startTopicsActivity("A1"));
            findViewById(R.id.cardA2).setOnClickListener(v -> startTopicsActivity("A2"));
            findViewById(R.id.cardB1).setOnClickListener(v -> startTopicsActivity("B1"));
            findViewById(R.id.cardB2).setOnClickListener(v -> startTopicsActivity("B2"));
            findViewById(R.id.cardC1).setOnClickListener(v -> startTopicsActivity("C1"));

            }
        private void updateProgress(DataSnapshot dataSnapshot, String level, TextView progressText, TextView progressCard){
            DataSnapshot levelSnapshot = dataSnapshot.child(level).child("levelProgress");
            Long progress = null;
            if (levelSnapshot.exists()) {
                try {
                    progress = levelSnapshot.getValue(Long.class);
                } catch (Exception e) {
                    android.util.Log.e("ListeningActivity", "Error reading progress for " + level + ": " + e.getMessage());
                }
            } else{
                DataSnapshot topicsSnapshot = dataSnapshot.child(level).child("topics");
                if (topicsSnapshot.exists()) {
                    long totalProgress = 0;
                    int topicCount = 0;
                    for (DataSnapshot topicSnapshot : topicsSnapshot.getChildren()) {
                        Long topicProgress = topicSnapshot.getValue(Long.class);
                        if (topicProgress != null) {
                            totalProgress += topicProgress;
                            topicCount++;
                        }
                    }
                    if (topicCount > 0) {
                        progress = totalProgress / topicCount;
                        mDatabase.child(mAuth.getCurrentUser().getUid()).child("progress")
                                .child("listening").child(level).child("levelProgress")
                                .setValue(progress);
                    }
                }
            }
            long finalProgress = progress != null ? progress : 0;
            progressText.setText(level + ": " + finalProgress + "%");
            progressCard.setText("Progress: " + finalProgress + "%");

        }
        private void startTopicsActivity(String level) {
            Intent intent = new Intent(this, TopicsActivity.class);
            intent.putExtra("LEVEL", level);
            startActivity(intent);
        }
    }