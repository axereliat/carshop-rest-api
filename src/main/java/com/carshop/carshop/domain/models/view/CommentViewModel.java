package com.carshop.carshop.domain.models.view;

public class CommentViewModel {

    private Integer id;

    private String content;

    private String addedOn;

    private UserViewModel author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public UserViewModel getAuthor() {
        return author;
    }

    public void setAuthor(UserViewModel author) {
        this.author = author;
    }
}
