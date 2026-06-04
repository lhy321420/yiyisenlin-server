package com.example.demo.mapper;

import com.example.demo.entity.CityStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CityStrategyMapper {
    int insert(CityStrategy strategy);
    CityStrategy selectById(Long id);
    void deleteById(Long id);
    List<CityStrategy> selectByProvinceAndCity(
            @Param("province") String province,
            @Param("city") String city
    );
}