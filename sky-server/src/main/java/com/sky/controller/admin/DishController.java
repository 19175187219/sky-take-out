package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增菜品
     * @param dishDTO
     */
    @ApiOperation("新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("xzcp{}",dishDTO.toString());
        dishService.saveFlavorsAndDishes(dishDTO);
        //清理缓存
        String key="dish_"+dishDTO.getCategoryId();
        extracted(key);
        return Result.success();
    }

    /**
     * 菜品分查询
     * @param dto
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageResultResult(DishPageQueryDTO dto){
        log.info("xzcp{}",dto.toString());
       PageResult pageResult = dishService.pageResult(dto);
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
        dishService.delete(ids);
        extracted("dish_*");
        return Result.success();
    }
    /**
     *
     *id回显查询菜品
     * @param id
     * @return com.sky.result.Result<com.sky.vo.DishVO>
     * @author zhuwanyi
     * @create 2024/10/15
     **/

    @GetMapping("/{id}")
@ApiOperation("id查询菜品")
    public Result<DishVO> update(@PathVariable Long id){
        log.info("xzcp{}","菜品");
    DishVO dishVO= dishService.updateById(id);
    return Result.success(dishVO);
    }
/**
 * 修改菜品
 *
 * @param dishDTO
 * @return com.sky.result.Result
 * @author zhuwanyi
 * @create 2024/10/15
 **/

@ApiOperation("修改菜品")
    @PutMapping
    public Result updatea(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品{}",dishDTO.toString());
        dishService.updateFlavorsAndDishes(dishDTO);
    extracted("dish_*");
        return Result.success();
    }
    /**
     *
     *
     * @param categoryId
     * @return com.sky.result.Result<java.util.List < com.sky.entity.Dish>>
     * @author zhuwanyi
     * @create 2024/10/21
     **/

    @GetMapping("/list")
    @ApiOperation("分类id查询菜品")
    public Result<List<Dish>> listResult(Long categoryId){
        log.info("xzcp{}","菜品");
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }
    /**
     *
     *
     * @param status
     * @param id
     * @return com.sky.result.Result
     * @author zhuwanyi
     * @create 2024/10/21
     **/

    @ApiOperation("起售停售")
    @PostMapping("/status/{status}")
    public Result start(@PathVariable Integer status,Long id) {
        dishService.start(status,id);
        extracted("dish_*");
        return Result.success();
    }
/**
 * 清理缓存
 *
 * @param pattern
 * @return void
 * @author zhuwanyi
 * @create 2024/10/29
 **/

private void extracted(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
