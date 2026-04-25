package com.middleware.org.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String account;

    private String email;

    private String phone;

    private String name;

    private String nickname;

    private String password;

    /**
     * 用户角色：USER=普通用户，ADMIN=管理员
     */
    private String role = "USER";

    /**
     * 账户启用状态：true=启用，false=禁用
     */
    private Boolean enabled = true;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
