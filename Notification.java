package com.example.demo.entity;

import java.time.LocalDateTime;

public class Notification {
    private Integer id;
    private Integer toUserId;
    private Integer fromUserId;
    private String type;      // COMMENT, REPLY, LIKE, FRIEND_REQUEST, FRIEND_ACCEPT, NEW_DIARY, NEW_PHOTO, NEW_RECORD, NEW_STRATEGY
    private String module;    // 新增：friend/record/album/map（对应四个功能模块）
    private Integer targetId;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;

    // 扩展字段
    private String fromUsername;
    private String fromAvatar;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getToUserId() { return toUserId; }
    public void setToUserId(Integer toUserId) { this.toUserId = toUserId; }

    public Integer getFromUserId() { return fromUserId; }
    public void setFromUserId(Integer fromUserId) { this.fromUserId = fromUserId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // 新增 getter/setter
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }

    public Integer getTargetId() { return targetId; }
    public void setTargetId(Integer targetId) { this.targetId = targetId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getFromUsername() { return fromUsername; }
    public void setFromUsername(String fromUsername) { this.fromUsername = fromUsername; }

    public String getFromAvatar() { return fromAvatar; }
    public void setFromAvatar(String fromAvatar) { this.fromAvatar = fromAvatar; }
}