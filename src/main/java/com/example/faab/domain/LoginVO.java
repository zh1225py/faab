package com.example.faab.domain;

import lombok.Data;

import java.io.Serializable;

@Data
//@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class LoginVO implements Serializable {
    private String username;
    private String password;
}
