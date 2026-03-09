package model;

import java.util.ArrayList;
import java.util.List;

public class ForumThread {
    private String id;
    private String title;
    private String question;
    private String author;
    private long createdAt;
    private List<ForumReply> replies;

    public ForumThread() {
    }

    public ForumThread(String id, String title, String question, String author, long createdAt) {
        this.id = id;
        this.title = title;
        this.question = question;
        this.author = author;
        this.createdAt = createdAt;
        this.replies = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public List<ForumReply> getReplies() {
        if (replies == null) {
            replies = new ArrayList<>();
        }
        return replies;
    }

    public void setReplies(List<ForumReply> replies) {
        this.replies = replies;
    }
}
