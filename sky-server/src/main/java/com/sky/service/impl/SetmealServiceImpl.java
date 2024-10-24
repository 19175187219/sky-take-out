package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    /**
     * 
     *
     * @param dto
     * @return com.sky.result.PageResult
     * @author zhuwanyi
     * @create 2024/10/21
     **/

    @Override
    public PageResult pageResult(SetmealPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        Page<SetmealVO> page=setmealMapper.selectSrtmeal(dto);
        return new PageResult(page.getTotal(),page.getResult());

    }
@Override
    public void save(SetmealDTO DTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(DTO,setmeal);
        setmealMapper.insert(setmeal);
        Long id = setmeal.getId();
        List<SetmealDish> setmealDishes = DTO.getSetmealDishes();
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(id);
            });
            setmealMapper.inserrtBath(setmealDishes);
    }

    @Override
    public void delete(List<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getByid(id);
            if ((StatusConstant.ENABLE==setmeal.getStatus())) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            });
ids.forEach(

        setmealId -> {
            //删除套餐
            setmealMapper.deleteByid(setmealId);
         //删除套餐关联
            setmealMapper.deleteBYsrmealId(setmealId);
        }
);
    }
/**
 * id查询
 *
 * @param id
 * @return com.sky.vo.SetmealVO
 * @author zhuwanyi
 * @create 2024/10/21
 **/

@Override
    public SetmealVO getByIdwithDish(Long id) {
        Setmeal setmeal=setmealMapper.getByid(id);
       List<SetmealDish> setmealDishes =setmealMapper.getBysetmealId(id);
    SetmealVO setmealVO = new SetmealVO();
    BeanUtils.copyProperties(setmeal,setmealVO);
    setmealVO.setSetmealDishes(setmealDishes);
    return setmealVO;
}
/**
 * 修改
 *
 * @param setmealDTO
 * @return void
 * @author zhuwanyi
 * @create 2024/10/21
 **/

@Override
    public void updateFlavorsAndDishes(SetmealDTO setmealDTO) {

    Setmeal setmeal = new Setmeal();
    BeanUtils.copyProperties(setmealDTO,setmeal);
    setmealMapper.update(setmeal);
    Long id = setmealDTO.getId();
    setmealMapper.deleteBYsrmealId(id);//删除套餐
    List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
    setmealDishes.forEach(setmealDish -> {
        setmealDish.setDishId(id);
    });
    setmealMapper.inserrtBath(setmealDishes);//重新插入套餐和菜品关联
}

    @Override
    public void start(Integer status, Long id) {
        if (status.equals(StatusConstant.ENABLE)){ //启用
           List<Dish> dishes= dishMapper.getBysetmealId(id);
           if (dishes.size()>0 && dishes!=null){//判断套餐内是否有菜品
               dishes.forEach(dish -> {
                   if (StatusConstant.DISABLE.equals(dish.getStatus())){//判断套餐内是否包含未起售菜品
                      throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                   }
               });
           }
        }
        //如果停售直接更新
        Setmeal setmeal=Setmeal.builder().id(id).status(status).build();
        setmealMapper.update(setmeal);

    }
}
