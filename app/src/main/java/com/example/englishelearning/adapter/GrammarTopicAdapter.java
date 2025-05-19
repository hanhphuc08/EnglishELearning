package com.example.englishelearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.grammar.GrammarTopic;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GrammarTopicAdapter extends RecyclerView.Adapter<GrammarTopicAdapter.TopicViewHolder> {

    private final List<GrammarTopic> topicList;
    private final OnTopicClickListener listener;

    public interface OnTopicClickListener {
        void onTopicClick(GrammarTopic topic);
    }

    public GrammarTopicAdapter(List<GrammarTopic> topicList, OnTopicClickListener listener) {
        this.topicList = topicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grammar_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        GrammarTopic topic = topicList.get(position);
        String tenseName = formatTenseName(topic.getKey());
        holder.title.setText(tenseName);

        // Load progress from Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference progressRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("progress")
                .child(topic.getKey());

        progressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalProgress = 0;
                int levelCount = topic.getLevels() != null ? topic.getLevels().size() : 0;
                if (levelCount == 0) {
                    holder.progressBar.setProgress(0);
                    holder.progressText.setText("Progress: 0%");
                    return;
                }

                for (DataSnapshot levelSnap : snapshot.getChildren()) {
                    Integer score = levelSnap.child("score").getValue(Integer.class);
                    Integer totalQuestions = levelSnap.child("total_questions").getValue(Integer.class);
                    if (score != null && totalQuestions != null && totalQuestions > 0) {
                        totalProgress += (float) score / totalQuestions * 100;
                    }
                }

                int averageProgress = levelCount > 0 ? Math.round(totalProgress / levelCount) : 0;
                holder.progressBar.setProgress(averageProgress);
                holder.progressText.setText("Progress: " + averageProgress + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.progressBar.setProgress(0);
                holder.progressText.setText("Progress: 0%");
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onTopicClick(topic));
    }

    private String formatTenseName(String key) {
        if (key == null || key.isEmpty()) {
            return "Unknown Tense";
        }
        String[] words = key.split("_");
        StringBuilder formattedName = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                formattedName.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return formattedName.toString().trim();
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView title, progressText;
        ProgressBar progressBar;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.topicTitle);
            progressBar = itemView.findViewById(R.id.topicProgressBar);
            progressText = itemView.findViewById(R.id.topicProgressText);
        }
    }
}