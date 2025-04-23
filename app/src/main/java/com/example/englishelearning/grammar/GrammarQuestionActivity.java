package com.example.englishelearning.grammar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.adapter.GrammarQuestionAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GrammarQuestionActivity extends AppCompatActivity {
    RecyclerView questionRecycler;
    GrammarQuestionAdapter adapter;
    List<GrammarQuestion> questionList = new ArrayList<>();
    DatabaseReference grammarRef;
    Button btnSubmit;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_question);

        questionRecycler = findViewById(R.id.questionRecycler);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvResult = findViewById(R.id.tvResult);
        questionRecycler.setLayoutManager(new LinearLayoutManager(this));

        int level = getIntent().getIntExtra("level", 1);
        String topicKey = getIntent().getStringExtra("topicKey");

        if (topicKey == null) {
            Toast.makeText(this, "Error: No topic selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("GrammarQuestionActivity", "Received topicKey: " + topicKey);

        if (level < 1 || level > 5) {
            Toast.makeText(this, "Invalid level selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        grammarRef = FirebaseDatabase.getInstance().getReference("grammar").child(topicKey);
        grammarRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GrammarTopic topic = snapshot.getValue(GrammarTopic.class);
                if (topic != null && topic.getLevels() != null && topic.getLevels().size() > 0) {
                    GrammarLevel selectedLevel = topic.getLevels().get(level - 1);
                    questionList.addAll(selectedLevel.getQuestions());
                    adapter = new GrammarQuestionAdapter(questionList);
                    questionRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GrammarQuestionActivity.this, "No questions found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GrammarQuestionActivity.this, "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (adapter == null || questionList.isEmpty()) {
                Toast.makeText(this, "No questions loaded", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<Integer, String> selectedAnswers = adapter.getSelectedAnswers();
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

            adapter.setSubmitted(true);
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText("Điểm của bạn: " + score + "/" + questionList.size());
            btnSubmit.setEnabled(false);
        });
    }
}