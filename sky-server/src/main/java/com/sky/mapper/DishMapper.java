package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 分页查询
     * @param dto
     * @return
     */
    Page<DishVO> selectDish(DishPageQueryDTO dto);
@Select("select * from dish where id=#{id}")
    Dish getbyID(Long id);
@Delete("delete from dish where id=#{id}")
    void delete(Long id);
@AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    List<Dish> list(Dish dish);
@Select("select  a.* from dish a left join setmeal_dish b on a.id=b.dish_id where setmeal_id=#{setmealId}")
    List<Dish> getBysetmealId(Long setmealId);
    Integer countByMap(Map map);
}
