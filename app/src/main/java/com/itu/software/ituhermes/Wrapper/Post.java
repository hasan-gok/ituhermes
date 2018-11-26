package com.itu.software.ituhermes.Wrapper;

public class Post {
    private String sender;
    private String message;
    private String date;
    private int topicId;

    public Post(String sender, String message, String date, int topicId) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.topicId = topicId;
    }

    public int getTopicId() {
        return topicId;
    }
    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}
