package com.example.englishelearning.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.model.VocabularyWord;

import java.util.ArrayList;
import java.util.List;

public class VocabularyWordAdapter extends RecyclerView.Adapter<VocabularyWordAdapter.WordViewHolder> {
    private List<VocabularyWord> words;
    private OnWordActionListener listener;

    public interface OnWordActionListener {
        void onPlayUKAudio(VocabularyWord word);

        void onPlayUSAudio(VocabularyWord word);

        void onPlayAudio(VocabularyWord word);

        void onWordClick(VocabularyWord word, int position);
    }

    public VocabularyWordAdapter(OnWordActionListener listener) {
        this.words = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vocabulary_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        VocabularyWord word = words.get(position);
        holder.bind(word, listener);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public void setWords(List<VocabularyWord> words) {
        this.words = words;
        notifyDataSetChanged();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWord;
        private TextView tvPronunciation;
        private RatingBar ratingBar;
        private ImageButton btnAudio;
        private ImageButton btnCopy;
        private ImageButton btnFavorite;
        private boolean isFavorite = false;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tv_word);
            tvPronunciation = itemView.findViewById(R.id.tv_pronunciation);

            btnAudio = itemView.findViewById(R.id.btn_audio);
            btnCopy = itemView.findViewById(R.id.btn_copy);

        }

        public void bind(final VocabularyWord word, final OnWordActionListener listener) {
            tvWord.setText(word.getWord());
            String phonetic = word.getPhonetic();
            if (phonetic != null && !phonetic.isEmpty()) {
                tvPronunciation.setVisibility(View.VISIBLE);
                tvPronunciation.setText(phonetic);
            } else {
                tvPronunciation.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWordClick(word, getAdapterPosition());
                }
            });

            btnAudio.setOnClickListener(v -> {
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
