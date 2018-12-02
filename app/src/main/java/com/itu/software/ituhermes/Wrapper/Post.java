package com.itu.software.ituhermes.Wrapper;

import java.io.Serializable;

public class Post implements Serializable {
    private String sender;
    private String message;
    private String date;
    private int topicId;
    private String postId;
    private String senderId;
    public Post(String sender, String message, String date, int topicId, String postId, String senderId) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.topicId = topicId;
        this.postId = postId;
        this.senderId = senderId;
    }

    public String getPostId() {
        return postId;
    }

    public String getSenderId() {
        return senderId;
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
