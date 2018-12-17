package com.itu.software.ituhermes.Wrapper;

import java.util.ArrayList;

public class User {
    private String email;
    private String name;
    private String lastName;
    private ArrayList<String> topicTags;
    private String token;
    private String userId;
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private static User currentUser = null;

    private User() {
        email = "";
        token = "";
        topicTags = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static User getCurrentUser() {
        if (currentUser == null) {
            currentUser = new User();
        }
        return currentUser;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getTopicTags() {
        return topicTags;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addTopicTag(String tag) {
        if (!topicTags.contains(tag))
            topicTags.add(tag);
    }

    public int deleteTopicTag(String tag) {
        int index = topicTags.indexOf(tag);
        if (index >= 0) {
            topicTags.remove(index);
        }
        return index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void clearData() {
        email = "";
        userId = "";
        name = "";
        lastName = "";
        token = "";
        topicTags.clear();
    }
}
