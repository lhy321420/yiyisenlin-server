package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PhotoLikeMapper {
    int insert(@Param("photoId") Integer photoId, @Param("userId") Integer userId);
    int delete(@Param("photoId") Integer photoId, @Param("userId") Integer userId);
    int count(@Param("photoId") Integer photoId, @Param("userId") Integer userId);
}