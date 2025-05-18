package com.example.englishelearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.model.GameScore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GameScoreAdapter extends RecyclerView.Adapter<GameScoreAdapter.ScoreViewHolder> {
    private List<GameScore> scores;
    private SimpleDateFormat dateFormat;

    public GameScoreAdapter(List<GameScore> scores) {
        this.scores = scores;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        GameScore score = scores.get(position);
        holder.tvTopicName.setText(score.getTopicName());
        holder.tvScore.setText("Score: " + score.getScore());
        holder.tvDate.setText(dateFormat.format(score.getPlayedAt()));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public void updateScores(List<GameScore> newScores) {
        this.scores = newScores;
        notifyDataSetChanged();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName;
        TextView tvScore;
        TextView tvDate;

        ScoreViewHolder(View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tv_topic_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
} 