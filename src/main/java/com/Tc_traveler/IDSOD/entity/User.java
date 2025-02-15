package com.Tc_traveler.IDSOD.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class User {
    // 用户ID
    private Integer id;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 创建时间
    private LocalDateTime create_time;
    // 更新时间
    private LocalDateTime update_time;
}
