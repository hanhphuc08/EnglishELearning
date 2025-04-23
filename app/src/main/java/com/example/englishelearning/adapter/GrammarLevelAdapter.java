package com.example.englishelearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.grammar.GrammarLevel;

import java.util.List;

public class GrammarLevelAdapter extends RecyclerView.Adapter<GrammarLevelAdapter.LevelViewHolder> {
    private final List<GrammarLevel> levelList;
    private final OnLevelClickListener listener;

    public interface OnLevelClickListener {
        void onLevelClick(GrammarLevel level);
    }

    public GrammarLevelAdapter(List<GrammarLevel> levelList, OnLevelClickListener listener) {
        this.levelList = levelList;
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
        holder.itemView.setOnClickListener(v -> listener.onLevelClick(level));
    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }

    public static class LevelViewHolder extends RecyclerView.ViewHolder {
        TextView levelTitle;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            levelTitle = itemView.findViewById(R.id.levelTitle);
        }
    }
}
