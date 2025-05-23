package com.example.englishelearning.speaking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.englishelearning.R;
import com.example.englishelearning.adapter.KeyPhraseAdapter;
import com.example.englishelearning.model.TopicSpeaking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class TestSpeakingActivity extends AppCompatActivity {
    private ArrayList<String> keyPhrases;
    private TextToSpeech textToSpeech;
    private int currentIndex = 0;
    private ActivityResultLauncher<Intent> speechRecognizerLauncher;
    private ArrayList<Boolean> isCorrectList = new ArrayList<>();
    private Button finishTestButton;
    private String level;
    private TopicSpeaking topicSpeaking;
    private ViewPager2 phrasesViewPager;
    private KeyPhraseAdapter keyPhraseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_speaking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        phrasesViewPager = findViewById(R.id.phrasesViewPager);

        keyPhrases = getIntent().getStringArrayListExtra("KEY_PHRASES");
        level = getIntent().getStringExtra("LEVEL");
        topicSpeaking = (TopicSpeaking) getIntent().getSerializableExtra("TOPIC");

        if (keyPhrases == null) {
            Toast.makeText(this, "No key phrases found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        for (int i = 0; i < keyPhrases.size(); i++) {
            isCorrectList.add(false); // mặc định là sai
        }

        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        speechRecognizerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (matches != null && !matches.isEmpty()) {
                            handleUserSpeech(matches.get(0));
                        }
                    }
                }
        );

        keyPhraseAdapter = new KeyPhraseAdapter(keyPhrases, this::speakPhrase, this::startSpeechRecordingForItem);
        phrasesViewPager.setAdapter(keyPhraseAdapter);

        // Xử lý khi chuyển trang ViewPager2
        phrasesViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentIndex = position; // Cập nhật currentIndex
                if (position == keyPhrases.size() - 1) {
                    finishTestButton.setVisibility(View.VISIBLE);
                } else {
                    finishTestButton.setVisibility(View.GONE);
                }
            }
        });

        finishTestButton = findViewById(R.id.finishTestButton);
        finishTestButton.setOnClickListener(v -> {
            int correctCount = 0;
            for (boolean isCorrect : isCorrectList) {
                if (isCorrect) correctCount++;
            }
            int totalQuestions = keyPhrases.size();
            int topicProgress = (correctCount * 100) / totalQuestions;

            updateLevelProgress("speaking", topicProgress);

            Intent intent = new Intent(TestSpeakingActivity.this, ResultActivity.class);
            intent.putExtra("CORRECT_COUNT", correctCount);
            intent.putExtra("TOTAL_QUESTIONS", totalQuestions);
            intent.putExtra("TOPIC_PROGRESS", topicProgress);
            intent.putExtra("LEVEL", level); // nếu có
            intent.putExtra("TOPIC", topicSpeaking);

            startActivity(intent);
            finish();
        });
    }

    private void startSpeechRecordingForItem(int position) {
        // currentIndex đã được cập nhật bởi onPageSelected của ViewPager2
        // Hoặc có thể truyền trực tiếp position vào đây
        // currentIndex = position; // Đảm bảo currentIndex là đúng trước khi gọi startSpeechRecognizer
        if (phrasesViewPager.getCurrentItem() == position) { // Chỉ xử lý nếu là item hiện tại
            startSpeechRecognizer(); // Hàm này vẫn dùng currentIndex nội bộ
        }
    }

    private void updateSpeakingLevelProgress(String userId, String level) {
        DatabaseReference topicRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("progress")
                .child("speaking")
                .child(level)
                .child("topics");

        topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalProgress = 0;
                int count = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    Long progress = child.getValue(Long.class);
                    if (progress != null) {
                        totalProgress += progress;
                        count++;
                    }
                }

                long levelProgress = (count > 0) ? (totalProgress / count) : 0;

                // Luôn set giá trị levelProgress, nếu chưa có topic thì là 0
                FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)
                        .child("progress")
                        .child("speaking")
                        .child(level)
                        .child("levelProgress")
                        .setValue(levelProgress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TestSpeakingActivity", "Error updating level progress", error.toException());
            }
        });
    }

    private void updateLevelProgress(String skill, int topicProgress) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        String topicId = String.valueOf(topicSpeaking.id);
        String levelKey = level;

        DatabaseReference topicRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("progress")
                .child(skill)
                .child(levelKey)
                .child("topics")
                .child(topicId);

        topicRef.setValue(topicProgress).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (skill.equals("speaking")) {
                    updateSpeakingLevelProgress(userId, levelKey);
                }
                // Nếu sau này mở rộng listening/reading thì thêm else if ở đây
            } else {
                Log.e("updateLevelProgress", "Failed to update topic progress", task.getException());
            }
        });
    }

    private void speakPhrase(String phrase) {
        textToSpeech.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    private void startSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizerLauncher.launch(intent);
    }

    private void handleUserSpeech(String userSpeech) {
        if (currentIndex < 0 || currentIndex >= keyPhrases.size()) return;

        String expected = keyPhrases.get(currentIndex);
        int similarity = calculateSimilarity(expected, userSpeech);

        if (similarity >= 80) { // Đúng từ 80% trở lên
            keyPhraseAdapter.updateItemStatus(currentIndex, true); // true là đúng
            Toast.makeText(this, "Good job! Similarity: " + similarity + "%", Toast.LENGTH_SHORT).show();
            isCorrectList.set(currentIndex, true);

        } else {
            keyPhraseAdapter.updateItemStatus(currentIndex, false); // false là sai
            Toast.makeText(this, "Try again! Similarity: " + similarity + "%", Toast.LENGTH_SHORT).show();
        }
    }

    private int calculateSimilarity(String s1, String s2) {
        s1 = s1.toLowerCase().trim();
        s2 = s2.toLowerCase().trim();

        int distance = levenshtein(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());

        if (maxLength == 0) return 100;
        return (100 * (maxLength - distance)) / maxLength;
    }

    private int levenshtein(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else {
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost
                    );
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}