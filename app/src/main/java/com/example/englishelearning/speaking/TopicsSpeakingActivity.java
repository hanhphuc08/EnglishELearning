package com.example.englishelearning.speaking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
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

        loadTopicsAndProgress();
    }

    private void loadTopicsAndProgress() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tham chiếu đến đường dẫn topics
        DatabaseReference topicsRef = mDatabase.child(level).child("topics");

        // Tham chiếu đến đường dẫn progress của user
        DatabaseReference userProgressRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("progress")
                .child("speaking")
                .child(level)
                .child("topics");

        // Đầu tiên, lấy thông tin về topics
        topicsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topicSpeakingList.clear();

                if (dataSnapshot.exists()) {
                    // Lưu trữ danh sách các topic đã tải để xử lý sau
                    final List<TopicSpeaking> loadedTopics = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TopicSpeaking topicSpeaking = snapshot.getValue(TopicSpeaking.class);
                        if (topicSpeaking != null) {
                            // Đặt giá trị mặc định cho progress là 0
                            topicSpeaking.setProgress(0);
                            loadedTopics.add(topicSpeaking);
                        }
                    }

                    // Sau đó, tải progress của user
                    userProgressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot progressSnapshot) {
                            // Thêm tất cả topic vào danh sách
                            topicSpeakingList.addAll(loadedTopics);

                            // Áp dụng progress cho từng topic nếu có
                            for (TopicSpeaking topic : topicSpeakingList) {
                                // +1 vì mảng trong Firebase bắt đầu từ 0
                                // nhưng id của topic có thể bắt đầu từ 1
                                DataSnapshot topicProgress = progressSnapshot.child(String.valueOf(topic.getId()));

                                if (topicProgress.exists() && topicProgress.getValue() != null) {
                                    try {
                                        Long progressValue = topicProgress.getValue(Long.class);
                                        if (progressValue != null) {
                                            topic.setProgress(progressValue.intValue());
                                        }
                                    } catch (Exception e) {
                                        Log.e("TopicsSpeakingActivity", "Error converting progress", e);
                                    }
                                }
                            }

                            // Cập nhật giao diện
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(TopicsSpeakingActivity.this,
                                    "Error loading progress",
                                    Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TopicsSpeakingActivity.this,
                        "Error loading topics",
                        Toast.LENGTH_SHORT).show();
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