package com.example.demo.entity;

import java.time.LocalDateTime;

public class DiaryLike {
    private Integer id;
    private Integer diaryId;
    private Integer userId;
    private LocalDateTime createTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getDiaryId() { return diaryId; }
    public void setDiaryId(Integer diaryId) { this.diaryId = diaryId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}