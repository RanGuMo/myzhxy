package com.atguigu.myzhxy.service.impl;

import com.atguigu.myzhxy.mapper.ClazzMapper;
import com.atguigu.myzhxy.pojo.Clazz;
import com.atguigu.myzhxy.service.ClazzService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("clazzServiceImpl")
@Transactional
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {
    /***
     * 分页查询所有班级信息【带条件】
     * @param page
     * @param clazz
     * @return
     */
    @Override
    public IPage<Clazz> getClazzByOpr(Page<Clazz> page, Clazz clazz) {
        QueryWrapper<Clazz> queryWrapper = new QueryWrapper<>();
       if(clazz!=null) {
           //班级名称条件 拼接
           String name = clazz.getName();
           if (!StringUtils.isEmpty(name)) {
               queryWrapper.eq("name", name);
           }

           //年级名称条件 拼接
           String gradeName = clazz.getGradeName();
           if (!StringUtils.isEmpty(gradeName)) {
               queryWrapper.eq("grade_name", gradeName);
           }
           //排序 条件
           queryWrapper.orderByDesc("id");
           queryWrapper.orderByAsc("name");
       }
        Page clazzPage = baseMapper.selectPage(page, queryWrapper);
        return clazzPage;
    }
}
