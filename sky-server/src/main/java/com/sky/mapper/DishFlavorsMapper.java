package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorsMapper {
    /**
     * 口味添加
     * @param flavors
     */
    void inserrtBath(List<DishFlavor> flavors);
@Delete("delete from dish_flavor where dish_id=#{dishid}")
    void delete(Long dishid);
}
