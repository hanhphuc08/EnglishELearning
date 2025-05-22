package com.example.englishelearning.model;

import java.util.Date;

public class GameScore {
    private String userId;
    private String topicId;
    private String topicName;
    private int score;
    private Date playedAt;

    public GameScore() {
    }

    public GameScore(String userId, String topicId, String topicName, int score) {
        this.userId = userId;
        this.topicId = topicId;
        this.topicName = topicName;
        this.score = score;
        this.playedAt = new Date();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Date playedAt) {
        this.playedAt = playedAt;
    }
} 