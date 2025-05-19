package com.example.englishelearning.grammar;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.englishelearning.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionFragment extends Fragment {
    private static final String ARG_QUESTION = "question";
    private static final String ARG_POSITION = "position";
    private static final String ARG_SUBMITTED = "submitted";
    private static final String ARG_SELECTED_ANSWERS = "selected_answers";

    private GrammarQuestion question;
    private int position;
    private boolean isSubmitted;
    private Map<Integer, String> selectedAnswers;
    private List<RadioButton> optionButtons = new ArrayList<>();

    public static QuestionFragment newInstance(GrammarQuestion question, int position, boolean isSubmitted, Map<Integer, String> selectedAnswers) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        args.putInt(ARG_POSITION, position);
        args.putBoolean(ARG_SUBMITTED, isSubmitted);
        args.putSerializable(ARG_SELECTED_ANSWERS, (HashMap<Integer, String>) selectedAnswers);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (GrammarQuestion) getArguments().getSerializable(ARG_QUESTION);
            position = getArguments().getInt(ARG_POSITION);
            isSubmitted = getArguments().getBoolean(ARG_SUBMITTED);
            selectedAnswers = (Map<Integer, String>) getArguments().getSerializable(ARG_SELECTED_ANSWERS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_grammar_question, container, false);

        TextView passageText = view.findViewById(R.id.passageText);
        TextView questionText = view.findViewById(R.id.questionText);
        LinearLayout optionsLayout = view.findViewById(R.id.optionsLayout);

        passageText.setText((position + 1) + ". " + question.getPassage());
        questionText.setText(question.getQuestion());

        List<String> options = question.getOptions();
        optionButtons.clear();
        optionsLayout.removeAllViews();

        for (String option : options) {
            RadioButton radio = new RadioButton(getContext());
            radio.setText(option);
            radio.setTextSize(16f);
            radio.setPadding(8, 8, 8, 8);
            radio.setButtonTintList(ContextCompat.getColorStateList(getContext(), R.color.teal_200));
            optionsLayout.addView(radio);
            optionButtons.add(radio);

            if (selectedAnswers.containsKey(position) && selectedAnswers.get(position).equals(option)) {
                radio.setChecked(true);
            }

            radio.setEnabled(!isSubmitted);

            radio.setOnClickListener(v -> {
                if (!isSubmitted) {
                    selectedAnswers.put(position, option);
                    for (RadioButton other : optionButtons) {
                        other.setChecked(other == radio);
                    }
                    ObjectAnimator.ofObject(radio, "textColor", new ArgbEvaluator(),
                                    Color.BLACK, ContextCompat.getColor(getContext(), R.color.teal_200))
                            .setDuration(300)
                            .start();
                }
            });
        }

        if (isSubmitted) {
            for (RadioButton btn : optionButtons) {
                String text = btn.getText().toString();
                if (text.equals(question.getAnswer())) {
                    btn.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                } else if (selectedAnswers.get(position) != null && selectedAnswers.get(position).equals(text)) {
                    btn.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else {
                    btn.setTextColor(Color.BLACK);
                }
            }
        } else {
            for (RadioButton btn : optionButtons) {
                btn.setTextColor(Color.BLACK);
            }
        }

        return view;
    }
}