package com.example.demo.mapper;

import com.example.demo.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysUserMapper {
    List<SysUser> listAll();
    SysUser getByUsername(String username);
    int insert1(SysUser user);
    int updateInfo(SysUser user);
    SysUser getById(@Param("id") Integer id);
    int updateAvatar(@Param("id") Integer id, @Param("avatar") String avatar);
    List<SysUser> getUsersByIds(@Param("ids") List<Integer> ids);
    List<SysUser> searchUsers(@Param("keyword") String keyword, @Param("excludeIds") List<Integer> excludeIds);
}