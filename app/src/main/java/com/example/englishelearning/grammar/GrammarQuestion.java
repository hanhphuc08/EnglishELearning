package com.example.englishelearning.grammar;

import java.util.List;

public class GrammarQuestion {
    private int id;
    private String question;
    private String passage;
    private String answer;
    private List<String> options;

    public GrammarQuestion() {}

    public int getId() { return id; }
    public String getQuestion() { return question; }
    public String getPassage() { return passage; }
    public String getAnswer() { return answer; }
    public List<String> getOptions() { return options; }
}

