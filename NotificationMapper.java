package com.example.demo.mapper;

import com.example.demo.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NotificationMapper {
    int insert(Notification notification);

    // 新增：批量插入通知
    int batchInsert(@Param("list") List<Notification> notifications);

    List<Notification> selectByUserId(@Param("userId") Integer userId, @Param("limit") int limit);
    int countUnread(@Param("userId") Integer userId);
    int markAsRead(@Param("id") Integer id);
    int markAllAsRead(@Param("userId") Integer userId);
    Notification selectById(@Param("id") Integer id);
}