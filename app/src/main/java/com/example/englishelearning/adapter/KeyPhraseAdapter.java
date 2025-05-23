package com.example.englishelearning.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;

import java.util.ArrayList;
import java.util.Collections;

public class KeyPhraseAdapter extends RecyclerView.Adapter<KeyPhraseAdapter.KeyPhraseViewHolder>{

    private ArrayList<String> phrases;
    private ArrayList<Boolean> itemCorrectStatus; // Để lưu trạng thái đúng/sai của từng item
    private OnPlayButtonClickListener playListener;
    private OnRecordButtonClickListener recordListener;
    private Context context; // Cần context để parse color

    public interface OnPlayButtonClickListener {
        void onPlayClicked(String phrase);
    }

    public interface OnRecordButtonClickListener {
        void onRecordClicked(int position);
    }

    public KeyPhraseAdapter(ArrayList<String> phrases, OnPlayButtonClickListener playListener, OnRecordButtonClickListener recordListener) {
        this.phrases = phrases;
        this.playListener = playListener;
        this.recordListener = recordListener;
        this.itemCorrectStatus = new ArrayList<>(Collections.nCopies(phrases.size(), null)); // null: chưa trả lời, true: đúng, false: sai
    }

    @NonNull
    @Override
    public KeyPhraseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext(); // Lấy context
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_key_phrase, parent, false);
        return new KeyPhraseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyPhraseViewHolder holder, int position) {
        String phrase = phrases.get(position);
        holder.phraseTextView.setText(phrase);

        holder.playButton.setOnClickListener(v -> playListener.onPlayClicked(phrase));
        holder.recordButton.setOnClickListener(v -> recordListener.onRecordClicked(holder.getAdapterPosition()));

        // Set background color based on status
        Boolean status = itemCorrectStatus.get(position);
        if (status == null) { // Chưa trả lời hoặc chưa có kết quả
            holder.cardContentLayout.setBackgroundColor(Color.TRANSPARENT); // Hoặc màu default của card
        } else if (status) { // Đúng
            holder.cardContentLayout.setBackgroundColor(Color.parseColor("#C8E6C9")); // Xanh lá nhạt
        } else { // Sai
            holder.cardContentLayout.setBackgroundColor(Color.parseColor("#FFCDD2")); // Đỏ nhạt
        }
    }

    @Override
    public int getItemCount() {
        return phrases.size();
    }

    // Phương thức để activity cập nhật trạng thái và màu sắc
    public void updateItemStatus(int position, boolean isCorrect) {
        if (position >= 0 && position < itemCorrectStatus.size()) {
            itemCorrectStatus.set(position, isCorrect);
            notifyItemChanged(position); // Quan trọng: Yêu cầu RecyclerView vẽ lại item này
        }
    }

    // Phương thức để reset trạng thái (nếu cần)
    public void resetItemStatus(int position) {
        if (position >= 0 && position < itemCorrectStatus.size()) {
            itemCorrectStatus.set(position, null);
            notifyItemChanged(position);
        }
    }

    static class KeyPhraseViewHolder extends RecyclerView.ViewHolder {
        TextView phraseTextView;
        ImageButton playButton;
        ImageButton recordButton;
        View cardContentLayout; // View để đổi màu nền

        KeyPhraseViewHolder(View itemView) {
            super(itemView);
            phraseTextView = itemView.findViewById(R.id.phraseTextView);
            playButton = itemView.findViewById(R.id.playButton);
            recordButton = itemView.findViewById(R.id.recordButton);
            cardContentLayout = itemView.findViewById(R.id.cardContentLayout); // ID của layout trong card để đổi màu
        }
    }
}
