package com.example.englishelearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.model.VocabularyTopic;

import java.util.ArrayList;
import java.util.List;

public class VocabularyTopicAdapter extends RecyclerView.Adapter<VocabularyTopicAdapter.TopicViewHolder> {
    private List<VocabularyTopic> topics;
    private OnTopicClickListener listener;

    public VocabularyTopicAdapter(OnTopicClickListener listener) {
        this.topics = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vocabulary_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        VocabularyTopic topic = topics.get(position);
        holder.bind(topic, listener);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void setTopics(List<VocabularyTopic> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTopicName;
        private TextView tvWordCount;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tv_topic_name);
            tvWordCount = itemView.findViewById(R.id.tv_word_count);
        }

        public void bind(final VocabularyTopic topic, final OnTopicClickListener listener) {
            tvTopicName.setText(topic.getTopic());
            int wordCount = topic.getWords() != null ? topic.getWords().size() : 0;
            tvWordCount.setText(wordCount + " words");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTopicClick(topic);
                }
            });
        }
    }
} 