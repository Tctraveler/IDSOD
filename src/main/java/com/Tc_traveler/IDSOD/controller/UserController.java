package com.Tc_traveler.IDSOD.controller;


import com.Tc_traveler.IDSOD.dto.Result;
import com.Tc_traveler.IDSOD.entity.User;
import com.Tc_traveler.IDSOD.service.SFTPService;
import com.Tc_traveler.IDSOD.service.UserService;
import com.Tc_traveler.IDSOD.utils.JwtUtil;
import com.Tc_traveler.IDSOD.utils.Md5Util;
import com.Tc_traveler.IDSOD.utils.ThreadLocalUtil;
import com.jcraft.jsch.*;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Value("${savePath}")
    private String localSavePath;

    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private int port;

    @Value("${sftp.username}")
    private String name;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.firstRemote-dir}")
    private String firstRemoteDir;

    @Value("${sftp.secondRemote-dir}")
    private String secondRemoteDir;

    @Value("${sftp.thirdRemote-dir}")
    private String thirdRemoteDir;

    @Value("${sftp.fourthRemote-dir}")
    private String fourthRemoteDir;

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
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
        return Result.success();
    }

//    @RequestMapping("/test2")
//    public Result test2(){
//        try {
//            sftpService.executeCommand("python /root/autodl-tmp/res101_MultiScaleFusion/predict.py --left /root/autodl-tmp/0_left.jpg --right /root/autodl-tmp/0_right.jpg");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.error("预测失败");
//        }
//        return Result.success();
//    }

    @PostMapping("/uploadPics")
    public Result uploadPics(@RequestParam(name = "file1") MultipartFile file1,@RequestParam(name = "file2") MultipartFile file2){
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        Integer id = (Integer) map.get("id");
        if(file1==null||file2==null){
            return Result.error("请上传完整的双眼照片");
        }
        if(file1.getSize()>1024*1024*10||file2.getSize()>1024*1024*10){
            return Result.error("单个文件大小不能大于10MB");
        }
        String suffix1 = file1.getOriginalFilename().substring(file1.getOriginalFilename().lastIndexOf(".")+1);
        String suffix2 = file2.getOriginalFilename().substring(file2.getOriginalFilename().lastIndexOf(".")+1);
        if(!"jpg".equalsIgnoreCase(suffix1)||!"jpg".equalsIgnoreCase(suffix2)){
            return Result.error("请选择jpg格式的图片");
        }
        File folder = new File(localSavePath);
        if(!folder.exists()){
            if(!folder.mkdirs()){
                return Result.error("文件夹创建失败");
            }
        }
        String filename1 = folder+"\\"+username+"leftEye.jpg", filename2 = folder+"\\"+username+"rightEye.jpg";
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            session = jsch.getSession(name,host,port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            file1.transferTo(new File(filename1));
            file2.transferTo(new File(filename2));
            sftpService.firstUploadFile(filename1,channelSftp);//上传需要亮度增强文件
            sftpService.firstUploadFile(filename2,channelSftp);
            sftpService.deleteAllFile(folder);//删除本地图片
            if(!folder.exists()){
                if(!folder.mkdirs()){
                    return Result.error("文件夹创建失败");
                }
            }
            sftpService.executeCommand("python /root/autodl-tmp/CSSE/CSEC-main/src/test.py checkpoint_path=/root/autodl-tmp/CSSE/CSEC-main/pretained/csec.ckpt");//执行亮度增强命令
            sftpService.deleteFile(firstRemoteDir+"/"+username+"leftEye.jpg",channelSftp);//删除已经亮度增强前的图片
            sftpService.deleteFile(firstRemoteDir+"/"+username+"rightEye.jpg",channelSftp);
            sftpService.downloadFile(secondRemoteDir+"/csecnet_pretained_csec.ckpt@lcdp_data.test"+"/"+username+"leftEye.jpg",channelSftp);//下载亮度增强后的文件
            sftpService.downloadFile(secondRemoteDir+"/csecnet_pretained_csec.ckpt@lcdp_data.test"+"/"+username+"rightEye.jpg",channelSftp);
            sftpService.deleteFolder(secondRemoteDir);//删除远程多余文件
//            sftpService.secondUploadFile(filename1,channelSftp);//上传需要预测的文件
//            sftpService.secondUploadFile(filename2,channelSftp);
//            sftpService.deleteAllFile(folder);//删除本地图片
//            if(!folder.exists()){
//                if(!folder.mkdirs()){
//                    return Result.error("文件夹创建失败");
//                }
//            }
//            sftpService.executeCommand("python /root/autodl-tmp/res101_MultiScaleFusion/predict.py --left "+thirdRemoteDir+"/"+username+"leftEye.jpg"+" --right "+thirdRemoteDir+"/"+username+"rightEye.jpg");//执行预测命令
//            sftpService.deleteFile(thirdRemoteDir+"/"+username+"leftEye.jpg",channelSftp);//删除已经预测前的文件
//            sftpService.deleteFile(thirdRemoteDir+"/"+username+"rightEye.jpg",channelSftp);
//            sftpService.downloadFile(fourthRemoteDir+"/result.txt",channelSftp);//下载预测结果
//            sftpService.deleteFile(fourthRemoteDir+"/result.txt",channelSftp);//删除远程预测结果
//            BufferedReader reader = new BufferedReader(new FileReader(folder+"\\result.txt"));
//            String line = null;
//            StringBuilder result = new StringBuilder();
//            while ((line = reader.readLine()) != null) {
//                if(line.startsWith("- ")){
//                    result.append(line.substring(2)).append(" ");
//                }
//            }
//            userService.addConsequence(id,result);
//            sftpService.deleteAllFile(folder);
//            if(!folder.exists()){
//                if(!folder.mkdirs()){
//                    return Result.error("文件夹创建失败");
//                }
//            }
        }catch (SftpException | JSchException e){
            e.printStackTrace();
            return Result.error("远程文件io异常");
        }catch (IOException e){
            e.printStackTrace();
            return Result.error("本地文件io异常");
        }catch (InterruptedException e){
            e.printStackTrace();
            return Result.error("线程异常");
        }finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        return Result.success();
    }
}
