package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    public void saveFlavorsAndDishes(DishDTO dishDTO);

    PageResult pageResult(DishPageQueryDTO dto);

    void delete(List<Long> ids);

    DishVO updateById(Long id);

    void updateFlavorsAndDishes(DishDTO dishDTO);

    List<Dish> list(Long categoryId);

    void start(Integer status, Long id);
    List<DishVO> listWithFlavor(Dish dish);

}
