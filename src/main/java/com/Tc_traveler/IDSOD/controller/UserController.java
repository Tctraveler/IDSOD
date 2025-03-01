package com.Tc_traveler.IDSOD.controller;


import com.Tc_traveler.IDSOD.dto.Result;
import com.Tc_traveler.IDSOD.entity.User;
import com.Tc_traveler.IDSOD.service.SFTPService;
import com.Tc_traveler.IDSOD.service.UserService;
import com.Tc_traveler.IDSOD.utils.JwtUtil;
import com.Tc_traveler.IDSOD.utils.Md5Util;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    // 注入UserService
    @Autowired
    private UserService userService;

    @Autowired
    private SFTPService sftpService;

    // 注册接口
    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^[a-zA-Z0-9_]{5,16}$",message = "用户名可以由字母、数字和下划线组成")String username,@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,16}$", message = "密码必须是8到16位长度的字母和数字组合") String firstPassword, String secondPassword){
        // 判断两次密码是否一致
        if(!firstPassword.equals(secondPassword)){
            return Result.error("两次密码不一致");
        }
        // 根据用户名查询用户
        User user = userService.findUserByUsername(username);
        // 如果用户已存在，返回错误信息
        if(user!=null){
            return Result.error("用户名已存在");
        }
        // 注册用户
        userService.register(username,firstPassword);
        // 返回成功信息
        return Result.success();
    }

    @PostMapping("/login")
    public Result login(@Pattern(regexp = "^[a-zA-Z0-9_]{5,16}$",message = "用户名可以由字母、数字和下划线组成")String username,@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,16}$", message = "密码必须是8到16位长度的字母和数字组合")String password){
        User user = userService.findUserByUsername(username);
        if(user==null){
            return Result.error("用户不存在");
        }
        if(Md5Util.getMD5String(password).equals(user.getPassword())){
            Map<String,Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            String token = JwtUtil.genToken(claims);
            return Result.success(token);
        }else {
            return Result.error("密码错误");
        }
    }

    @RequestMapping("/test")
    public Result test(){
        try {
            sftpService.uploadFile("D:\\123456.txt");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
        return Result.success();
    }

    @RequestMapping("/test2")
    public Result test2(){
        try {
            sftpService.executeCommand("python /root/autodl-tmp/res101_MultiScaleFusion/predict.py --left /root/autodl-tmp/0_left.jpg --right /root/autodl-tmp/0_right.jpg");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("预测失败");
        }
        return Result.success();
    }
}
