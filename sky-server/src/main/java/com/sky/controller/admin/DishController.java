package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品")
@Slf4j
public class DishController {
    @Autowired
    private DishServer dishServer;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @ApiOperation("新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("xzcp{}",dishDTO.toString());
        dishServer.saveFlavorsAndDishes(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分类查询
     * @param dto
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageResultResult(DishPageQueryDTO dto){
        log.info("xzcp{}",dto.toString());
       PageResult pageResult =dishServer.pageResult(dto);
       return Result.success(pageResult);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
@ApiOperation("删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("xzcp{}",ids.toString());
        dishServer.delete(ids);
        return Result.success();
    }
}
