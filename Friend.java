package com.example.demo.entity;

import java.time.LocalDateTime;

public class Friend {
    private Integer id;
    private Integer userId;
    private Integer friendId;
    private Integer status;   // 1: 好友
    private LocalDateTime createTime;

    // getter / setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getFriendId() { return friendId; }
    public void setFriendId(Integer friendId) { this.friendId = friendId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}