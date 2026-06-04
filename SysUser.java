package com.example.demo.entity;

import java.io.Serializable;

public class SysUser implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String birthday;
    private String avatar;   // 新增头像字段

    // getter / setter 方法
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    private String description;   // 个性签名/描述

    // 加上 getter/setter
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    // 以下字段用于好友详情展示，不映射数据库
    private String status;
    private String photos;
    private String recentActivity;

    // getter / setter
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPhotos() { return photos; }
    public void setPhotos(String photos) { this.photos = photos; }

    public String getRecentActivity() { return recentActivity; }
    public void setRecentActivity(String recentActivity) { this.recentActivity = recentActivity; }
    private String mbti;
    private String zodiac;

    // getter / setter
    public String getMbti() { return mbti; }
    public void setMbti(String mbti) { this.mbti = mbti; }
    public String getZodiac() { return zodiac; }
    public void setZodiac(String zodiac) { this.zodiac = zodiac; }
}