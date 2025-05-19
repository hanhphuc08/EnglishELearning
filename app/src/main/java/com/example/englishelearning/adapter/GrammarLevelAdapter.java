package com.example.englishelearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.grammar.GrammarLevel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GrammarLevelAdapter extends RecyclerView.Adapter<GrammarLevelAdapter.LevelViewHolder> {
    private final List<GrammarLevel> levelList;
    private final OnLevelClickListener listener;
    private final String topicKey;

    public interface OnLevelClickListener {
        void onLevelClick(GrammarLevel level);
    }

    public GrammarLevelAdapter(List<GrammarLevel> levelList, String topicKey, OnLevelClickListener listener) {
        this.levelList = levelList;
        this.topicKey = topicKey;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        GrammarLevel level = levelList.get(position);
        holder.levelTitle.setText("Level " + level.getLevel());

        // Load progress from Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference progressRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("progress")
                .child(topicKey)
                .child(String.valueOf(level.getLevel()));

        progressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer score = snapshot.child("score").getValue(Integer.class);
                Integer totalQuestions = snapshot.child("total_questions").getValue(Integer.class);
                int progress = 0;
                if (score != null && totalQuestions != null && totalQuestions > 0) {
                    progress = Math.round((float) score / totalQuestions * 100);
                }
                holder.progressBar.setProgress(progress);
                holder.progressText.setText("Progress: " + progress + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.progressBar.setProgress(0);
                holder.progressText.setText("Progress: 0%");
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onLevelClick(level));
    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }

    public static class LevelViewHolder extends RecyclerView.ViewHolder {
        TextView levelTitle, progressText;
        ProgressBar progressBar;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            levelTitle = itemView.findViewById(R.id.levelTitle);
            progressBar = itemView.findViewById(R.id.levelProgressBar);
            progressText = itemView.findViewById(R.id.levelProgressText);
        }
    }
}