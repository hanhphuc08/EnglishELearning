package com.example.englishelearning.grammar;

import java.util.List;

public class GrammarLevel {
    private int level;
    private String description;
    private List<GrammarQuestion> questions;

    public GrammarLevel() {}

    public int getLevel() { return level; }
    public String getDescription() { return description; }
    public List<GrammarQuestion> getQuestions() { return questions; }
}
