package com.example.englishelearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.model.Topic;
import com.example.englishelearning.model.TopicSpeaking;

import java.util.List;

public class TopicSpeakingAdapter extends RecyclerView.Adapter<TopicSpeakingAdapter.ViewHolder> {
    private List<TopicSpeaking> topicSpeakings;
    private OnTopicClickListener listener;

    public TopicSpeakingAdapter(List<TopicSpeaking> topicSpeakings, OnTopicClickListener listener) {
        this.topicSpeakings = topicSpeakings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopicSpeaking topicSpeaking = topicSpeakings.get(position);
        holder.topicTitle.setText(topicSpeaking.title);
        holder.itemView.setOnClickListener(v -> listener.onTopicClick(topicSpeaking));
    }

    @Override
    public int getItemCount() {
        return topicSpeakings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView topicTitle;

        ViewHolder(View itemView) {
            super(itemView);
            topicTitle = itemView.findViewById(R.id.topicTitle);
        }
    }
    public interface OnTopicClickListener {
        void onTopicClick(TopicSpeaking topicSpeaking);
    }
}
