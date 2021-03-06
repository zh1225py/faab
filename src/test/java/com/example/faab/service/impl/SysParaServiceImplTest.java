package com.example.faab.service.impl;

import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.*;
import com.example.faab.exception.BaseException;
import com.example.faab.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;


@SpringBootTest
class SysParaServiceImplTest {

    @Autowired
    SysParaService sysParaService;

    @Autowired
    UserService userService;

    @Autowired
    UserKeyService userKeyService;

    @Autowired
    DecryptService decryptService;

    @Autowired
    UploadFileService uploadFileService;

    @Autowired
    ListTranceService listTranceService;

  /*  @Test
    public void setup(){
        DoublePairing doublePairing = new DoublePairing();
        doublePairing.getStart();
        sysParaService.Setup();
        //SysPara sysPara = sysParaService.getSysPara();
        //System.out.println(sysPara);
    }*/

    @Test
    public void setup(){
        DoublePairing doublePairing = new DoublePairing();
        doublePairing.getStart();
        Serial serial = new Serial();
        //sysParaService.Setup();
        SysPara sysPara = sysParaService.getSysPara();
        MSK msk = (MSK)serial.deserial(sysPara.getMsk());
        PP pp = (PP)serial.deserial(sysPara.getPp());
        System.out.println(msk);
        System.out.println(pp);
    }

    @Test
    public void keyGen() {
        DoublePairing.getStart();
        SysPara sysPara = sysParaService.getSysPara();
        UserVO user = userService.getOneUser("001");
        String username = user.getUsername();
        List<String> userAttr = user.getAttr();
        String[] attrs = userAttr.toArray(new String[userAttr.size()]);
        Serial serial = new Serial();
        MSK msk = (MSK)serial.deserial(sysPara.getMsk());
        PP pp = (PP)serial.deserial(sysPara.getPp());
        userKeyService.SKGen(pp,msk,username,attrs,true);
    }

    @Test
    public void uploadTest(){
        try{
            DoublePairing.getStart();
            String username = "001";
//            String[] attrs = {"Doctor","Patient","RafflesHospital"};
            UserVO oneUser = userService.getOneUser(username);
            List<String> userAttr = oneUser.getAttr();
            String[] attrs = userAttr.toArray(new String[userAttr.size()]);
            SysPara sysPara = sysParaService.getSysPara();//1.???????????????
            Serial serial = new Serial();
            MSK msk = (MSK)serial.deserial(sysPara.getMsk());
            PP pp = (PP)serial.deserial(sysPara.getPp());
            UserKey userKey = userKeyService.getUserKey(username);//2.????????????
            SK sk = (SK)serial.deserial(userKey.getSk());
//            decryptService.TKGen(sk, attrs);//3.????????????????????????
            String ACCESSPOLICY = "((RafflesHospital OR CentralHospital) AND (SurgeryDepartment OR (Doctor AND Patient)))";
            File file = new File("C:\\Users\\bob\\Desktop\\file\\999.txt");
            UploadFile uploadFile = uploadFileService.Encryption(pp, "999", file, ACCESSPOLICY, attrs);//4.???????????????
            Theta_CT theta_ct = uploadFileService.Sign(pp, sk);//5.?????????????????????
            uploadFileService.Verify(pp,theta_ct,uploadFile);//6.?????????????????????
//            System.out.println(resultofverify);
//            Trans trans = decryptService.Transform(uploadFile, attrs);//7. ????????????
//            decryptService.Decrypt(sk, trans);//8. ??????
////            sysParaService.SecProvenance();//9.????????????
        }catch(Exception e){
            System.out.println("?????????????????????????????????????????????");
            System.out.println("1.???????????????????????????????????????????????????????????????????????????????????????????????????A OR B OR C,??????????????????{A,B,D},?????????????????????{A,B}");
            System.out.println("2.?????????????????????????????????????????????(A OR B) AND (C AND (D OR F))");
            System.out.println("3.?????????????????????????????????");
        }
    }
    @Test
    public void downloadTest() throws BaseException {
        UploadFile uploadFile = uploadFileService.getFile("test");
        if(uploadFile != null){
            DoublePairing.getStart();
            String username = "admin2";
//            String[] attrs = {"Doctor","Patient","RafflesHospital"};
            UserVO oneUser = userService.getOneUser(username);
            List<String> userAttr = oneUser.getAttr();
            String[] attrs = userAttr.toArray(new String[userAttr.size()]);
            Serial serial = new Serial();
            UserKey userKey = userKeyService.getUserKey(username);//2.????????????
            SK sk = (SK)serial.deserial(userKey.getSk());
            decryptService.TKGen(sk, attrs);//3.????????????????????????
            Trans trans = decryptService.Transform(uploadFile, attrs);//7. ????????????
            byte[] decrypt = decryptService.Decrypt(sk, trans);//8. ??????
            System.out.println(new String(decrypt));
        }
//        try{
//            DoublePairing.getStart();
//            String username = "001";
//            String[] attrs = {"Doctor","Patient","RafflesHospital"};
//            SysPara sysPara = sysParaService.getSysPara();//1.???????????????
//            Serial serial = new Serial();
//            MSK msk = (MSK)serial.deserial(sysPara.getMsk());
//            PP pp = (PP)serial.deserial(sysPara.getPp());
//            UserKey userKey = userKeyService.getUserKey(username);//2.????????????
//            SK sk = (SK)serial.deserial(userKey.getSk());
//            decryptService.TKGen(sk, attrs);//3.????????????????????????
//
//            Theta_CT theta_ct = uploadFileService.Sign(pp, sk);//5.?????????????????????
//            boolean resultofverify = uploadFileService.Verify(pp,theta_ct,uploadFile);//6.????????????
//            System.out.println(resultofverify);
//            Trans trans = decryptService.Transform(uploadFile, attrs);//7. ????????????
//            decryptService.Decrypt(sk, trans);//8. ??????
////            sysParaService.SecProvenance();//9.????????????
//        }catch(Exception e){
//            System.out.println("?????????????????????????????????????????????");
//            System.out.println("1.???????????????????????????????????????????????????????????????????????????????????????????????????A OR B OR C,??????????????????{A,B,D},?????????????????????{A,B}");
//            System.out.println("2.?????????????????????????????????????????????(A OR B) AND (C AND (D OR F))");
//            System.out.println("3.?????????????????????????????????");
//        }
    }

    @Test
    public void tranceTest() {
        UploadFile uploadFile = uploadFileService.getFile("test");
        if (uploadFile != null) {
            DoublePairing.getStart();
            SysPara sysPara = sysParaService.getSysPara();//1.???????????????
            Serial serial = new Serial();
            MSK msk = (MSK) serial.deserial(sysPara.getMsk());
            listTranceService.SecProvenance(uploadFile, msk);
        }
    }

}