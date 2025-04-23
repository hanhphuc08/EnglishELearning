package com.example.englishelearning.grammar;

import java.util.List;

public class GrammarTopic {
    private String key;
    private String content;
    private String description;
    private int id;
    private List<GrammarLevel> levels;

    public GrammarTopic() {}

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getContent() { return content; }
    public String getDescription() { return description; }
    public int getId() { return id; }
    public List<GrammarLevel> getLevels() { return levels; }
}