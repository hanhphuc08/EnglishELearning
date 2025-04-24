package com.example.englishelearning.model;

import java.io.Serializable;

public class VocabularyWord implements Serializable {
    private int id;
    private String word;
    private String meaning;
    private String phonetic;
    private String example;

    public VocabularyWord() {
    }

    public VocabularyWord(int id, String word, String meaning, String phonetic, String example) {
        this.id = id;
        this.word = word;
        this.meaning = meaning;
        this.phonetic = phonetic;
        this.example = example;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
