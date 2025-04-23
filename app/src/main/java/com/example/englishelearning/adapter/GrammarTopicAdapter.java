package com.example.englishelearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.grammar.GrammarTopic;

import java.util.List;

public class GrammarTopicAdapter extends RecyclerView.Adapter<GrammarTopicAdapter.TopicViewHolder> {

    private final List<GrammarTopic> topicList;
    private final OnTopicClickListener listener;

    // Define interface for topic click
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
        // Lấy tên thì từ key và định dạng lại
        String tenseName = formatTenseName(topic.getKey());
        holder.title.setText(tenseName); // Hiển thị tên thì (tense name)

        holder.itemView.setOnClickListener(v -> {
            listener.onTopicClick(topic); // Gọi listener khi click vào topic
        });
    }

    // Hàm định dạng tên thì (future_continuous -> Future Continuous)
    private String formatTenseName(String key) {
        if (key == null || key.isEmpty()) {
            return "Unknown Tense";
        }
        // Thay dấu gạch dưới bằng khoảng trắng và viết hoa chữ cái đầu mỗi từ
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
        TextView title;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.topicTitle);
        }
    }
}