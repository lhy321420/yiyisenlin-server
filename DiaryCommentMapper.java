package com.example.demo.mapper;

import com.example.demo.entity.DiaryComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DiaryCommentMapper {
    int insert(DiaryComment comment);
    List<DiaryComment> selectByDiaryId(@Param("diaryId") Integer diaryId, @Param("currentUserId") Integer currentUserId);
    DiaryComment selectById(@Param("id") Integer id);
    int updateLikeCount(@Param("id") Integer id, @Param("increment") int increment);
    int deleteByDiaryId(@Param("diaryId") Integer diaryId);
    int deleteComment(Integer commentId);
}