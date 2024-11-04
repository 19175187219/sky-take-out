package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorsMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServerImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
private DishFlavorsMapper dishFlavorsMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
@Autowired
    SetmealMapper setmealMapper;
    @Transactional//事务注解
   /**
    * 新增菜品
    *
 * @param dishDTO
    * @return void
    * @author zhuwanyi
    * @create 2024/10/15
    **/
    public void saveFlavorsAndDishes(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品插入一条数据
        dishMapper.insert(dish);
        //向口味插入n条数据
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&&flavors.size()>0){
           flavors.forEach(dishFlavor -> {
               dishFlavor.setDishId(id);
           });
            dishFlavorsMapper.inserrtBath(flavors);
        }

    }

    /**
     * 枫叶查询
     * @param dto
     * @return
     */
    @Transactional//事务注解
    public PageResult pageResult(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        Page<DishVO> page=dishMapper.selectDish(dto);
        return new PageResult(page.getTotal(),page.getResult());
    }


/**
 *
 *删除菜品
 * @param ids
 * @return void
 * @author zhuwanyi
 * @create 2024/10/15
 **/

@Transactional//事务注解
    public void delete(List<Long> ids) {
        //是否启售
        for (Long id : ids) {
            Dish dish=dishMapper.getbyID(id);
            if (dish.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //是否关联套餐
       List<Long> longs=  setmealDishMapper.getIds(ids);
        if (longs!=null&&longs.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
        }
        //删除菜品数据
        for (Long id : ids) {
            dishMapper.delete(id);
            //删除口味数据
        dishFlavorsMapper.delete(id);
        }

    }

    @Transactional
    public DishVO updateById(Long id) {
    Dish dish=dishMapper.getbyID(id);
   List<DishFlavor> dishFlavors=dishFlavorsMapper.updateById(id);
   DishVO dishVO=new DishVO();
  BeanUtils.copyProperties(dish,dishVO);
  dishVO.setFlavors(dishFlavors);
        return dishVO;
    }
@Transactional
    public void updateFlavorsAndDishes(DishDTO dishDTO) {
        //修改菜品基本信息
    Dish dish = new Dish();
    BeanUtils.copyProperties(dishDTO,dish);
    dishMapper.update(dish);
//删除原有口味
dishFlavorsMapper.delete(dishDTO.getId());
    //重新插入口味
    List<DishFlavor> flavors = dishDTO.getFlavors();
    if(flavors!=null && flavors.size()>0){
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dish.getId());
        });
        dishFlavorsMapper.inserrtBath(flavors);
    }

}

    @Override
    public List<Dish> list(Long categoryId) {

    Dish dish=Dish.builder().categoryId(categoryId)
            .status(StatusConstant.ENABLE).build();
        return dishMapper.list(dish);
    }

    @Transactional
    public void start(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        if (status == StatusConstant.DISABLE) {
            // 如果是停售操作，还需要将包含当前菜品的套餐也停售
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            // select setmeal_id from setmeal_dish where dish_id in (?,?,?)
            List<Long> setmealIds = setmealDishMapper.getIds(dishIds);
            if (setmealIds != null && setmealIds.size() > 0) {
                for (Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    @Override

    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            List<DishFlavor> flavors = dishFlavorsMapper.updateById(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

}

