package com.example.demo.mapper;

import com.example.demo.entity.Friend;
import com.example.demo.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendMapper {
    // 查询用户的好友ID列表
    List<Integer> getFriendIdsByUserId(@Param("userId") Integer userId);

    // 添加好友关系
    int addFriend(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    // 检查是否已经是好友
    int checkFriend(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    // 根据用户ID列表批量查询用户信息（用于获取好友详情）
    List<SysUser> getUsersByIds(@Param("ids") List<Integer> ids);
    int deleteFriend(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

}