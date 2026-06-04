package com.example.demo.entity;

import java.time.LocalDateTime;

public class FriendRequest {
    private Integer id;
    private Integer fromUserId;
    private Integer toUserId;
    private Integer status;   // 0-待处理 1-已接受 2-已拒绝
    private LocalDateTime createTime;

    // 无参构造器
    public FriendRequest() {}

    // getter / setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getFromUserId() { return fromUserId; }
    public void setFromUserId(Integer fromUserId) { this.fromUserId = fromUserId; }

    public Integer getToUserId() { return toUserId; }
    public void setToUserId(Integer toUserId) { this.toUserId = toUserId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}