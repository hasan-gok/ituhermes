package com.itu.software.ituhermes.Wrapper;

public class Topic implements java.io.Serializable {
    private String title;
    private String tag;
    private int topicId;
    private int postCount;
    private int pageCount;
    private boolean isSubscribing;

    public Topic(String title, String tag, int topicId, int postCount, int pageCount, boolean isSubscribing) {
        this.title = title;
        this.topicId = topicId;
        this.postCount = postCount;
        this.tag = tag;
        this.pageCount = pageCount;
        this.isSubscribing = isSubscribing;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPostCount() {
        return postCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getTag() {
        return tag;
    }

    public void setSubscribing(boolean subscribing) {
        isSubscribing = subscribing;
    }

    public boolean isSubscribing() {
        return isSubscribing;
    }

    public int getTopicId() {
        return topicId;
    }
}
