package com.itu.software.ituhermes.Wrapper;

import android.util.Log;

import java.util.ArrayList;

public class User {
    private String email;
    private ArrayList<String> topicTags;
    public static User currentUser = null;

    private User() {
        email = "";
        topicTags = new ArrayList<>();
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


}
