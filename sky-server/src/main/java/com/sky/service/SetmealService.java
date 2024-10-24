package com.sky.service;

import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    PageResult pageResult(SetmealPageQueryDTO dto);

    void save(SetmealDTO DTO);

    void delete(List<Long> ids);

    SetmealVO getByIdwithDish(Long id);

    void updateFlavorsAndDishes(SetmealDTO setmealDTO);

    void start(Integer status, Long id);
}
