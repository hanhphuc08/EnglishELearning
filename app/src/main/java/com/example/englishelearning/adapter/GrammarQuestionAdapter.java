package com.example.englishelearning.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.grammar.GrammarQuestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrammarQuestionAdapter extends RecyclerView.Adapter<GrammarQuestionAdapter.QuestionViewHolder> {
    private final List<GrammarQuestion> questionList;
    private final Map<Integer, String> selectedAnswers = new HashMap<>();
    private boolean isSubmitted = false;

    public GrammarQuestionAdapter(List<GrammarQuestion> questionList) {
        this.questionList = questionList;
    }

    public void setSubmitted(boolean submitted) {
        this.isSubmitted = submitted;
        notifyDataSetChanged();
    }

    public Map<Integer, String> getSelectedAnswers() {
        return selectedAnswers;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grammar_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        GrammarQuestion question = questionList.get(position);
        holder.passageText.setText((position + 1) + ". " + question.getPassage());
        holder.questionText.setText(question.getQuestion());

        List<String> options = question.getOptions();
        holder.optionButtons.clear();
        holder.optionsLayout.removeAllViews();

        for (String option : options) {
            RadioButton radio = new RadioButton(holder.itemView.getContext());
            radio.setText(option);
            radio.setTextSize(16f);
            radio.setPadding(8, 8, 8, 8);
            radio.setButtonTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.teal_200));
            holder.optionsLayout.addView(radio);
            holder.optionButtons.add(radio);

            if (selectedAnswers.containsKey(position) && selectedAnswers.get(position).equals(option)) {
                radio.setChecked(true);
            }

            radio.setEnabled(!isSubmitted);

            radio.setOnClickListener(v -> {
                if (!isSubmitted) {
                    selectedAnswers.put(position, option);
                    for (RadioButton other : holder.optionButtons) {
                        other.setChecked(other == radio);
                    }
                    ObjectAnimator.ofObject(radio, "textColor", new ArgbEvaluator(),
                                    Color.BLACK, ContextCompat.getColor(holder.itemView.getContext(), R.color.teal_200))
                            .setDuration(300)
                            .start();
                }
            });
        }

        if (isSubmitted) {
            for (RadioButton btn : holder.optionButtons) {
                String text = btn.getText().toString();
                if (text.equals(question.getAnswer())) {
                    btn.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                } else if (selectedAnswers.get(position) != null && selectedAnswers.get(position).equals(text)) {
                    btn.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
                } else {
                    btn.setTextColor(Color.BLACK);
                }
            }
        } else {
            for (RadioButton btn : holder.optionButtons) {
                btn.setTextColor(Color.BLACK);
            }
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionText, passageText;
        LinearLayout optionsLayout;
        List<RadioButton> optionButtons = new ArrayList<>();

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            passageText = itemView.findViewById(R.id.passageText);
            questionText = itemView.findViewById(R.id.questionText);
            optionsLayout = itemView.findViewById(R.id.optionsLayout);
        }
    }
}