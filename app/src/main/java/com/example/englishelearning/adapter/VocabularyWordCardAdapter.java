package com.example.englishelearning.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.englishelearning.R;
import com.example.englishelearning.model.VocabularyWord;
import com.example.englishelearning.vocabulary.VocabularyWordCardActivity;

import java.util.ArrayList;
import java.util.List;

public class VocabularyWordCardAdapter extends RecyclerView.Adapter<VocabularyWordCardAdapter.WordCardViewHolder> {
    private List<VocabularyWord> words;
    private OnWordCardActionListener listener;

    public interface OnWordCardActionListener {
        void onPlayUKAudio(VocabularyWord word);
        void onPlayUSAudio(VocabularyWord word);
        void onPlayAudio(VocabularyWord word);
    }

    public VocabularyWordCardAdapter(OnWordCardActionListener listener) {
        this.words = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public WordCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word_card, parent, false);
        return new WordCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordCardViewHolder holder, int position) {
        VocabularyWord word = words.get(position);
        holder.bind(word, listener, getItemCount());
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public void setWords(List<VocabularyWord> words) {
        this.words = words;
        notifyDataSetChanged();
    }

    static class WordCardViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWord;
        private TextView tvPronunciation;
        private TextView tvMeaning;
        private TextView tvExample;
        private ImageButton btnUKAudio;
        private ImageButton btnUSAudio;
        private ImageButton btnPlay;
        private ImageButton btnCopy;
        private ImageButton btnBack;

        public WordCardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tv_word);
            tvPronunciation = itemView.findViewById(R.id.tv_pronunciation);
            tvMeaning = itemView.findViewById(R.id.tv_meaning);
            tvExample = itemView.findViewById(R.id.tv_example);
            btnUKAudio = itemView.findViewById(R.id.btn_uk_audio);
            btnUSAudio = itemView.findViewById(R.id.btn_us_audio);
            btnPlay = itemView.findViewById(R.id.btn_play);
            btnCopy = itemView.findViewById(R.id.btn_copy);
            btnBack = itemView.findViewById(R.id.btn_back);
        }

        public void bind(final VocabularyWord word, final OnWordCardActionListener listener, int totalCards) {
            btnBack.setOnClickListener(v -> {
                if (itemView.getContext() instanceof VocabularyWordCardActivity) {
                    ((VocabularyWordCardActivity) itemView.getContext()).finish();
                }
            });

            tvWord.setText(word.getWord());
            String phonetic = word.getPhonetic();
            if (phonetic != null && !phonetic.isEmpty()) {
                tvPronunciation.setVisibility(View.VISIBLE);
                tvPronunciation.setText(phonetic);
            } else {
                tvPronunciation.setVisibility(View.GONE);
            }

            String meaning = word.getMeaning();
            if (meaning != null && !meaning.isEmpty()) {
                tvMeaning.setVisibility(View.VISIBLE);
                tvMeaning.setText(meaning);
            } else {
                tvMeaning.setVisibility(View.GONE);
            }

            String example = word.getExample();
            if (example != null && !example.isEmpty()) {
                tvExample.setVisibility(View.VISIBLE);
                tvExample.setText(example);
            } else {
                tvExample.setVisibility(View.GONE);
            }

            btnUKAudio.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlayUKAudio(word);
                }
            });

            btnUSAudio.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlayUSAudio(word);
                }
            });

            btnPlay.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlayAudio(word);
                }
            });

            btnCopy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) itemView.getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("word", word.getWord());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(itemView.getContext(), "Word copied", Toast.LENGTH_SHORT).show();
            });
        }
    }
} 