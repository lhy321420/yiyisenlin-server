package com.example.demo.mapper;

import com.example.demo.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {
    UserProfile getByUserId(@Param("userId") Integer userId);
    int insertOrUpdate(UserProfile profile);

}