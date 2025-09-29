package com.walletapp.model;

public class Category {
    private Integer id;
    private Integer userId;
    private Integer parentId; // nullable
    private String name;

    public Category() {}

    public Category(Integer id, Integer userId, Integer parentId, String name) {
        this.id = id;
        this.userId = userId;
        this.parentId = parentId;
        this.name = name;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
