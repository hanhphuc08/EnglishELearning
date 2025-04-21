    package com.example.englishelearning;

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

    import com.google.firebase.Firebase;
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
                // Handle case where Firebase is not initialized
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
                                Long a1Progress = dataSnapshot.child("A1").getValue(Long.class);
                                Long a2Progress = dataSnapshot.child("A2").getValue(Long.class);
                                Long b1Progress = dataSnapshot.child("B1").getValue(Long.class);
                                Long b2Progress = dataSnapshot.child("B2").getValue(Long.class);
                                Long c1Progress = dataSnapshot.child("C1").getValue(Long.class);

                                progressA1.setText("A1: " + (a1Progress != null ? a1Progress : 0) + "%");
                                progressA2.setText("A2: " + (a2Progress != null ? a2Progress : 0) + "%");
                                progressB1.setText("B1: " + (b1Progress != null ? b1Progress : 0) + "%");
                                progressB2.setText("B2: " + (b2Progress != null ? b2Progress : 0) + "%");
                                progressC1.setText("C1: " + (c1Progress != null ? c1Progress : 0) + "%");

                                progressA1Card.setText("Progress: " + (a1Progress != null ? a1Progress : 0) + "%");
                                progressA2Card.setText("Progress: " + (a2Progress != null ? a2Progress : 0) + "%");
                                progressB1Card.setText("Progress: " + (b1Progress != null ? b1Progress : 0) + "%");
                                progressB2Card.setText("Progress: " + (b2Progress != null ? b2Progress : 0) + "%");
                                progressC1Card.setText("Progress: " + (c1Progress != null ? c1Progress : 0) + "%");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
            }
            findViewById(R.id.cardA1).setOnClickListener(v -> startTopicsActivity("A1"));
            findViewById(R.id.cardA2).setOnClickListener(v -> startTopicsActivity("A2"));
            findViewById(R.id.cardB1).setOnClickListener(v -> startTopicsActivity("B1"));
            findViewById(R.id.cardB2).setOnClickListener(v -> startTopicsActivity("B2"));
            findViewById(R.id.cardC1).setOnClickListener(v -> startTopicsActivity("C1"));

            }
        private void startTopicsActivity(String level) {
            Intent intent = new Intent(this, TopicsActivity.class);
            intent.putExtra("LEVEL", level);
            startActivity(intent);
        }
    }