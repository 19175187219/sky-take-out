package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishServer;
import com.sky.vo.DishVO;
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
     * 菜品分查询
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
    DishVO dishVO= dishServer.updateById(id);
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
        dishServer.updateFlavorsAndDishes(dishDTO);
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
        List<Dish> list = dishServer.list(categoryId);
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
        dishServer.start(status,id);
        return Result.success();
    }
}
