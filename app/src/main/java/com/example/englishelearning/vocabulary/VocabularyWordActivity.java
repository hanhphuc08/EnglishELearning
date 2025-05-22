package com.example.englishelearning.vocabulary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.adapter.VocabularyWordAdapter;
import com.example.englishelearning.model.VocabularyTopic;
import com.example.englishelearning.model.VocabularyWord;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class VocabularyWordActivity extends AppCompatActivity implements VocabularyWordAdapter.OnWordActionListener {

    private static final String EXTRA_TOPIC = "extra_topic";
    private static final int PERMISSION_REQUEST_CODE = 123;

    private RecyclerView recyclerView;
    private VocabularyWordAdapter adapter;
    private ImageView btnBack;
    private TextView tvTitle;
    private VocabularyTopic topic;
    
    private TextToSpeech ttsUK;
    private TextToSpeech ttsUS;
    private boolean isTTSReady = false;

    public static Intent newIntent(Context context, VocabularyTopic topic) {
        Intent intent = new Intent(context, VocabularyWordActivity.class);
        intent.putExtra(EXTRA_TOPIC, topic);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_word);

        topic = (VocabularyTopic) getIntent().getSerializableExtra(EXTRA_TOPIC);
        if (topic == null) {
            Toast.makeText(this, "Error loading topic data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        checkPermissions();
        setupViews();

        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_title);

        tvTitle.setText(topic.getTopic());

        btnBack.setOnClickListener(v -> finish());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        } else {
            setupTextToSpeech();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupTextToSpeech();
            } else {
                Toast.makeText(this, "Permission needed for text-to-speech", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupTextToSpeech() {
        ttsUK = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = ttsUK.setLanguage(Locale.UK);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "UK English TTS not available", Toast.LENGTH_SHORT).show();
                } else {
                    isTTSReady = true;
                }
            } else {
                Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        ttsUS = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = ttsUS.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "US English TTS not available", Toast.LENGTH_SHORT).show();
                } else {
                    isTTSReady = true;
                }
            } else {
                Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.wordRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VocabularyWordAdapter(this);
        recyclerView.setAdapter(adapter);

        if (topic.getWords() != null) {
            adapter.setWords(topic.getWords());
        }

        MaterialButton btnPlayGame = findViewById(R.id.btn_play_game);
        btnPlayGame.setOnClickListener(v -> {
            startActivity(VocabularyGameActivity.newIntent(this, topic));
        });
    }

    private void speakWord(TextToSpeech tts, String word) {
        if (tts != null && isTTSReady) {
            tts.setSpeechRate(0.8f);
            tts.setPitch(1.0f);
            int result = tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
            
            if (result == TextToSpeech.ERROR) {
                Toast.makeText(this, "Error in TTS", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Text-to-speech not ready", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPlayUKAudio(VocabularyWord word) {
        speakWord(ttsUK, word.getWord());
    }

    @Override
    public void onPlayUSAudio(VocabularyWord word) {
        speakWord(ttsUS, word.getWord());
    }

    @Override
    public void onPlayAudio(VocabularyWord word) {
        speakWord(ttsUS, word.getWord());
    }

    @Override
    public void onWordClick(VocabularyWord word, int position) {
        startActivity(VocabularyWordCardActivity.newIntent(this, topic, position));
    }

    @Override
    protected void onDestroy() {
        if (ttsUK != null) {
            ttsUK.stop();
            ttsUK.shutdown();
        }
        if (ttsUS != null) {
            ttsUS.stop();
            ttsUS.shutdown();
        }
        super.onDestroy();
    }
}
