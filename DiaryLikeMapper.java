package com.example.demo.mapper;

import com.example.demo.entity.DiaryLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DiaryLikeMapper {
    int insert(DiaryLike like);
    int delete(@Param("diaryId") Integer diaryId, @Param("userId") Integer userId);
    int countByDiaryAndUser(@Param("diaryId") Integer diaryId, @Param("userId") Integer userId);
}