package com.example.englishelearning.model;

import java.io.Serializable;
import java.util.List;

public class VocabularyTopic implements Serializable {
    private int id;
    private String topic;
    private List<VocabularyWord> words;


    public VocabularyTopic() {
    }

    public VocabularyTopic(int id, String topic, List<VocabularyWord> words) {
        this.id = id;
        this.topic = topic;
        this.words = words;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<VocabularyWord> getWords() {
        return words;
    }

    public void setWords(List<VocabularyWord> words) {
        this.words = words;
    }
}
