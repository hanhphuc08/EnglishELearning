package com.example.englishelearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.model.Topic;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder>{

    private List<Topic> topics;
    private OnTopicClickListener listener;

    public TopicAdapter(List<Topic> topics, OnTopicClickListener listener) {
        this.topics = topics;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Topic topic = topics.get(position);
        holder.topicTitle.setText(topic.title);
        holder.itemView.setOnClickListener(v -> listener.onTopicClick(topic));
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView topicTitle;

        ViewHolder(View itemView) {
            super(itemView);
            topicTitle = itemView.findViewById(R.id.topicTitle);
        }
    }

    public interface OnTopicClickListener {
        void onTopicClick(Topic topic);
    }
}
