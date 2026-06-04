package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("city_photo")
public class CityPhoto {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String province;
    private String city;
    private String url;
    private Long userId;
    private Date createTime;
}