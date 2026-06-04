package com.example.demo.mapper;

import com.example.demo.entity.PhotoComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PhotoCommentMapper {
    int insert(PhotoComment comment);
    List<PhotoComment> selectByPhotoId(@Param("photoId") Integer photoId);
    int deleteByPhotoId(@Param("photoId") Integer photoId);
    // 新增：根据评论ID删除评论（需要验证用户ID）
    int deleteById(@Param("id") Integer id, @Param("userId") Integer userId);
}