package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "c端购物车相关接口")
@Slf4j
public class ShopCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     * 添加购物车
     *
 * @param shoppingCartDTO
     * @return com.sky.result.Result
     * @author zhuwanyi
     * @create 2024/11/4
     **/

    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车：{}",shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }
@GetMapping("/list")
@ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> select(){
    List<ShoppingCart> list=  shoppingCartService.showShoppingCart();
    return Result.success(list);
    }

    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result clean(){
        shoppingCartService.deleteShoppingCart();
        return Result.success();
    }
    @PostMapping("/sub")
    @ApiOperation("删除购物车某个商品")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }
}
