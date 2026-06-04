package com.example.demo.mapper;

import com.example.demo.entity.Photo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PhotoMapper {
    int insert(Photo photo);
    Photo selectById(@Param("id") Integer id, @Param("currentUserId") Integer currentUserId);
    List<Photo> selectAll(@Param("currentUserId") Integer currentUserId);
    List<Photo> searchByName(@Param("keyword") String keyword, @Param("currentUserId") Integer currentUserId);
    List<Photo> searchByTime(@Param("date") String date, @Param("currentUserId") Integer currentUserId);
    int updateLikeCount(@Param("id") Integer id, @Param("increment") int increment);
    int updateCommentCount(@Param("id") Integer id, @Param("increment") int increment);
    // 修改：删除照片（需要验证用户ID）
    int deleteById(@Param("id") Integer id, @Param("userId") Integer userId);
}