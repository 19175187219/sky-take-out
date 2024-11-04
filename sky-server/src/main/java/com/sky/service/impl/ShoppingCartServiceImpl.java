package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {


    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前加入购物车的商品是否存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart); //属性拷贝
        Long userId = BaseContext.getCurrentId();  //拦截器获取到的用户id
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //如果已经存在了，只需要将数量加一
        if(list!=null&&list.size()>0){
            //这里list要么没有数据，要么只有一条数据
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1); //update shopping_cart set number=?where id=?
            shoppingCartMapper.updateNumberById(cart);
        }else {
            //如果不存在，需要插入一条购物车数据
            /**
             * 判断这次添加到购物车的是菜品还是套餐
             */
            Long dishId = shoppingCartDTO.getDishId();
            if(dishId!=null){
                //本次添加是菜品
                Dish dish = dishMapper.getbyID(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{
                //本次添加的是套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getByid(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            //统一插入数据
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long id = BaseContext.getCurrentId();
        ShoppingCart build = ShoppingCart.builder()
                .userId(id)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(build);
        return list;
    }
/**
 * 清空购物车
 *
 * @return void
 * @author zhuwanyi
 * @create 2024/11/4
 **/

@Override
    public void deleteShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByMyId(userId);
    }
/**
 * 删除购物车某个菜品
 *
 * @return void
 * @author zhuwanyi
 * @create 2024/11/4
 **/

@Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
    //判断当前加入购物车的商品是否存在
    ShoppingCart shoppingCart = new ShoppingCart();
    BeanUtils.copyProperties(shoppingCartDTO,shoppingCart); //属性拷贝
    Long userId = BaseContext.getCurrentId();  //拦截器获取到的用户id
    shoppingCart.setUserId(userId);
    List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
    if (list.size()>0 && list!=null){

            shoppingCart = list.get(0);
            Integer number = shoppingCart.getNumber();
            if (number==1){
                shoppingCartMapper.deleteShoppingId(shoppingCart.getId());//1份直接删除
            }

        else{
        shoppingCart.setNumber(shoppingCart.getNumber() - 1);
        shoppingCartMapper.updateNumberById(shoppingCart);//不为一份修改
        }}
    }
    }
