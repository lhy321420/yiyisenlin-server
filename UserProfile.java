package com.example.demo.entity;

public class UserProfile {
    private Integer userId;
    private String status;          // 个人状态/签名
    private String photos;          // JSON 字符串，存储照片列表
    private String recentActivity;  // 最近动态

    // 无参构造器
    public UserProfile() {}

    // getter / setter
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPhotos() { return photos; }
    public void setPhotos(String photos) { this.photos = photos; }

    public String getRecentActivity() { return recentActivity; }
    public void setRecentActivity(String recentActivity) { this.recentActivity = recentActivity; }
}