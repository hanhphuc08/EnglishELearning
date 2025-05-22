package com.example.englishelearning.grammar;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.englishelearning.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrammarQuestionActivity extends AppCompatActivity {
    ViewPager2 questionViewPager;
    QuestionPagerAdapter adapter;
    List<GrammarQuestion> questionList = new ArrayList<>();
    DatabaseReference grammarRef;
    FloatingActionButton fabSubmit;
    TextView tvQuestionIndex;
    ImageView btnBack;
    ChipGroup chipGroupQuestions;
    com.google.android.material.button.MaterialButton btnSave;
    android.widget.ProgressBar quizProgressBar;
    Map<Integer, String> selectedAnswers = new HashMap<>();
    boolean isSubmitted = false;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_question);

        questionViewPager = findViewById(R.id.question_view_pager);
        fabSubmit = findViewById(R.id.fab_submit);
        tvQuestionIndex = findViewById(R.id.tv_question_index);
        btnBack = findViewById(R.id.btn_back);
        chipGroupQuestions = findViewById(R.id.chip_group_questions);
        btnSave = findViewById(R.id.btn_save);
        quizProgressBar = findViewById(R.id.quiz_progress_bar);

        int level = getIntent().getIntExtra("level", 1);
        String topicKey = getIntent().getStringExtra("topicKey");

        if (topicKey == null) {
            Toast.makeText(this, "Error: No topic selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (level < 1 || level > 5) {
            Toast.makeText(this, "Invalid level selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load temporary progress
        loadTemporaryProgress(topicKey, level);

        grammarRef = FirebaseDatabase.getInstance().getReference("grammar").child(topicKey);
        grammarRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GrammarTopic topic = snapshot.getValue(GrammarTopic.class);
                if (topic != null && topic.getLevels() != null && topic.getLevels().size() > 0) {
                    GrammarLevel selectedLevel = topic.getLevels().get(level - 1);
                    questionList.addAll(selectedLevel.getQuestions());
                    adapter = new QuestionPagerAdapter(GrammarQuestionActivity.this);
                    questionViewPager.setAdapter(adapter);
                    setupQuestionNavigation();
                    updateProgressAndIndex();
                } else {
                    Toast.makeText(GrammarQuestionActivity.this, "No questions found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GrammarQuestionActivity.this, "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveProgressTemporarily());

        fabSubmit.setOnClickListener(v -> {
            if (adapter == null || questionList.isEmpty()) {
                Toast.makeText(this, "No questions loaded", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedAnswers.size() < questionList.size()) {
                Toast.makeText(this, "Vui lòng trả lời tất cả câu hỏi!", Toast.LENGTH_SHORT).show();
                return;
            }

            int score = 0;
            for (int i = 0; i < questionList.size(); i++) {
                String userAnswer = selectedAnswers.get(i);
                String correctAnswer = questionList.get(i).getAnswer();
                if (userAnswer != null && userAnswer.equals(correctAnswer)) {
                    score++;
                }
            }

            isSubmitted = true;
            adapter.notifyDataSetChanged();
            showResultDialog(score);
            saveProgressToFirebase(score, level, topicKey);
            fabSubmit.setEnabled(false);
        });

        questionViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateProgressAndIndex();
                updateChipSelection(position);
            }
        });
    }

    private class QuestionPagerAdapter extends FragmentStateAdapter {
        public QuestionPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return QuestionFragment.newInstance(questionList.get(position), position, isSubmitted, selectedAnswers);
        }

        @Override
        public int getItemCount() {
            return questionList.size();
        }
    }

    private void setupQuestionNavigation() {
        chipGroupQuestions.removeAllViews();
        for (int i = 0; i < questionList.size(); i++) {
            Chip chip = new Chip(this);
            chip.setText(String.valueOf(i + 1));
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColorResource(R.color.teal_200);
            chip.setTextColor(getResources().getColorStateList(R.color.white));
            final int position = i;
            chip.setOnClickListener(v -> questionViewPager.setCurrentItem(position, true));
            chipGroupQuestions.addView(chip);
        }
    }

    private void updateProgressAndIndex() {
        int currentQuestion = questionViewPager.getCurrentItem() + 1;
        int totalQuestions = questionList.size();
        tvQuestionIndex.setText("Câu hỏi " + currentQuestion + "/" + totalQuestions);
        int progress = (int) ((float) selectedAnswers.size() / totalQuestions * 100);
        quizProgressBar.setProgress(progress);

        ObjectAnimator.ofInt(quizProgressBar, "progress", progress)
                .setDuration(300)
                .start();
    }

    private void updateChipSelection(int position) {
        for (int i = 0; i < chipGroupQuestions.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupQuestions.getChildAt(i);
            chip.setChecked(i == position);
        }
    }

    private void saveProgressTemporarily() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để lưu tiến độ", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference tempProgressRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("temp_progress")
                .child(getIntent().getStringExtra("topicKey"))
                .child(String.valueOf(getIntent().getIntExtra("level", 1)));

        Map<String, String> stringKeyedAnswers = new HashMap<>();
        for (Map.Entry<Integer, String> entry : selectedAnswers.entrySet()) {
            stringKeyedAnswers.put(String.valueOf(entry.getKey()), entry.getValue());
        }

        Map<String, Object> tempProgress = new HashMap<>();
        tempProgress.put("selected_answers", stringKeyedAnswers);
        tempProgressRef.setValue(tempProgress).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Đã lưu tạm " + selectedAnswers.size() + " câu trả lời", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lưu tạm thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadTemporaryProgress(String topicKey, int level) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d("GrammarQuestionActivity", "No user logged in, skipping temp progress load");
            return;
        }

        String userId = user.getUid();
        DatabaseReference tempProgressRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("temp_progress")
                .child(topicKey)
                .child(String.valueOf(level));

        tempProgressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot answersSnapshot = snapshot.child("selected_answers");
                    try {
                        // Try to deserialize as Map<String, String>
                        Map<String, String> savedAnswers = answersSnapshot.getValue(new GenericTypeIndicator<Map<String, String>>() {});
                        if (savedAnswers != null) {
                            selectedAnswers.clear();
                            for (Map.Entry<String, String> entry : savedAnswers.entrySet()) {
                                try {
                                    selectedAnswers.put(Integer.parseInt(entry.getKey()), entry.getValue());
                                } catch (NumberFormatException e) {
                                    Log.e("GrammarQuestionActivity", "Failed to parse key: " + entry.getKey(), e);
                                }
                            }
                        } else {
                            // Fallback: Try to deserialize as List<String>
                            List<String> savedAnswersList = answersSnapshot.getValue(new GenericTypeIndicator<List<String>>() {});
                            if (savedAnswersList != null) {
                                selectedAnswers.clear();
                                for (int i = 0; i < savedAnswersList.size(); i++) {
                                    if (savedAnswersList.get(i) != null) {
                                        selectedAnswers.put(i, savedAnswersList.get(i));
                                    }
                                }
                            }
                        }

                        if (!selectedAnswers.isEmpty() && adapter != null) {
                            adapter.notifyDataSetChanged();
                            updateProgressAndIndex();
                            updateChipSelection(questionViewPager.getCurrentItem());
                            Toast.makeText(GrammarQuestionActivity.this, "Đã khôi phục " + selectedAnswers.size() + " câu trả lời", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("GrammarQuestionActivity", "Failed to deserialize temp progress: " + e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GrammarQuestionActivity", "Failed to load temp progress: " + error.getMessage());
            }
        });
    }

    private void saveProgressToFirebase(int score, int level, String topicKey) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để lưu tiến độ", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference progressRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("progress")
                .child(topicKey)
                .child(String.valueOf(level));

        Map<String, Object> progressData = new HashMap<>();
        progressData.put("score", score);
        progressData.put("total_questions", questionList.size());
        progressData.put("completed", true);

        progressRef.setValue(progressData).addOnSuccessListener(aVoid -> {
            // Delete temporary progress after saving final progress
            DatabaseReference tempProgressRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("temp_progress")
                    .child(topicKey)
                    .child(String.valueOf(level));
            tempProgressRef.removeValue().addOnFailureListener(e -> {
                Log.e("GrammarQuestionActivity", "Failed to delete temp progress: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to save progress: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showResultDialog(int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kết quả");
        builder.setMessage("Điểm của bạn: " + score + "/" + questionList.size() + "\nBạn có muốn xem chi tiết?");
        builder.setPositiveButton("Xem chi tiết", (dialog, which) -> {
            Intent intent = new Intent(this, ResultDetailActivity.class);
            intent.putExtra("question_list", new ArrayList<>(questionList));
            intent.putExtra("selected_answers", new HashMap<>(selectedAnswers));
            startActivity(intent);
        });
        builder.setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}