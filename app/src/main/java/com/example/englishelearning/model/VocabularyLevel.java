package com.example.englishelearning.model;

import java.util.List;

public class VocabularyLevel {
    private int id;
    private String level;
    private List<VocabularyTopic> topics;


    public VocabularyLevel() {
    }

    public VocabularyLevel(int id, String level, List<VocabularyTopic> topics) {
        this.id = id;
        this.level = level;
        this.topics = topics;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<VocabularyTopic> getTopics() {
        return topics;
    }

    public void setTopics(List<VocabularyTopic> topics) {
        this.topics = topics;
    }
}
