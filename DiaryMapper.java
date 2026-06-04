package com.example.demo.mapper;

import com.example.demo.entity.Diary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DiaryMapper {
    int insert(Diary diary);
    Diary selectById(@Param("id") Integer id, @Param("currentUserId") Integer currentUserId);
    List<Diary> selectByUserId(@Param("userId") Integer userId, @Param("currentUserId") Integer currentUserId);
    List<Diary> selectPublicDiaries(@Param("currentUserId") Integer currentUserId);
    List<Diary> selectFriendDiaries(@Param("currentUserId") Integer currentUserId, @Param("friendIds") List<Integer> friendIds);
    List<Diary> selectFavoritesByUserId(@Param("userId") Integer userId, @Param("currentUserId") Integer currentUserId);

    int updateViewCount(@Param("id") Integer id);
    int updateLikeCount(@Param("id") Integer id, @Param("increment") int increment);
    int updateCommentCount(@Param("id") Integer id, @Param("increment") int increment);
    int updateFavoriteCount(@Param("id") Integer id, @Param("increment") int increment);

    int insertFavorite(@Param("diaryId") Integer diaryId, @Param("userId") Integer userId);
    int deleteFavorite(@Param("diaryId") Integer diaryId, @Param("userId") Integer userId);
    int countFavorite(@Param("diaryId") Integer diaryId, @Param("userId") Integer userId);
    int deleteDiary(@Param("diaryId") Integer diaryId);
    int deleteById(@Param("id") Integer id, @Param("userId") Integer userId);
}