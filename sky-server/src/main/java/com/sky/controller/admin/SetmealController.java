package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishServer;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishServer dishServer;
    /**
     *
     *
     * @param dto
     * @return com.sky.result.Result<com.sky.result.PageResult>
     * @author zhuwanyi
     * @create 2024/10/21
     **/

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageResultResult(SetmealPageQueryDTO dto){
        log.info("xzcp{}",dto.toString());
        PageResult pageResult =setmealService.pageResult(dto);
        return Result.success(pageResult);
    }
/**
 *
 *
 * @param DTO
 * @return com.sky.result.Result
 * @author zhuwanyi
 * @create 2024/10/21
 **/

@ApiOperation("新增套餐")
    @PostMapping
    public Result save(@RequestBody SetmealDTO DTO) {
        log.info("xzcp{}",DTO.toString());
        setmealService.save(DTO);
        return Result.success();
    }
    /**
     *
     *
     * @param ids
     * @return com.sky.result.Result
     * @author zhuwanyi
     * @create 2024/10/21
     **/

    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("{}",ids.toString());
        setmealService.delete(ids);
        return Result.success();
    }
    /**
     *
     *
     * @param id
     * @return com.sky.result.Result<com.sky.vo.DishVO>
     * @author zhuwanyi
     * @create 2024/10/21
     **/

    @GetMapping("/{id}")
    @ApiOperation("id查询菜品")
    public Result<SetmealVO> update(@PathVariable Long id){
        log.info("xzcp{}","菜品");
        SetmealVO setmealVO= setmealService.getByIdwithDish(id);
        return Result.success(setmealVO);
    }
    /**
 *
 *
 * @param setmealDTO
 * @return com.sky.result.Result
 * @author zhuwanyi
 * @create 2024/10/21
 **/

@ApiOperation("修改菜品")
    @PutMapping
    public Result updatea(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改菜品{}",setmealDTO.toString());
        setmealService.updateFlavorsAndDishes(setmealDTO);
        return Result.success();
    }

    @ApiOperation("起售停售")
    @PostMapping("/status/{status}")
    public Result start(@PathVariable Integer status,Long id) {
    setmealService.start(status,id);
    return Result.success();
    }
}
