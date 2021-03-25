package com.example.faab.service.impl;

import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.MSK;
import com.example.faab.entity.PP;
import com.example.faab.entity.SysPara;
import com.example.faab.entity.User;
import com.example.faab.service.SysParaService;
import com.example.faab.service.UserAttrService;
import com.example.faab.service.UserKeyService;
import com.example.faab.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class UserServiceImplTest {


    @Autowired
    UserAttrService userAttrService;

    @Autowired
    UserService userService;

    @Autowired
    SysParaService sysParaService;

    @Autowired
    UserKeyService userKeyService;

    @Test
    public void getAttr(){
//        List<UserVO> allUsers = userService.getAllUsers();
        UserVO userVO = userService.loginUser("001","123456");
        System.out.println(userVO);
    }

    @Test
    public void addUser(){
        UserVO user = new UserVO();
        user.setUsername("test");
        List<String> attrs = new ArrayList<>();
        attrs.add("hospital");
        user.setAttr(attrs);

        DoublePairing doublePairing = new DoublePairing();
        doublePairing.getStart();
        Serial serial = new Serial();
        SysPara sysPara = sysParaService.getSysPara();
        MSK msk = (MSK)serial.deserial(sysPara.getMsk());
        PP pp = (PP)serial.deserial(sysPara.getPp());

        String username = user.getUsername();
        List<String> user_attr = user.getAttr();
        User newUser = new User();
        if(username !=null && user_attr != null){
            newUser.setUsername(username);
            userService.addUser(newUser);
            for(String attr : user_attr){
                userAttrService.addUserAttr(username,attr);
            }
        }
        String[] userAttr = user_attr.toArray(new String[user_attr.size()]);
        userKeyService.SKGen(pp, msk, username, userAttr,true);
    }
}