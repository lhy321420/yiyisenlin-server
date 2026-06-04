package com.example.demo.controller;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.common.Result;
import com.example.demo.entity.*;
import com.example.demo.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    // 登录
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody SysUser user, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        System.out.println("【登录请求】session ID (登录前): " + session.getId());

        try {
            SysUser loginUser = sysUserService.login(user.getUsername(), user.getPassword());
            if (loginUser != null) {
                session.setAttribute("loginUser", loginUser);
                System.out.println("【登录成功】session ID: " + session.getId());
                System.out.println("【登录成功】存入 session 的用户: " + loginUser.getUsername() + ", ID: " + loginUser.getId());
                result.put("code", 200);
                result.put("msg", "登录成功");
                result.put("data", loginUser);
            } else {
                System.out.println("【登录失败】用户名或密码错误: " + user.getUsername());
                result.put("code", 500);
                result.put("msg", "用户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "服务器错误");
        }
        return result;
    }

    // 个人信息接口（包含头像）
    @GetMapping("/info")
    public Map<String, Object> info(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        System.out.println("【/user/info】请求 session ID: " + session.getId());

        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        System.out.println("【/user/info】从 session 中取出的 loginUser: " + loginUser);

        if (loginUser == null) {
            System.out.println("【/user/info】未登录，返回 401");
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }

        SysUser user = sysUserService.getById(loginUser.getId());
        map.put("code", 200);
        map.put("msg", "获取成功");
        map.put("data", user);
        return map;
    }

    // 修改个人信息（不包含头像）
    @PostMapping("/update-info")
    public Map<String, Object> updateInfo(@RequestBody SysUser user, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");

        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录，无法修改信息");
            return map;
        }

        user.setId(loginUser.getId());
        sysUserService.updateInfo(user);

        SysUser updatedUser = sysUserService.getById(loginUser.getId());
        session.setAttribute("loginUser", updatedUser);

        map.put("code", 200);
        map.put("msg", "保存成功");
        map.put("data", updatedUser);
        return map;
    }

    // 上传/更新头像
    @PostMapping("/upload-avatar")
    public Map<String, Object> uploadAvatar(@RequestBody Map<String, String> payload, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");

        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }

        String avatarBase64 = payload.get("avatar");
        if (avatarBase64 == null || avatarBase64.isEmpty()) {
            map.put("code", 400);
            map.put("msg", "头像数据不能为空");
            return map;
        }

        sysUserService.updateAvatar(loginUser.getId(), avatarBase64);

        SysUser updatedUser = sysUserService.getById(loginUser.getId());
        session.setAttribute("loginUser", updatedUser);

        map.put("code", 200);
        map.put("msg", "头像更新成功");
        map.put("avatar", avatarBase64);
        return map;
    }

    // 获取当前用户的好友列表
    @GetMapping("/friends")
    public Map<String, Object> getFriends(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        List<SysUser> friends = sysUserService.getFriends(loginUser.getId());
        map.put("code", 200);
        map.put("data", friends);
        return map;
    }

    // 搜索用户（按用户名）
    @GetMapping("/search")
    public Map<String, Object> searchUsers(@RequestParam("keyword") String keyword, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            map.put("code", 400);
            map.put("msg", "搜索关键字不能为空");
            return map;
        }
        List<SysUser> users = sysUserService.searchUsers(loginUser.getId(), keyword.trim());
        map.put("code", 200);
        map.put("data", users);
        return map;
    }

    // 发送好友申请
    @PostMapping("/sendFriendRequest")
    public Map<String, Object> sendFriendRequest(@RequestBody Map<String, Integer> param, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        Integer friendId = param.get("friendId");
        if (friendId == null) {
            map.put("code", 400);
            map.put("msg", "缺少好友ID");
            return map;
        }
        boolean success = sysUserService.sendFriendRequest(loginUser.getId(), friendId);
        if (success) {
            map.put("code", 200);
            map.put("msg", "好友申请已发送");
        } else {
            map.put("code", 500);
            map.put("msg", "发送失败，可能已经是好友或已发送过申请");
        }
        return map;
    }

    // 获取未读申请数量（小铃铛红点）
    @GetMapping("/unreadRequestCount")
    public Map<String, Object> unreadRequestCount(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        int count = sysUserService.getUnreadRequestCount(loginUser.getId());
        map.put("code", 200);
        map.put("data", count);
        return map;
    }

    // 获取未处理申请列表（包含发送者信息）
    @GetMapping("/pendingRequests")
    public Map<String, Object> pendingRequests(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        List<Map<String, Object>> requests = sysUserService.getPendingRequestsWithSender(loginUser.getId());
        map.put("code", 200);
        map.put("data", requests);
        return map;
    }

    // 接受好友申请
    @PostMapping("/acceptRequest")
    public Map<String, Object> acceptRequest(@RequestBody Map<String, Integer> param, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        Integer requestId = param.get("requestId");
        if (requestId == null) {
            map.put("code", 400);
            map.put("msg", "缺少申请ID");
            return map;
        }
        boolean success = sysUserService.acceptFriendRequest(requestId, loginUser.getId());
        if (success) {
            map.put("code", 200);
            map.put("msg", "已添加好友");
        } else {
            map.put("code", 500);
            map.put("msg", "操作失败");
        }
        return map;
    }

    // 拒绝好友申请
    @PostMapping("/rejectRequest")
    public Map<String, Object> rejectRequest(@RequestBody Map<String, Integer> param, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        Integer requestId = param.get("requestId");
        if (requestId == null) {
            map.put("code", 400);
            map.put("msg", "缺少申请ID");
            return map;
        }
        boolean success = sysUserService.rejectFriendRequest(requestId, loginUser.getId());
        if (success) {
            map.put("code", 200);
            map.put("msg", "已拒绝申请");
        } else {
            map.put("code", 500);
            map.put("msg", "操作失败");
        }
        return map;
    }

    // 删除好友
    @PostMapping("/deleteFriend")
    public Map<String, Object> deleteFriend(@RequestBody Map<String, Integer> param, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        Integer friendId = param.get("friendId");
        if (friendId == null) {
            map.put("code", 400);
            map.put("msg", "缺少好友ID");
            return map;
        }
        boolean success = sysUserService.deleteFriend(loginUser.getId(), friendId);
        if (success) {
            map.put("code", 200);
            map.put("msg", "删除好友成功");
        } else {
            map.put("code", 500);
            map.put("msg", "删除失败");
        }
        return map;
    }

    // 获取好友详情（含状态、照片等）
    @GetMapping("/friendDetail")
    public Map<String, Object> friendDetail(@RequestParam("friendId") Integer friendId, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        SysUser friend = sysUserService.getFriendDetail(loginUser.getId(), friendId);
        if (friend == null) {
            map.put("code", 404);
            map.put("msg", "好友不存在或不是好友关系");
            return map;
        }
        map.put("code", 200);
        map.put("data", friend);
        return map;
    }

    // 更新我的个人资料（状态/照片墙）
    @PostMapping("/updateProfile")
    public Map<String, Object> updateProfile(@RequestBody Map<String, String> payload, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        String status = payload.get("status");
        String photos = payload.get("photos");
        String recentActivity = payload.get("recentActivity");
        boolean success = sysUserService.updateMyProfile(loginUser.getId(), status, photos, recentActivity);
        if (success) {
            map.put("code", 200);
            map.put("msg", "更新成功");
        } else {
            map.put("code", 500);
            map.put("msg", "更新失败");
        }
        return map;
    }

    // ==================== 日记相关接口 ====================

    // 创建日记
    @PostMapping("/diary/create")
    public Map<String, Object> createDiary(@RequestBody Diary diary, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        diary.setUserId(loginUser.getId());
        int rows = sysUserService.createDiary(diary);
        map.put("code", rows > 0 ? 200 : 500);
        map.put("msg", rows > 0 ? "发布成功" : "发布失败");
        return map;
    }

    // 获取我的日记
    @GetMapping("/diary/my")
    public Map<String, Object> getMyDiaries(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        List<Diary> diaries = sysUserService.getMyDiaries(loginUser.getId());
        map.put("code", 200);
        map.put("data", diaries);
        return map;
    }

    // 获取公开日记
    @GetMapping("/diary/public")
    public Map<String, Object> getPublicDiaries(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        List<Diary> diaries = sysUserService.getPublicDiaries(loginUser.getId());
        map.put("code", 200);
        map.put("data", diaries);
        return map;
    }

    // 获取好友可见日记
    @GetMapping("/diary/friend")
    public Map<String, Object> getFriendDiaries(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        List<Diary> diaries = sysUserService.getFriendDiaries(loginUser.getId());
        map.put("code", 200);
        map.put("data", diaries);
        return map;
    }

    // 获取日记详情
    @GetMapping("/diary/detail")
    public Map<String, Object> getDiaryDetail(@RequestParam("id") Integer diaryId, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        Diary diary = sysUserService.getDiaryById(diaryId, loginUser.getId());
        if (diary == null) {
            map.put("code", 404);
            map.put("msg", "日记不存在或无权限");
            return map;
        }
        map.put("code", 200);
        map.put("data", diary);
        return map;
    }

    // 点赞/取消点赞
    @PostMapping("/diary/toggleLike")
    public Map<String, Object> toggleLike(@RequestBody Map<String, Integer> param, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        Integer diaryId = param.get("diaryId");
        boolean liked = sysUserService.toggleLike(diaryId, loginUser.getId());
        int count = sysUserService.getDiaryLikeCount(diaryId);

        map.put("code", 200);
        map.put("liked", liked);
        map.put("likeCount", count);
        return map;
    }

    // 添加评论
    @PostMapping("/diary/addComment")
    public Map<String, Object> addComment(@RequestBody DiaryComment comment, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        comment.setUserId(loginUser.getId());
        boolean success = sysUserService.addComment(comment);
        map.put("code", success ? 200 : 500);
        map.put("msg", success ? "评论成功" : "评论失败");
        return map;
    }

    // 获取日记的评论列表
    @GetMapping("/diary/comments")
    public Map<String, Object> getComments(@RequestParam("diaryId") Integer diaryId, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        List<DiaryComment> comments = sysUserService.getCommentsByDiaryId(diaryId, loginUser.getId());
        map.put("code", 200);
        map.put("data", comments);
        return map;
    }

    // 获取未读通知数（包含好友申请和日记互动）
    @GetMapping("/unreadNotificationCount")
    public Map<String, Object> unreadNotificationCount(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        int count = sysUserService.getUnreadNotificationCount(loginUser.getId());
        map.put("code", 200);
        map.put("data", count);
        return map;
    }

    // 获取通知列表
    @GetMapping("/notifications")
    public Map<String, Object> getNotifications(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        List<Notification> notifications = sysUserService.getNotifications(loginUser.getId(), 50);
        map.put("code", 200);
        map.put("data", notifications);
        return map;
    }

    // 标记通知已读
    @PostMapping("/notification/read")
    public Map<String, Object> markNotificationRead(@RequestBody Map<String, Integer> param, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        Integer notificationId = param.get("id");
        boolean success = sysUserService.markNotificationRead(notificationId, loginUser.getId());
        map.put("code", success ? 200 : 500);
        return map;
    }

    // 收藏/取消收藏
    @PostMapping("/diary/toggleFavorite")
    public Map<String, Object> toggleFavorite(@RequestBody Map<String, Integer> param, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        Integer diaryId = param.get("diaryId");
        boolean favorited = sysUserService.toggleFavorite(diaryId, loginUser.getId());
        int count = sysUserService.getDiaryFavoriteCount(diaryId);

        map.put("code", 200);
        map.put("favorited", favorited);
        map.put("favoriteCount", count);
        return map;
    }

    // 获取收藏日记列表
    @GetMapping("/diary/favorites")
    public Map<String, Object> getFavorites(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        List<Diary> diaries = sysUserService.getFavoriteDiaries(loginUser.getId());
        map.put("code", 200);
        map.put("data", diaries);
        return map;
    }

    // 切换评论点赞
    @PostMapping("/diary/toggleCommentLike")
    public Map<String, Object> toggleCommentLike(@RequestBody Map<String, Integer> param, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        Integer commentId = param.get("commentId");
        boolean liked = sysUserService.toggleCommentLike(commentId, loginUser.getId());
        int count = sysUserService.getCommentLikeCount(commentId);

        map.put("code", 200);
        map.put("liked", liked);
        map.put("likeCount", count);
        return map;
    }

    // 删除评论
    @PostMapping("/diary/deleteComment")
    public Result deleteComment(@RequestBody Map<String, Integer> map) {
        Integer commentId = map.get("commentId");
        boolean success = sysUserService.deleteComment(commentId);
        return success ? Result.success() : Result.error("删除失败");
    }
    // ==================== 删除日记接口 ====================
    @PostMapping("/diary/delete")
    public Map<String, Object> deleteDiary(
            @RequestBody Map<String, Integer> param,
            HttpSession session
    ) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");

        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }

        Integer diaryId = param.get("diaryId");
        if (diaryId == null) {
            map.put("code", 400);
            map.put("msg", "缺少日记ID");
            return map;
        }

        // 调用 service 删除
        boolean success = sysUserService.deleteDiary(diaryId, loginUser.getId());

        if (success) {
            map.put("code", 200);
            map.put("msg", "删除成功");
        } else {
            map.put("code", 500);
            map.put("msg", "删除失败，无权限或日记不存在");
        }
        return map;
    }
    // 上传照片
    @PostMapping(value = "/city/uploadPhoto", consumes = "multipart/form-data")
    public Result uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("province") String province,
            @RequestParam("city") String city,
            HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) return Result.error("未登录");
        CityPhoto photo = new CityPhoto();
        photo.setProvince(province);
        photo.setCity(city);
        photo.setUserId(user.getId().longValue());
        photo.setCreateTime(new Date());
        sysUserService.savePhoto(photo, file);
        return Result.success();
    }

    // 上传旅行记录
    @PostMapping("/city/uploadRecord")
    public Result uploadRecord(@RequestBody CityRecord record, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) return Result.error("未登录");
        record.setUserId(user.getId().longValue());
        record.setCreateTime(new Date());
        sysUserService.saveRecord(record);
        return Result.success();
    }

    // 上传攻略
    @PostMapping("/city/uploadStrategy")
    public Result uploadStrategy(@RequestBody CityStrategy strategy, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) return Result.error("未登录");
        strategy.setUserId(user.getId().longValue());
        strategy.setCreateTime(new Date());
        sysUserService.saveStrategy(strategy);
        return Result.success();
    }

    // 兼容前端路径
    @PostMapping(value = "/city/submitPhoto",consumes = "multipart/form-data")
    public Result submitPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("province") String province,
            @RequestParam("city") String city,
            HttpSession session){
        return uploadPhoto(file,province,city,session);
    }

    @PostMapping("/city/submitRecord")
    public Result submitRecord(@RequestBody CityRecord record, HttpSession session) {
        return uploadRecord(record, session);
    }

    @PostMapping("/city/submitStrategy")
    public Result submitStrategy(@RequestBody CityStrategy strategy, HttpSession session) {
        return uploadStrategy(strategy, session);
    }

    // ==============================
    // ✅ 已修复：返回带 userAvatar 的数据
    // ==============================
    @GetMapping("/city/photos")
    public Result getPhotos(@RequestParam String province, @RequestParam String city) {
        return Result.success(sysUserService.getPhotos(province, city));
    }

    @GetMapping("/city/records")
    public Result getRecords(@RequestParam String province, @RequestParam String city) {
        return Result.success(sysUserService.getRecords(province, city));
    }

    @GetMapping("/city/strategies")
    public Result getStrategies(@RequestParam String province, @RequestParam String city) {
        return Result.success(sysUserService.getStrategies(province, city));
    }

    // ============== 城市删除接口 ==============
    @PostMapping("/city/delPhoto")
    public Result delPhoto(@RequestParam Long id, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) return Result.error("未登录");
        return sysUserService.delPhoto(id, user.getId().longValue());
    }

    @PostMapping("/city/delRecord")
    public Result delRecord(@RequestParam Long id, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) return Result.error("未登录");
        return sysUserService.delRecord(id, user.getId().longValue());
    }

    @PostMapping("/city/delStrategy")
    public Result delStrategy(@RequestParam Long id, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) return Result.error("未登录");
        return sysUserService.delStrategy(id, user.getId().longValue());
    }
    // ==================== 相册相关接口 ====================

    // 上传照片
    @PostMapping("/photo/upload")
    public Map<String, Object> uploadPhoto(@RequestBody Map<String, String> payload, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        String imageUrl = payload.get("imageUrl");
        String description = payload.get("description");

        Photo photo = new Photo();
        photo.setUserId(loginUser.getId());
        photo.setImageUrl(imageUrl);
        photo.setDescription(description);

        int rows = sysUserService.uploadPhoto(photo);
        map.put("code", rows > 0 ? 200 : 500);
        map.put("msg", rows > 0 ? "上传成功" : "上传失败");
        if (rows > 0) {
            map.put("data", photo);
        }
        return map;
    }

    // 获取所有照片
    @GetMapping("/photo/list")
    public Map<String, Object> getPhotoList(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        List<Photo> photos = sysUserService.getAllPhotos(loginUser.getId());
        map.put("code", 200);
        map.put("data", photos);
        return map;
    }

    // 搜索照片（按人名）
    @GetMapping("/photo/searchByName")
    public Map<String, Object> searchPhotosByName(@RequestParam("keyword") String keyword, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        List<Photo> photos = sysUserService.searchPhotosByName(keyword, loginUser.getId());
        map.put("code", 200);
        map.put("data", photos);
        return map;
    }

    // 搜索照片（按时间）
    @GetMapping("/photo/searchByTime")
    public Map<String, Object> searchPhotosByTime(@RequestParam("date") String date, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        List<Photo> photos = sysUserService.searchPhotosByTime(date, loginUser.getId());
        map.put("code", 200);
        map.put("data", photos);
        return map;
    }

    // 获取照片详情
    @GetMapping("/photo/detail")
    public Map<String, Object> getPhotoDetail(@RequestParam("id") Integer photoId, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        Photo photo = sysUserService.getPhotoById(photoId, loginUser.getId());
        map.put("code", 200);
        map.put("data", photo);
        return map;
    }

    // 点赞/取消点赞照片
    @PostMapping("/photo/toggleLike")
    public Map<String, Object> togglePhotoLike(@RequestBody Map<String, Integer> param, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        Integer photoId = param.get("photoId");
        boolean liked = sysUserService.togglePhotoLike(photoId, loginUser.getId());
        map.put("code", 200);
        map.put("liked", liked);
        return map;
    }

    // 添加照片评论
    @PostMapping("/photo/addComment")
    public Map<String, Object> addPhotoComment(@RequestBody PhotoComment comment, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        comment.setUserId(loginUser.getId());
        boolean success = sysUserService.addPhotoComment(comment);
        map.put("code", success ? 200 : 500);
        map.put("msg", success ? "评论成功" : "评论失败");
        return map;
    }

    // 获取照片评论列表
    @GetMapping("/photo/comments")
    public Map<String, Object> getPhotoComments(@RequestParam("photoId") Integer photoId, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            return map;
        }
        List<PhotoComment> comments = sysUserService.getPhotoComments(photoId);
        map.put("code", 200);
        map.put("data", comments);
        return map;
    }
    // ==================== 相册删除接口 ====================

    // 删除评论
    @PostMapping("/photo/deleteComment")
    public Map<String, Object> deletePhotoComment(@RequestBody Map<String, Integer> param, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        Integer commentId = param.get("commentId");
        if (commentId == null) {
            map.put("code", 400);
            map.put("msg", "缺少评论ID");
            return map;
        }
        boolean success = sysUserService.deletePhotoComment(commentId, loginUser.getId());
        if (success) {
            map.put("code", 200);
            map.put("msg", "删除成功");
        } else {
            map.put("code", 500);
            map.put("msg", "删除失败，只能删除自己的评论");
        }
        return map;
    }

    // 删除照片
    @PostMapping("/photo/delete")
    public Map<String, Object> deletePhoto(@RequestBody Map<String, Integer> param, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            map.put("code", 401);
            map.put("msg", "未登录");
            return map;
        }
        Integer photoId = param.get("photoId");
        if (photoId == null) {
            map.put("code", 400);
            map.put("msg", "缺少照片ID");
            return map;
        }
        boolean success = sysUserService.deletePhoto(photoId, loginUser.getId());
        if (success) {
            map.put("code", 200);
            map.put("msg", "删除成功");
        } else {
            map.put("code", 500);
            map.put("msg", "删除失败，只能删除自己上传的照片");
        }
        return map;
    }
}