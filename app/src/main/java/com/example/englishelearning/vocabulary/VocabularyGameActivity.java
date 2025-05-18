package com.example.englishelearning.vocabulary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.englishelearning.R;
import com.example.englishelearning.model.GameScore;
import com.example.englishelearning.model.VocabularyTopic;
import com.example.englishelearning.model.VocabularyWord;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class VocabularyGameActivity extends AppCompatActivity {

    private static final String EXTRA_TOPIC = "extra_topic";
    
    private TextView tvScore;
    private TextView tvWord;
    private MaterialButton btnOption1;
    private MaterialButton btnOption2;
    private MaterialButton btnOption3;
    private MaterialButton btnOption4;
    private MaterialButton btnNext;
    private CardView cardWord;
    
    private VocabularyTopic topic;
    private List<VocabularyWord> allWords;
    private List<VocabularyWord> currentWords;
    private int currentWordIndex = 0;
    private int score = 0;
    private boolean isAnswered = false;
    private Random random = new Random();
    private MediaPlayer correctPlayer;
    private MediaPlayer wrongPlayer;

    public static Intent newIntent(Context context, VocabularyTopic topic) {
        Intent intent = new Intent(context, VocabularyGameActivity.class);
        intent.putExtra(EXTRA_TOPIC, topic);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_game);

        topic = (VocabularyTopic) getIntent().getSerializableExtra(EXTRA_TOPIC);
        if (topic == null || topic.getWords() == null || topic.getWords().isEmpty()) {
            Toast.makeText(this, "No words available for this topic", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        allWords = new ArrayList<>(topic.getWords());
        Collections.shuffle(allWords);
        
        correctPlayer = MediaPlayer.create(this, R.raw.correct);
        wrongPlayer = MediaPlayer.create(this, R.raw.error);
        
        setupViews();
        setupClickListeners();
        loadWords();
        showNextWord();
    }

    private void setupViews() {
        tvScore = findViewById(R.id.tv_score);
        tvWord = findViewById(R.id.tv_word);
        btnOption1 = findViewById(R.id.btn_option1);
        btnOption2 = findViewById(R.id.btn_option2);
        btnOption3 = findViewById(R.id.btn_option3);
        btnOption4 = findViewById(R.id.btn_option4);
        btnNext = findViewById(R.id.btn_next);
        cardWord = findViewById(R.id.card_word);
        btnNext.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        btnOption1.setOnClickListener(v -> checkAnswer(btnOption1));
        btnOption2.setOnClickListener(v -> checkAnswer(btnOption2));
        btnOption3.setOnClickListener(v -> checkAnswer(btnOption3));
        btnOption4.setOnClickListener(v -> checkAnswer(btnOption4));
        btnNext.setOnClickListener(v -> {
            if (currentWordIndex < currentWords.size() - 1) {
                currentWordIndex++;
                showNextWord();
            } else {
                showGameOver();
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void loadWords() {
        currentWords = new ArrayList<>(allWords);
        Collections.shuffle(currentWords);
    }
    private void showNextWord() {
        if (currentWordIndex >= currentWords.size()) {
            showGameOver();
            return;
        }
        VocabularyWord currentWord = currentWords.get(currentWordIndex);
        tvWord.setText(currentWord.getWord());
        List<String> answers = new ArrayList<>();
        answers.add(currentWord.getMeaning());
        while (answers.size() < 4) {
            int randomIndex = random.nextInt(allWords.size());
            String randomMeaning = allWords.get(randomIndex).getMeaning();
            if (!answers.contains(randomMeaning)) {
                answers.add(randomMeaning);
            }
        }
        Collections.shuffle(answers);
        btnOption1.setText(answers.get(0));
        btnOption2.setText(answers.get(1));
        btnOption3.setText(answers.get(2));
        btnOption4.setText(answers.get(3));
        resetButtonColors();
        isAnswered = false;
        btnNext.setVisibility(View.GONE);
        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        cardWord.startAnimation(scaleUp);
    }

    private void checkAnswer(MaterialButton selectedButton) {
        if (isAnswered) return;
        isAnswered = true;

        VocabularyWord currentWord = currentWords.get(currentWordIndex);
        String correctAnswer = currentWord.getMeaning();
        Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        selectedButton.startAnimation(scaleDown);

        if (selectedButton.getText().toString().equals(correctAnswer)) {
            selectedButton.setBackgroundTintList(getColorStateList(R.color.correct_answer));
            score += 10;
            tvScore.setText("Score: " + score);
            Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
            cardWord.startAnimation(scaleUp);
            if (correctPlayer != null) correctPlayer.start();
        } else {
            selectedButton.setBackgroundTintList(getColorStateList(R.color.error));
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            cardWord.startAnimation(shake);
            if (wrongPlayer != null) wrongPlayer.start();
            if (btnOption1.getText().toString().equals(correctAnswer)) {
                btnOption1.setBackgroundTintList(getColorStateList(R.color.correct_answer));
            } else if (btnOption2.getText().toString().equals(correctAnswer)) {
                btnOption2.setBackgroundTintList(getColorStateList(R.color.correct_answer));
            } else if (btnOption3.getText().toString().equals(correctAnswer)) {
                btnOption3.setBackgroundTintList(getColorStateList(R.color.correct_answer));
            } else if (btnOption4.getText().toString().equals(correctAnswer)) {
                btnOption4.setBackgroundTintList(getColorStateList(R.color.correct_answer));
            }
        }
        selectedButton.postDelayed(() -> {
            btnNext.setVisibility(View.VISIBLE);
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            btnNext.startAnimation(fadeIn);
        }, 1000);
    }

    private void resetButtonColors() {
        btnOption1.setBackgroundTintList(getColorStateList(R.color.primary));
        btnOption2.setBackgroundTintList(getColorStateList(R.color.primary));
        btnOption3.setBackgroundTintList(getColorStateList(R.color.primary));
        btnOption4.setBackgroundTintList(getColorStateList(R.color.primary));
    }

    private void showGameOver() {
        String message = "Game Over!\nYour score: " + score;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        saveScore();
        
        finish();
    }

    private void saveScore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId == null) return;

        GameScore gameScore = new GameScore(
            userId,
            String.valueOf(topic.getId()),
            topic.getTopic(),
            score
        );

        DatabaseReference scoresRef = FirebaseDatabase.getInstance()
            .getReference("game_scores")
            .child(userId)
            .push();

        scoresRef.setValue(gameScore)
            .addOnSuccessListener(aVoid -> {
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, 
                    "Error saving score: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (correctPlayer != null) {
            correctPlayer.release();
        }
        if (wrongPlayer != null) {
            wrongPlayer.release();
        }
    }
} 