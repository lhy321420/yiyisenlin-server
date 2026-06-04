package com.example.demo.mapper;

import com.example.demo.entity.CityPhoto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CityPhotoMapper {
    int insert(CityPhoto photo);
    CityPhoto selectById(Long id);
    void deleteById(Long id);
    List<CityPhoto> selectByProvinceAndCity(
            @Param("province") String province,
            @Param("city") String city
    );
}