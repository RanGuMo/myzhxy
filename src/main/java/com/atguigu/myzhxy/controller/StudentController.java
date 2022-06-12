package com.atguigu.myzhxy.controller;


import com.atguigu.myzhxy.pojo.Student;
import com.atguigu.myzhxy.service.StudentService;
import com.atguigu.myzhxy.util.MD5;
import com.atguigu.myzhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "学生管理控制器")
@RestController
@RequestMapping("/sms/studentController")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @ApiOperation("删除一个或者批量删除学生信息")
    @DeleteMapping("/delStudentById")
    public Result delStudentById(
            @ApiParam("要删除的学生id的Json数组")@RequestBody List<Integer> ids
    ){
        studentService.removeByIds(ids);
        return Result.ok();
    }


    @ApiOperation("添加或修改学生信息")
    @PostMapping("/addOrUpdateStudent")
    public Result addOrUpdateStudent(
            @ApiParam("Json转换后段Student数据模型") @RequestBody Student student
    ){
        //如果新增学生信息，需要对密码进行加密
        if (!StringUtils.isEmpty(student.getPassword())){
            student.setPassword(MD5.encrypt(student.getPassword()));
        }
        //保存学生信息进入数据库
        studentService.saveOrUpdate(student);
        return Result.ok();

    }



    // /sms/studentController/getStudentByOpr/1/3
    @ApiOperation("查询学生信息，分页带条件")
    @GetMapping("/getStudentByOpr/{pageNo}/{pageSize}")
    public Result getStudentByOpr(
            @ApiParam("页码数") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("页大小") @PathVariable("pageSize") Integer pageSize,
            @ApiParam("查询条件转换后的后端模型") Student student
    ){
        //准备 分页信息封装的page对象
        Page<Student> page = new Page<>(pageNo,pageSize);
        //获取分页的学生信息
        IPage<Student> iPage = studentService.getStudentByOpr(page,student);
        return  Result.ok(iPage);

    }


}
