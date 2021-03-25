package com.example.faab.controller;


import com.example.faab.domain.ApiResult;
import com.example.faab.domain.ApiResultUtil;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.MSK;
import com.example.faab.entity.UploadFile;
import com.example.faab.service.DecryptService;
import com.example.faab.service.ListTranceService;
import com.example.faab.service.UploadFileService;
import com.example.faab.service.UserService;
import com.example.faab.vo.DownLoadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 *
 */
@RestController
public class ListTranceController {
    @Autowired
    UploadFileService uploadFileService;

    @Autowired
    ListTranceService listTranceService;

    @Autowired
    DecryptService decryptService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/trance",method = RequestMethod.POST)
    public ApiResult trance(@RequestBody DownLoadFile uploadFile, HttpServletRequest request) throws IOException {
        String filename = uploadFile.getFileName();
        System.out.println(filename);
        UploadFile getFile = uploadFileService.getFile(filename);
        if (getFile != null) {
            HttpSession session = request.getSession();
            MSK msk = (MSK)session.getAttribute("msk");



            String id = listTranceService.SecProvenance(getFile, msk);
            if(id != null){
                UserVO user = userService.getOneUser(id);

                //check the user policy
                decryptService.Transform(getFile,(String[])user.getAttr().toArray());

                return ApiResultUtil.successReturn(user);
            }
            return ApiResultUtil.errorAuthorized("追溯用户失败");
        }else{
            return ApiResultUtil.errorAuthorized("没有找到文件");
        }
//        return ApiResultUtil.success();
    }
}
