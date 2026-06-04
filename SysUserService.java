package com.example.demo.service;
import java.time.LocalDateTime;
import com.example.demo.common.Result;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private FriendMapper friendMapper;
    @Resource
    private FriendRequestMapper friendRequestMapper;
    @Resource
    private UserProfileMapper userProfileMapper;

    @Resource
    private DiaryMapper diaryMapper;
    @Resource
    private DiaryCommentMapper diaryCommentMapper;
    @Resource
    private DiaryLikeMapper diaryLikeMapper;
    @Resource
    private NotificationMapper notificationMapper;
    @Resource
    private DiaryCommentLikeMapper diaryCommentLikeMapper;

    // ========== 城市功能 Mapper ==========
    @Resource
    private CityPhotoMapper cityPhotoMapper;
    @Resource
    private CityRecordMapper cityRecordMapper;
    @Resource
    private CityStrategyMapper cityStrategyMapper;

    // ========== ✅ 修复：固定项目根目录 upload 文件夹，绝对路径 ==========
    private static final String UPLOAD_FOLDER = System.getProperty("user.dir") + File.separator + "upload" + File.separator;

    // ========== 原有方法（不变） ==========
    public List<SysUser> listAll() {
        return sysUserMapper.listAll();
    }

    public SysUser getByUsername(String username) {
        return sysUserMapper.getByUsername(username);
    }

    public SysUser login(String username, String password) {
        SysUser user = sysUserMapper.getByUsername(username);
        if (user == null) return null;
        if (!user.getPassword().equals(password)) return null;
        return user;
    }

    public int register(SysUser user) {
        SysUser exist = sysUserMapper.getByUsername(user.getUsername());
        if (exist != null) return 0;
        return sysUserMapper.insert1(user);
    }

    public int updateInfo(SysUser user) {
        return sysUserMapper.updateInfo(user);
    }

    public SysUser getById(Integer id) {
        return sysUserMapper.getById(id);
    }

    public int updateAvatar(Integer userId, String avatar) {
        return sysUserMapper.updateAvatar(userId, avatar);
    }

    public boolean addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) return false;
        if (friendMapper.checkFriend(userId, friendId) > 0) return false;
        return friendMapper.addFriend(userId, friendId) > 0;
    }

    public List<SysUser> getFriends(Integer userId) {
        List<Integer> friendIds = friendMapper.getFriendIdsByUserId(userId);
        if (friendIds == null || friendIds.isEmpty()) return new ArrayList<>();
        friendIds.removeIf(Objects::isNull);
        if (friendIds.isEmpty()) return new ArrayList<>();
        return sysUserMapper.getUsersByIds(friendIds);
    }

    public List<SysUser> searchUsers(Integer currentUserId, String keyword) {
        List<Integer> excludeIds = friendMapper.getFriendIdsByUserId(currentUserId);
        if (excludeIds == null) excludeIds = new ArrayList<>();
        excludeIds.removeIf(Objects::isNull);
        excludeIds.add(currentUserId);
        return sysUserMapper.searchUsers(keyword, excludeIds);
    }

    public boolean sendFriendRequest(Integer fromUserId, Integer toUserId) {
        if (fromUserId.equals(toUserId)) return false;
        if (friendMapper.checkFriend(fromUserId, toUserId) > 0) return false;
        if (friendRequestMapper.checkPending(fromUserId, toUserId) > 0) return false;
        FriendRequest request = new FriendRequest();
        request.setFromUserId(fromUserId);
        request.setToUserId(toUserId);
        request.setStatus(0);
        int result = friendRequestMapper.insertRequest(request);

        if (result > 0) {
            SysUser fromUser = sysUserMapper.getById(fromUserId);
            // 发送通知给被申请人
            sendNotificationToUser(
                    toUserId,
                    fromUserId,
                    "friend",
                    "FRIEND_REQUEST",
                    null,
                    fromUser.getUsername() + " 申请添加你为好友"
            );
        }

        return result > 0;
    }

    public int getUnreadRequestCount(Integer userId) {
        return friendRequestMapper.countPendingRequests(userId);
    }

    public List<Map<String, Object>> getPendingRequestsWithSender(Integer userId) {
        List<FriendRequest> requests = friendRequestMapper.getPendingRequests(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (FriendRequest req : requests) {
            SysUser sender = sysUserMapper.getById(req.getFromUserId());
            if (sender == null) continue;
            Map<String, Object> map = new HashMap<>();
            map.put("id", req.getId());
            map.put("fromUserId", sender.getId());
            map.put("username", sender.getUsername());
            map.put("avatar", sender.getAvatar());
            result.add(map);
        }
        return result;
    }

    public boolean acceptFriendRequest(Integer requestId, Integer currentUserId) {
        FriendRequest request = friendRequestMapper.selectById(requestId);
        if (request == null || !request.getToUserId().equals(currentUserId) || request.getStatus() != 0) return false;
        friendRequestMapper.updateStatus(requestId, 1);
        friendMapper.addFriend(request.getFromUserId(), request.getToUserId());

        // 通知对方：你已接受好友请求
        SysUser currentUser = sysUserMapper.getById(currentUserId);
        sendNotificationToUser(
                request.getFromUserId(),
                currentUserId,
                "friend",
                "FRIEND_ACCEPT",
                currentUserId,
                currentUser.getUsername() + " 接受了你的好友申请"
        );

        return true;
    }

    public boolean rejectFriendRequest(Integer requestId, Integer currentUserId) {
        FriendRequest request = friendRequestMapper.selectById(requestId);
        if (request == null || !request.getToUserId().equals(currentUserId) || request.getStatus() != 0) return false;
        friendRequestMapper.updateStatus(requestId, 2);
        return true;
    }

    public boolean deleteFriend(Integer userId, Integer friendId) {
        return friendMapper.deleteFriend(userId, friendId) > 0;
    }

    public SysUser getFriendDetail(Integer userId, Integer friendId) {
        if (friendMapper.checkFriend(userId, friendId) == 0) return null;
        SysUser friend = sysUserMapper.getById(friendId);
        UserProfile profile = userProfileMapper.getByUserId(friendId);
        if (profile != null) {
            friend.setStatus(profile.getStatus());
            friend.setPhotos(profile.getPhotos());
            friend.setRecentActivity(profile.getRecentActivity());
        }
        return friend;
    }

    public boolean updateMyProfile(Integer userId, String status, String photos, String recentActivity) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setStatus(status);
        profile.setPhotos(photos);
        profile.setRecentActivity(recentActivity);
        return userProfileMapper.insertOrUpdate(profile) > 0;
    }

    // ========== 日记相关 ==========
    public int createDiary(Diary diary) {
        int result = diaryMapper.insert(diary);
        if (result > 0) {
            // 如果日记是公开的（permission=3）或好友可见（permission=2），通知好友
            if (diary.getPermission() == 2 || diary.getPermission() == 3) {
                SysUser user = sysUserMapper.getById(diary.getUserId());
                notifyAllFriends(
                        diary.getUserId(),
                        user.getUsername(),
                        "record",           // 模块：记录
                        "NEW_DIARY",        // 类型：新日记
                        diary.getId(),
                        "{username} 发布了新日记",
                        diary.getTitle()
                );
            }
        }
        return result;
    }

    public Diary getDiaryById(Integer diaryId, Integer currentUserId) {
        Diary diary = diaryMapper.selectById(diaryId, currentUserId);
        if (diary == null) return null;
        if (diary.getPermission() == 1 && !diary.getUserId().equals(currentUserId)) return null;
        if (diary.getPermission() == 2 && friendMapper.checkFriend(currentUserId, diary.getUserId()) == 0) return null;
        diaryMapper.updateViewCount(diaryId);
        return diary;
    }

    public List<Diary> getMyDiaries(Integer userId) {
        return diaryMapper.selectByUserId(userId, userId);
    }

    public List<Diary> getPublicDiaries(Integer currentUserId) {
        return diaryMapper.selectPublicDiaries(currentUserId);
    }

    public List<Diary> getFriendDiaries(Integer currentUserId) {
        List<Integer> friendIds = friendMapper.getFriendIdsByUserId(currentUserId);
        if (friendIds == null || friendIds.isEmpty()) return new ArrayList<>();
        return diaryMapper.selectFriendDiaries(currentUserId, friendIds);
    }

    public boolean toggleLike(Integer diaryId, Integer userId) {
        int count = diaryLikeMapper.countByDiaryAndUser(diaryId, userId);
        if (count == 0) {
            DiaryLike like = new DiaryLike();
            like.setDiaryId(diaryId);
            like.setUserId(userId);
            diaryLikeMapper.insert(like);
            diaryMapper.updateLikeCount(diaryId, 1);
            Diary diary = diaryMapper.selectById(diaryId, userId);
            if (diary != null && !diary.getUserId().equals(userId)) {
                Notification n = new Notification();
                n.setToUserId(diary.getUserId());
                n.setFromUserId(userId);
                n.setType("LIKE");
                n.setTargetId(diaryId);
                n.setContent("赞了你的日记《" + diary.getTitle() + "》");
                notificationMapper.insert(n);
            }
            return true;
        } else {
            diaryLikeMapper.delete(diaryId, userId);
            diaryMapper.updateLikeCount(diaryId, -1);
            return false;
        }
    }

    public boolean addComment(DiaryComment comment) {
        int result = diaryCommentMapper.insert(comment);
        if (result > 0) {
            diaryMapper.updateCommentCount(comment.getDiaryId(), 1);
            Diary diary = diaryMapper.selectById(comment.getDiaryId(), comment.getUserId());
            Integer toUserId = null;
            String notifyContent = "";
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                DiaryComment parent = diaryCommentMapper.selectById(comment.getParentId());
                if (parent != null && !parent.getUserId().equals(comment.getUserId())) {
                    toUserId = parent.getUserId();
                    notifyContent = "回复了你的评论：" + comment.getContent();
                }
            }
            if (toUserId == null && diary != null && !diary.getUserId().equals(comment.getUserId())) {
                toUserId = diary.getUserId();
                notifyContent = "评论了你的日记《" + diary.getTitle() + "》：" + comment.getContent();
            }
            if (toUserId != null) {
                Notification n = new Notification();
                n.setToUserId(toUserId);
                n.setFromUserId(comment.getUserId());
                n.setType(comment.getParentId() != null && comment.getParentId() > 0 ? "REPLY" : "COMMENT");
                n.setTargetId(comment.getDiaryId());
                n.setContent(notifyContent);
                notificationMapper.insert(n);
            }
            return true;
        }
        return false;
    }

    public List<DiaryComment> getCommentsByDiaryId(Integer diaryId, Integer currentUserId) {
        return diaryCommentMapper.selectByDiaryId(diaryId, currentUserId);
    }

    public int getUnreadNotificationCount(Integer userId) {
        return notificationMapper.countUnread(userId);
    }

    public List<Notification> getNotifications(Integer userId, int limit) {
        return notificationMapper.selectByUserId(userId, limit);
    }

    public boolean markNotificationRead(Integer notificationId, Integer userId) {
        Notification n = notificationMapper.selectById(notificationId);
        if (n != null && n.getToUserId().equals(userId)) {
            notificationMapper.markAsRead(notificationId);
            return true;
        }
        return false;
    }

    // ========== 收藏功能 ==========
    public boolean toggleFavorite(Integer diaryId, Integer userId) {
        int count = diaryMapper.countFavorite(diaryId, userId);
        if (count == 0) {
            diaryMapper.insertFavorite(diaryId, userId);
            diaryMapper.updateFavoriteCount(diaryId, 1);
            return true;
        } else {
            diaryMapper.deleteFavorite(diaryId, userId);
            diaryMapper.updateFavoriteCount(diaryId, -1);
            return false;
        }
    }

    public List<Diary> getFavoriteDiaries(Integer userId) {
        return diaryMapper.selectFavoritesByUserId(userId, userId);
    }

    // ========== 评论点赞功能 ==========
    public boolean toggleCommentLike(Integer commentId, Integer userId) {
        int count = diaryCommentLikeMapper.count(commentId, userId);
        if (count == 0) {
            diaryCommentLikeMapper.insert(commentId, userId);
            diaryCommentLikeMapper.updateLikeCount(commentId, 1);
            return true;
        } else {
            diaryCommentLikeMapper.delete(commentId, userId);
            diaryCommentLikeMapper.updateLikeCount(commentId, -1);
            return false;
        }
    }

    // ===================== 获取计数 =====================
    public int getDiaryLikeCount(Integer diaryId) {
        Diary diary = diaryMapper.selectById(diaryId, 0);
        return diary != null ? (diary.getLikeCount() == null ? 0 : diary.getLikeCount()) : 0;
    }

    public int getDiaryFavoriteCount(Integer diaryId) {
        Diary diary = diaryMapper.selectById(diaryId, 0);
        return diary != null ? (diary.getFavoriteCount() == null ? 0 : diary.getFavoriteCount()) : 0;
    }

    public int getCommentLikeCount(Integer commentId) {
        DiaryComment comment = diaryCommentMapper.selectById(commentId);
        return comment != null ? (comment.getLikeCount() == null ? 0 : comment.getLikeCount()) : 0;
    }

    public boolean deleteComment(Integer commentId) {
        return diaryCommentMapper.deleteComment(commentId) > 0;
    }
    // ==================== 删除日记 ====================
    public boolean deleteDiary(Integer diaryId, Integer currentUserId) {
        // 1. 先查询日记是否存在，并且是本人才能删
        Diary diary = diaryMapper.selectById(diaryId, currentUserId);
        if (diary == null) {
            return false;
        }
        // 2. 只能删除自己的日记
        if (!diary.getUserId().equals(currentUserId)) {
            return false;
        }
        // 3. 删除日记（会自动删除关联点赞、评论，看你业务是否需要）
        return diaryMapper.deleteDiary(diaryId) > 0;
    }

    // ===================== 城市功能：删除 =====================
    public Result delPhoto(Long id, Long currentUserId) {
        CityPhoto p = cityPhotoMapper.selectById(id);
        if (p == null) return Result.error("照片不存在");
        if (!p.getUserId().equals(currentUserId)) return Result.error("无权限删除");

        cityPhotoMapper.deleteById(id);
        return Result.success("删除成功");
    }

    public Result delRecord(Long id, Long currentUserId) {
        CityRecord r = cityRecordMapper.selectById(id);
        if (r == null) return Result.error("记录不存在");
        if (!r.getUserId().equals(currentUserId)) return Result.error("无权限删除");

        cityRecordMapper.deleteById(id);
        return Result.success("删除成功");
    }

    public Result delStrategy(Long id, Long currentUserId) {
        CityStrategy s = cityStrategyMapper.selectById(id);
        if (s == null) return Result.error("攻略不存在");
        if (!s.getUserId().equals(currentUserId)) return Result.error("无权限删除");

        cityStrategyMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // ===================== ✅ 已修复：上传图片，保存到项目 upload 文件夹 =====================
    public Result savePhoto(CityPhoto photo, MultipartFile file) {
        try {
            // 自动创建文件夹
            File folder = new File(UPLOAD_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID() + suffix;
            File dest = new File(folder, fileName);

            // 保存文件
            file.transferTo(dest);

            // 正确的访问路径
            photo.setUrl("/upload/" + fileName);

            // 存入数据库
            cityPhotoMapper.insert(photo);

            // 城市照片属于"地图"模块（因为与地理位置相关）
            SysUser user = sysUserMapper.getById(photo.getUserId().intValue());
            notifyAllFriends(
                    photo.getUserId().intValue(),
                    user.getUsername(),
                    "map",                      // 模块：地图
                    "NEW_LOCATION_PHOTO",       // 类型：新位置照片
                    photo.getId().intValue(),
                    "{username} 在 " + photo.getCity() + " 上传了照片",
                    null
            );

            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("图片上传失败：" + e.getMessage());
        }
    }

    // ===================== 原有保存（不变） =====================
    public void saveRecord(CityRecord record) {
        cityRecordMapper.insert(record);
        // 城市记录也属于"记录"模块
        SysUser user = sysUserMapper.getById(record.getUserId().intValue());
        notifyAllFriends(
                record.getUserId().intValue(),
                user.getUsername(),
                "record",                    // 模块：记录
                "NEW_CITY_RECORD",           // 类型：新城市记录
                record.getId().intValue(),
                "{username} 分享了新的旅行记录",
                record.getContent().length() > 50 ? record.getContent().substring(0, 50) + "..." : record.getContent()
        );
    }

    public void saveStrategy(CityStrategy strategy) {
        cityStrategyMapper.insert(strategy);
        SysUser user = sysUserMapper.getById(strategy.getUserId().intValue());
        notifyAllFriends(
                strategy.getUserId().intValue(),
                user.getUsername(),
                "record",                      // 模块：记录
                "NEW_STRATEGY",                // 类型：新攻略
                strategy.getId().intValue(),
                "{username} 发布了新的城市攻略",
                strategy.getContent().length() > 50 ? strategy.getContent().substring(0, 50) + "..." : strategy.getContent()
        );
    }

    // ===================== ✅ 最终完美修复：城市列表带上 userAvatar + username =====================
    public List<Map<String, Object>> getPhotos(String province, String city) {
        List<CityPhoto> list = cityPhotoMapper.selectByProvinceAndCity(province, city);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CityPhoto p : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("url", p.getUrl());
            map.put("province", p.getProvince());
            map.put("city", p.getCity());
            map.put("userId", p.getUserId());
            map.put("createTime", p.getCreateTime());

            SysUser user = sysUserMapper.getById(p.getUserId().intValue());
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("userAvatar", user.getAvatar() == null ? "" : user.getAvatar());
            } else {
                map.put("username", "未知用户");
                map.put("userAvatar", "");
            }
            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> getRecords(String province, String city) {
        List<CityRecord> list = cityRecordMapper.selectByProvinceAndCity(province, city);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CityRecord r : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("content", r.getContent());
            map.put("province", r.getProvince());
            map.put("city", r.getCity());
            map.put("userId", r.getUserId());
            map.put("createTime", r.getCreateTime());

            SysUser user = sysUserMapper.getById(r.getUserId().intValue());
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("userAvatar", user.getAvatar() == null ? "" : user.getAvatar());
            } else {
                map.put("username", "未知用户");
                map.put("userAvatar", "");
            }
            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> getStrategies(String province, String city) {
        List<CityStrategy> list = cityStrategyMapper.selectByProvinceAndCity(province, city);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CityStrategy s : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("content", s.getContent());
            map.put("province", s.getProvince());
            map.put("city", s.getCity());
            map.put("userId", s.getUserId());
            map.put("createTime", s.getCreateTime());

            SysUser user = sysUserMapper.getById(s.getUserId().intValue());
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("userAvatar", user.getAvatar() == null ? "" : user.getAvatar());
            } else {
                map.put("username", "未知用户");
                map.put("userAvatar", "");
            }
            result.add(map);
        }
        return result;
    }

    public Long getCurrentUserId() {
        return 1L;
    }
    // ========== 相册相关 ==========
    @Resource
    private PhotoMapper photoMapper;
    @Resource
    private PhotoCommentMapper photoCommentMapper;
    @Resource
    private PhotoLikeMapper photoLikeMapper;

    // 上传照片
    public int uploadPhoto(Photo photo) {
        int result = photoMapper.insert(photo);
        if (result > 0) {
            SysUser user = sysUserMapper.getById(photo.getUserId());
            notifyAllFriends(
                    photo.getUserId(),
                    user.getUsername(),
                    "album",            // 模块：相册
                    "NEW_PHOTO",        // 类型：新照片
                    photo.getId(),
                    "{username} 上传了新照片",
                    null
            );
        }
        return result;
    }

    // 获取所有照片
    public List<Photo> getAllPhotos(Integer currentUserId) {
        return photoMapper.selectAll(currentUserId);
    }

    // 按人名搜索照片
    public List<Photo> searchPhotosByName(String keyword, Integer currentUserId) {
        return photoMapper.searchByName(keyword, currentUserId);
    }

    // 按时间搜索照片
    public List<Photo> searchPhotosByTime(String date, Integer currentUserId) {
        return photoMapper.searchByTime(date, currentUserId);
    }

    // 获取照片详情
    public Photo getPhotoById(Integer photoId, Integer currentUserId) {
        return photoMapper.selectById(photoId, currentUserId);
    }

    // 点赞/取消点赞照片
    public boolean togglePhotoLike(Integer photoId, Integer userId) {
        int count = photoLikeMapper.count(photoId, userId);
        if (count == 0) {
            photoLikeMapper.insert(photoId, userId);
            photoMapper.updateLikeCount(photoId, 1);
            return true;
        } else {
            photoLikeMapper.delete(photoId, userId);
            photoMapper.updateLikeCount(photoId, -1);
            return false;
        }
    }

    // 添加照片评论
    public boolean addPhotoComment(PhotoComment comment) {
        int result = photoCommentMapper.insert(comment);
        if (result > 0) {
            photoMapper.updateCommentCount(comment.getPhotoId(), 1);
            return true;
        }
        return false;
    }

    // 获取照片评论列表
    public List<PhotoComment> getPhotoComments(Integer photoId) {
        return photoCommentMapper.selectByPhotoId(photoId);
    }
    // ========== 相册相关（新增删除功能） ==========

    // 删除评论
    public boolean deletePhotoComment(Integer commentId, Integer userId) {
        return photoCommentMapper.deleteById(commentId, userId) > 0;
    }

    // 删除照片（同时删除该照片下的所有评论）
    public boolean deletePhoto(Integer photoId, Integer userId) {
        // 1. 先删除该照片下的所有评论
        photoCommentMapper.deleteByPhotoId(photoId);
        // 2. 删除照片
        return photoMapper.deleteById(photoId, userId) > 0;
    }
    // ========== 新增：批量发送通知给所有好友 ==========
    /**
     * 当用户在四个模块（朋友、记录、相册、地图）中做了可公开的事情时，
     * 给该用户的所有好友发送通知
     *
     * @param userId 当前操作用户ID
     * @param fromUserName 当前操作用户名（用于显示）
     * @param module 模块类型：friend/record/album/map
     * @param type 操作类型：ADD_FRIEND/NEW_DIARY/NEW_PHOTO/NEW_RECORD/NEW_STRATEGY等
     * @param targetId 目标ID（日记ID/照片ID/记录ID等）
     * @param contentTemplate 内容模板，如 "{username} 发布了新日记"
     * @param extraContent 额外内容（如日记标题）
     */
    public void notifyAllFriends(Integer userId, String fromUserName, String module,
                                 String type, Integer targetId, String contentTemplate, String extraContent) {
        // 获取用户的所有好友ID
        List<Integer> friendIds = friendMapper.getFriendIdsByUserId(userId);
        if (friendIds == null || friendIds.isEmpty()) {
            return; // 没有好友，无需通知
        }

        // 构建通知内容
        String content;
        if (extraContent != null && !extraContent.isEmpty()) {
            content = contentTemplate.replace("{username}", fromUserName) + "：" + extraContent;
        } else {
            content = contentTemplate.replace("{username}", fromUserName);
        }

        // 批量构建通知对象
        List<Notification> notifications = new ArrayList<>();
        for (Integer friendId : friendIds) {
            Notification notif = new Notification();
            notif.setToUserId(friendId);
            notif.setFromUserId(userId);
            notif.setModule(module);
            notif.setType(type);
            notif.setTargetId(targetId);
            notif.setContent(content);
            notif.setIsRead(0);
            notif.setCreateTime(LocalDateTime.now());
            notifications.add(notif);
        }

        // 批量插入
        if (!notifications.isEmpty()) {
            notificationMapper.batchInsert(notifications);
        }
    }

    // ========== 发送通知给指定用户（用于好友申请等） ==========
    public void sendNotificationToUser(Integer toUserId, Integer fromUserId, String module,
                                       String type, Integer targetId, String content) {
        Notification notif = new Notification();
        notif.setToUserId(toUserId);
        notif.setFromUserId(fromUserId);
        notif.setModule(module);
        notif.setType(type);
        notif.setTargetId(targetId);
        notif.setContent(content);
        notif.setIsRead(0);
        notif.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notif);
    }
}