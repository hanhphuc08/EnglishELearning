package com.example.englishelearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.model.VocabularyLevel;
import com.example.englishelearning.model.VocabularyTopic;

import java.util.ArrayList;
import java.util.List;

public class VocabularyLevelAdapter extends RecyclerView.Adapter<VocabularyLevelAdapter.LevelViewHolder> {
    private List<VocabularyLevel> levels;
    private OnTopicClickListener listener;

    public VocabularyLevelAdapter(OnTopicClickListener listener) {
        this.levels = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vocabulary_level, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        VocabularyLevel level = levels.get(position);
        holder.bind(level, listener);
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    public void setLevels(List<VocabularyLevel> levels) {
        this.levels = levels;
        notifyDataSetChanged();
    }

    static class LevelViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLevelName;
        private TextView tvTopicCount;
        private ImageView ivLevelIcon;
        private View levelHeader;
        private RecyclerView rvTopics;
        private boolean isExpanded = false;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLevelName = itemView.findViewById(R.id.tv_level_name);
            tvTopicCount = itemView.findViewById(R.id.tv_topic_count);
            ivLevelIcon = itemView.findViewById(R.id.iv_level_icon);
            levelHeader = itemView.findViewById(R.id.level_header);
            rvTopics = itemView.findViewById(R.id.rv_topics);
        }

        public void bind(final VocabularyLevel level, final OnTopicClickListener listener) {
            tvLevelName.setText(level.getLevel());
            int topicCount = level.getTopics() != null ? level.getTopics().size() : 0;
            tvTopicCount.setText(topicCount + " topics");


            rvTopics.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            VocabularyTopicAdapter topicAdapter = new VocabularyTopicAdapter(listener);
            rvTopics.setAdapter(topicAdapter);
            
            if (level.getTopics() != null) {
                topicAdapter.setTopics(level.getTopics());
            }


            levelHeader.setOnClickListener(v -> {
                isExpanded = !isExpanded;
                rvTopics.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                ivLevelIcon.setRotation(isExpanded ? 180 : 0);
            });
        }
    }
} 