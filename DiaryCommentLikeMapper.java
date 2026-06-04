package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DiaryCommentLikeMapper {
    int insert(@Param("commentId") Integer commentId, @Param("userId") Integer userId);
    int delete(@Param("commentId") Integer commentId, @Param("userId") Integer userId);
    int count(@Param("commentId") Integer commentId, @Param("userId") Integer userId);
    int updateLikeCount(@Param("commentId") Integer commentId, @Param("increment") int increment);
}