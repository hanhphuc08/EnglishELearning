package com.example.englishelearning.model;

import java.io.Serializable;
import java.util.List;

public class TopicSpeaking implements Serializable {
    public int id;
    public String title;
    public String video;
    public String transcript;
    public int progress;

    public List<String> key_phrases;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public List<String> getKeyPhrases() {
        return key_phrases;
    }

    public void setKeyPhrases(List<String> key_phrases) {
        this.key_phrases = key_phrases;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
