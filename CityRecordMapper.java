package com.example.demo.mapper;

import com.example.demo.entity.CityRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CityRecordMapper {
    int insert(CityRecord record);
    CityRecord selectById(Long id);
    void deleteById(Long id);
    List<CityRecord> selectByProvinceAndCity(
            @Param("province") String province,
            @Param("city") String city
    );
}