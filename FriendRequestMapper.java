package com.example.demo.mapper;

import com.example.demo.entity.FriendRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FriendRequestMapper {
    // 发送好友申请
    int insertRequest(FriendRequest request);
    // 获取发给我的未处理申请列表
    List<FriendRequest> getPendingRequests(@Param("toUserId") Integer toUserId);
    // 获取未读申请数量（status=0）
    int countPendingRequests(@Param("toUserId") Integer toUserId);
    // 更新申请状态（接受/拒绝）
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    // 检查是否已经发送过申请（未处理的）
    int checkPending(@Param("fromUserId") Integer fromUserId, @Param("toUserId") Integer toUserId);
    FriendRequest selectById(@Param("id") Integer id);
}