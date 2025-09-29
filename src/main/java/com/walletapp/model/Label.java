package com.walletapp.model;

public class Label {
    private Integer id;
    private Integer userId;
    private String name;

    public Label() {}

    public Label(Integer id, Integer userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

