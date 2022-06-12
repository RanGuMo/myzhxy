package com.atguigu.myzhxy.controller;


import com.atguigu.myzhxy.pojo.Admin;
import com.atguigu.myzhxy.pojo.LoginForm;
import com.atguigu.myzhxy.pojo.Student;
import com.atguigu.myzhxy.pojo.Teacher;
import com.atguigu.myzhxy.service.AdminService;
import com.atguigu.myzhxy.service.StudentService;
import com.atguigu.myzhxy.service.TeacherService;
import com.atguigu.myzhxy.util.CreateVerifiCodeImage;
import com.atguigu.myzhxy.util.JwtHelper;
import com.atguigu.myzhxy.util.Result;
import com.atguigu.myzhxy.util.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Api(tags = "系统控制器")
@RestController
@RequestMapping("/sms/system")
public class SystemController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;


    @ApiOperation("头像上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result headerImgUpload(
            @ApiParam("文件二进制数据")@RequestPart("multipartFile") MultipartFile multipartFile
    ) {
       //使用UUID 随机生成文件名
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        //生成新的文件名字
        //String newFileName = uuid.concat(multipartFile.getOriginalFilename());
        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String newFileName = uuid.concat(originalFilename.substring(i));
        //生成文件的保持路径（实际生产环境中 这里会使用真正的文件存储服务器）注意upload 后面要加 /
        String portraitPath ="D:/java/IDEACode/myzhxy/target/classes/public/upload/".concat(newFileName);
        //保存文件
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //响应图片路径

        String headerImg = "upload/".concat(newFileName);
        return Result.ok(headerImg);
    }



    @ApiOperation("通过token获取用户信息")
    @GetMapping("/getInfo")
    public Result getInfoByToken(@RequestHeader("token") String token){
        boolean expiration = JwtHelper.isExpiration(token);//先查看token 是否过期
        if (expiration){ //如果过期
            return Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }
        //从token 中解析出用户id 和 用户类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        Map<String,Object> map = new LinkedHashMap<>();
        switch (userType){
            case 1:
                Admin admin = adminService.getAdminById(userId);
                map.put("userType",1);
                map.put("user",admin);
                break;
            case 2:
                Student student = studentService.getAdminById(userId);
                map.put("userType",2);
                map.put("user",student);
                break;
            case 3:
                Teacher teacher = teacherService.getAdminById(userId);
                map.put("userType",3);
                map.put("user",teacher);
                break;
        }
        return Result.ok(map);
}
    @ApiOperation("登录请求验证")
    @PostMapping("/login")
    public Result login(@RequestBody LoginForm loginForm, HttpServletRequest request){
       //验证码校验
        HttpSession session = request.getSession();
        String sessionVerifiCode = (String)session.getAttribute("verifiCode");
        String loginverifiCode = loginForm.getVerifiCode();
        if ("".equals(sessionVerifiCode) || sessionVerifiCode ==null ){
            return Result.fail().message("验证码失效，请刷新后重试！");
        }
        if (!sessionVerifiCode.equalsIgnoreCase(loginverifiCode)){ //equalsIgnoreCase 忽略大小写
            return Result.fail().message("验证码有误，请小心输入后重试");
        }
        // 验证通过后，从session 域中 移除现有的验证码
        session.removeAttribute("verifiCode");
        //分用户类型进行校验（看是管理员，还是学生，还是教师）

        //准备一个map 用于存放响应的数据
        Map<String,Object> map = new LinkedHashMap<>();
        switch(loginForm.getUserType()){
            case 1:
                try {
                    Admin admin = adminService.login(loginForm);
                    if (admin != null){
                        //用户类型+用户id 转换为一个密文，以token 的名称向客户端反馈
                        map.put("token", JwtHelper.createToken(admin.getId().longValue(),1));
                    }else{
                        throw new RuntimeException("用户名或者密码错误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }

            case 2:
                try {
                    Student student = studentService.login(loginForm);
                    if (student != null){
                        map.put("token", JwtHelper.createToken(student.getId().longValue(),2));
                    }else{
                        throw new RuntimeException("用户名或者密码错误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }

            case 3:
                try {
                    Teacher teacher = teacherService.login(loginForm);
                    if (teacher != null){
                        map.put("token", JwtHelper.createToken(teacher.getId().longValue(),3));
                    }else{
                        throw new RuntimeException("用户名或者密码错误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }

        }
        return Result.fail().message("查无此用户！！！");


    }
     @ApiOperation("获取验证码图片")
     @GetMapping("/getVerifiCodeImage")
     public void getVerifiCodeImage(HttpServletRequest request, HttpServletResponse response){
         //1.获取生成的图片
         BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();
         //2.获取图片上的验证码,并转换为字符串格式
         String verifiCode = new String(CreateVerifiCodeImage.getVerifiCode());
         //3.将验证码文本放入到seesion 域中，为下一次验证做准备
         HttpSession session = request.getSession();
         session.setAttribute("verifiCode",verifiCode);
         //4.将验证码图片响应给浏览器
         try {
             ImageIO.write(verifiCodeImage,"JPEG",response.getOutputStream());
         } catch (IOException e) {
             e.printStackTrace();
         }

     }


}
