package com.example.englishelearning.grammar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultDetailActivity extends AppCompatActivity {
    RecyclerView resultRecycler;
    ResultDetailAdapter adapter;
    List<GrammarQuestion> questionList;
    Map<Integer, String> selectedAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_detail);

        resultRecycler = findViewById(R.id.result_recycler);
        resultRecycler.setLayoutManager(new LinearLayoutManager(this));

        questionList = (List<GrammarQuestion>) getIntent().getSerializableExtra("question_list");
        selectedAnswers = (Map<Integer, String>) getIntent().getSerializableExtra("selected_answers");

        adapter = new ResultDetailAdapter(questionList, selectedAnswers);
        resultRecycler.setAdapter(adapter);
    }

    static class ResultDetailAdapter extends RecyclerView.Adapter<ResultDetailAdapter.ResultViewHolder> {
        private final List<GrammarQuestion> questionList;
        private final Map<Integer, String> selectedAnswers;

        ResultDetailAdapter(List<GrammarQuestion> questionList, Map<Integer, String> selectedAnswers) {
            this.questionList = questionList;
            this.selectedAnswers = selectedAnswers;
        }

        @NonNull
        @Override
        public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_detail, parent, false);
            return new ResultViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
            GrammarQuestion question = questionList.get(position);
            holder.questionText.setText((position + 1) + ". " + question.getQuestion());
            holder.correctAnswer.setText("Đáp án đúng: " + question.getAnswer());
            holder.userAnswer.setText("Đáp án của bạn: " + (selectedAnswers.get(position) != null ? selectedAnswers.get(position) : "Chưa chọn"));
        }

        @Override
        public int getItemCount() {
            return questionList.size();
        }

        static class ResultViewHolder extends RecyclerView.ViewHolder {
            TextView questionText, correctAnswer, userAnswer;

            ResultViewHolder(@NonNull View itemView) {
                super(itemView);
                questionText = itemView.findViewById(R.id.result_question_text);
                correctAnswer = itemView.findViewById(R.id.result_correct_answer);
                userAnswer = itemView.findViewById(R.id.result_user_answer);
            }
        }
    }
}