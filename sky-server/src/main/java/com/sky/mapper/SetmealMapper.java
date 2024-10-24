package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    Page<SetmealVO> selectSrtmeal(SetmealPageQueryDTO dto);
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    void inserrtBath(List<SetmealDish> setmealDishes);
@Select("select * from setmeal where id=#{id}")
    Setmeal getByid(Long id);
    @Delete("delete  from setmeal where id=#{id}")
    void deleteByid(Long setmealId);
    @Delete("delete  from setmeal_dish where setmeal_id=#{setmealId}")
    void deleteBYsrmealId(Long setmealId);
@Select("select  * from setmeal_dish where setmeal_id=#{setmealId};")
    List<SetmealDish> getBysetmealId(Long setmealId);
@AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);
    @Select("select  * from setmeal_dish where setmeal_id=#{setmealId};")
    SetmealDish getBysetmealI(Long id);

}
