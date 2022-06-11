package com.atguigu.myzhxy.service.impl;

import com.atguigu.myzhxy.mapper.StudentMapper;
import com.atguigu.myzhxy.pojo.LoginForm;
import com.atguigu.myzhxy.pojo.Student;
import com.atguigu.myzhxy.service.StudentService;
import com.atguigu.myzhxy.util.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("studentServiceImpl")
@Transactional
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Override
    public Student login(LoginForm loginForm) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        //拼接 用户名 和 密码
        queryWrapper.eq("name",loginForm.getUsername());
        queryWrapper.eq("password", MD5.encrypt(loginForm.getPassword()));

        Student student = baseMapper.selectOne(queryWrapper);
        return student;
    }

    @Override
    public Student getAdminById(Long userId) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",userId);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 按条件查询学生信息【带分页】
     * @param page
     * @param student
     * @return
     */
    @Override
    public IPage<Student> getStudentByOpr(Page<Student> page, Student student) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        if (student!=null){
            if (!StringUtils.isEmpty(student.getName())){
                queryWrapper.like("name",student.getName());
            }
            if (!StringUtils.isEmpty(student.getClazzName())){
                queryWrapper.eq("clazz_name",student.getClazzName());
            }
            queryWrapper.orderByDesc("id");
            queryWrapper.orderByAsc("name");
        }
        //创建分页对象
        Page studentPage = baseMapper.selectPage(page, queryWrapper);

        return studentPage;
    }
}
