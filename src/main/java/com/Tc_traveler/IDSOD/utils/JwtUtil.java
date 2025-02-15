package com.Tc_traveler.IDSOD.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class JwtUtil {
    // 定义一个私有的静态常量，用于存储密钥
    private static final String SECRET_KEY;
    // 定义一个私有的静态常量，用于存储过期时间
    private static final long EXPIRE_TIME;
    // 静态代码块，用于加载配置文件
    static {
        try(InputStream input = JwtUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            // 创建一个Properties对象
            Properties properties = new Properties();
            // 加载配置文件
            properties.load(input);
            // 从配置文件中获取密钥
            SECRET_KEY = properties.getProperty("jwt.key");
            //配置文件里面写long类型的数据的时候不需要后面加L
            EXPIRE_TIME = Long.parseLong(properties.getProperty("jwt.expiration"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // 生成token
    public static String genToken(Map<String,Object> claims) {
        // 创建JwtBuilder对象
        JwtBuilder jwtBuilder = Jwts.builder()
                // 设置token中的声明
                .setClaims(claims)
                // 设置token的签发时间
                .setIssuedAt(new Date())
                // 设置token的过期时间
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRE_TIME))
                // 设置token的主题
                .setSubject("all")
                // 使用HS256算法进行签名
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY);
        // 返回生成的token
        return jwtBuilder.compact();
    }

    // 解析token
    public static Map<String,Object> parseToken(String token){
        // 创建Jwts的解析器
        return Jwts.parser()
                // 设置签名密钥
                .setSigningKey(SECRET_KEY)
                // 解析token
                .parseClaimsJws(token)
                // 获取token中的声明
                .getBody();
    }
}
