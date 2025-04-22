package com.example.englishelearning.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.model.Topic;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder>{

    private List<Topic> topics;
    private OnTopicClickListener listener;
    private String level;

    public TopicAdapter(List<Topic> topics, OnTopicClickListener listener, String level) {
        this.topics = topics;
        this.listener = listener;
        this.level = level;
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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .child("progress").child("listening").child(level).child("topics")
                .child(String.valueOf(topic.id)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long progress = snapshot.getValue(Long.class);
                        if (progress != null && progress == 100) {
                            holder.topicProgress.setTextColor(Color.parseColor("#4CAF50"));
                        } else {
                            holder.topicProgress.setTextColor(Color.parseColor("#757575"));
                        }
                        holder.topicProgress.setText("Progress: " + (progress != null ? progress + "%" : "0%"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        holder.topicProgress.setText("Progress: 0%");
                        android.util.Log.e("TopicAdapter", "Error loading progress for topic " + topic.id + ": " + databaseError.getMessage());
                    }
                });
        holder.itemView.setOnClickListener(v -> listener.onTopicClick(topic));
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView topicTitle, topicProgress;
        ImageView topicIcon;

        ViewHolder(View itemView) {
            super(itemView);
            topicTitle = itemView.findViewById(R.id.topicTitle);
            topicProgress = itemView.findViewById(R.id.topicProgress);
            topicIcon = itemView.findViewById(R.id.topicIcon);
        }
    }

    public interface OnTopicClickListener {
        void onTopicClick(Topic topic);
    }
}
