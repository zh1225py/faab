package com.example.faab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)//在自增主键的变量加上即可
    private Integer fileId;

    private String username;

    private String password;

    private Boolean sex;

    private String email;

    private String phone;

    @Override
    public String toString() {
        return  "{username:'" + username + '\'' +
                ", password:'" + password + '\'' +
                ", sex:" + sex +
                ", email:'" + email + '\'' +
                ", phone:'" + phone + '\'' +
                '}';
    }
}
