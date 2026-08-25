package com.fitnessai.platform.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fitnessai.platform.common.persistence.BaseEntity;

@TableName("sys_user")
public class User extends BaseEntity {
    private String username;
    private String passwordHash;
    private String nickname;
    private String email;
    private Integer status;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
