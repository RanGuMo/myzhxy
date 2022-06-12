package com.atguigu.myzhxy.controller;


import com.atguigu.myzhxy.pojo.Grade;
import com.atguigu.myzhxy.service.GradeService;
import com.atguigu.myzhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "年级管理控制器")
@RestController
@RequestMapping("/sms/gradeController")
public class GradeController {
    @Autowired
    private GradeService gradeService;

    @ApiOperation("获取所有Grade信息")
    @GetMapping("/getGrades")
    public Result getGrades(){
        List<Grade> grades = gradeService.list();
        return Result.ok(grades);
    }

    @ApiOperation("删除一个或者多个grade信息")
    @DeleteMapping("/deleteGrade")
    public Result deleteGrade( @ApiParam("JSON的年级id集合,映射为后台List<Integer>")
                                   @RequestBody List<Integer> ids){
        gradeService.removeByIds(ids);
        return Result.ok();
    }

    @ApiOperation("添加或者修改年级信息")
    @PostMapping("/saveOrUpdateGrade")
    public Result saveOrUpdateGarde(@ApiParam("JSON的grade对象转换后台数据模型")
                                        @RequestBody Grade grade){
        //调用服务层方法，实现添加或者修改年级信息
        gradeService.saveOrUpdate(grade);
        return Result.ok();
    }


    @ApiOperation("查询年级信息,分页带条件")
    @GetMapping("getGrades/{pageNo}/{pageSize}")
    public Result GradeByOpr(
            @ApiParam("分页查询页码数") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页查询页大小") @PathVariable("pageSize")Integer pageSize,
            @ApiParam("分页查询模糊匹配班级名") String gradeName
            ){
        //设置分页信息
        Page<Grade> page = new Page<>(pageNo,pageSize);
        // 调用服务层方法，传入分页信息 和 模糊查询的条件
        IPage<Grade> pageRs= gradeService.getGradeByOpr(page,gradeName);
        return Result.ok(pageRs);
    }

}
