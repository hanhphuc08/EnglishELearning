package com.example.englishelearning.grammar;

import java.io.Serializable;
import java.util.List;

public class GrammarQuestion implements Serializable {
    private int id;
    private String question;
    private String passage;
    private String answer;
    private List<String> options;

    public GrammarQuestion() {
        // Hàm tạo rỗng cho Firebase
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPassage() {
        return passage;
    }

    public void setPassage(String passage) {
        this.passage = passage;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}