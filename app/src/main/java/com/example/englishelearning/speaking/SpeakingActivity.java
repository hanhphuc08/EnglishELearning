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

import com.example.englishelearning.R;
import com.example.englishelearning.listening.TopicsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference("users").child(userId); // userId = UID hiện tại của người dùng

        userRef.child("progress").child("speaking").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TextView progressA1Card = findViewById(R.id.progressA1Card);
                TextView progressA2Card = findViewById(R.id.progressA2Card);
                TextView progressB1Card = findViewById(R.id.progressB1Card);
                TextView progressB2Card = findViewById(R.id.progressB2Card);
                TextView progressA1 = findViewById(R.id.progressA1);
                TextView progressA2 = findViewById(R.id.progressA2);
                TextView progressB1 = findViewById(R.id.progressB1);
                TextView progressB2 = findViewById(R.id.progressB2);

                if (snapshot.child("A1").child("levelProgress").exists()) {
                    int a1Progress = snapshot.child("A1").child("levelProgress").getValue(Integer.class);
                    progressA1Card.setText("Progress: " + a1Progress + "%");
                    progressA1.setText("A1: " + a1Progress + "%" );
                }

                if (snapshot.child("A2").child("levelProgress").exists()) {
                    int a2Progress = snapshot.child("A2").child("levelProgress").getValue(Integer.class);
                    progressA2Card.setText("Progress: " + a2Progress + "%");
                    progressA2.setText("A1: " + a2Progress + "%" );
                }

                if (snapshot.child("B1").child("levelProgress").exists()) {
                    int b1Progress = snapshot.child("B1").child("levelProgress").getValue(Integer.class);
                    progressB1Card.setText("Progress: " + b1Progress + "%");
                    progressB1.setText("A1: " + b1Progress + "%" );
                }

                if (snapshot.child("B2").child("levelProgress").exists()) {
                    int b2Progress = snapshot.child("B2").child("levelProgress").getValue(Integer.class);
                    progressB2Card.setText("Progress: " + b2Progress + "%");
                    progressB2.setText("A1: " + b2Progress + "%" );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SpeakingActivity.this, "Failed to load progress", Toast.LENGTH_SHORT).show();
            }
        });

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