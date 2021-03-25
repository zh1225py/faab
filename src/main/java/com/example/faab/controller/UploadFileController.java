package com.example.faab.controller;


import com.example.faab.constants.TemplateConstant;
import com.example.faab.domain.ApiResult;
import com.example.faab.domain.ApiResultUtil;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.*;
import com.example.faab.exception.BaseException;
import com.example.faab.service.*;
import com.example.faab.vo.DownLoadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.faab.domain.StatusCodeEnum.DOWNLOAD_FAILED;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-11
 */
@RestController
public class UploadFileController {

    @Autowired
    DecryptService decryptService;

    @Autowired
    UserAttrService userAttrService;

    @Autowired
    UserKeyService userKeyService;

    @Autowired
    UserService userService;

    @Autowired
    UploadFileService uploadFileService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @PostMapping(value = "/user/uploadFile")
    @ResponseBody
    public ApiResult upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        //String ACCESSPOLICY = request.getParameter("policy");
//        String keyword = request.getParameter("keyword");
        //String ACCESSPOLICY = "((RafflesHospital OR CentralHospital) AND (SurgeryDepartment OR (Doctor AND Patient)))";
        String ACCESSPOLICY = TemplateConstant.preAccessTemplate;
        System.out.println(ACCESSPOLICY);
        if (file.isEmpty()) {
            ApiResultUtil.errorAuthorized("无文件");
        }

        String format = sdf.format(new Date());
        String realPath = request.getServletContext().getRealPath("/") + format;
        System.out.println(realPath);
        File folder = new File(realPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        //String newName = file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf("."));
        String newName = file.getOriginalFilename();
        System.out.println(newName);
        File upload_file = new File(folder, newName);
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + format + "/" + newName;
        System.out.println(url);
        try {
            file.transferTo(upload_file);
            HttpSession session = request.getSession();
            PP pp = (PP) session.getAttribute("pp");
            SK sk = (SK) session.getAttribute("sk");
            UserVO loginUser = (UserVO) session.getAttribute("loginUser");
            List userAttr = userAttrService.getUserAttr(loginUser.getUsername());
            String[] attrs = (String[]) userAttr.toArray(new String[userAttr.size()]);

            //generate access policy
            for (int i = 0; i < attrs.length; i++) {
                ACCESSPOLICY = ACCESSPOLICY.concat(attrs[i]+" ").concat(" "+TemplateConstant.accessTemplateConcat+ " ");
            }
            ACCESSPOLICY = ACCESSPOLICY.substring(0,ACCESSPOLICY.lastIndexOf(TemplateConstant.accessTemplateConcat))
                    .concat(TemplateConstant.endAccessTemplate);

            decryptService.TKGen(sk, attrs);//3.用户转换密钥生成
            if ((pp != null) && (sk != null) && (upload_file != null)) {
                UploadFile uploadFile = uploadFileService.Encryption(pp, newName, upload_file, ACCESSPOLICY, attrs);//4.多媒体加密，假设数据内容为fzu
                Theta_CT theta_ct = uploadFileService.Sign(pp, sk);//5.多媒体密文签名
                uploadFileService.Verify(pp,theta_ct,uploadFile);//6.签名校验后上传
                return ApiResultUtil.success();
            }
            return ApiResultUtil.errorParam("上传失败");
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResultUtil.errorParam("上传失败");
        }
    }

    @RequestMapping(value = "/user/files",method = RequestMethod.GET)
    public ApiResult getAllfile(HttpServletRequest request, HttpServletResponse response){
        List<String> allFilename = uploadFileService.getAllFileName();
        return ApiResultUtil.successReturn(allFilename);
    }

    @RequestMapping(value = "/user/getFile",method = RequestMethod.POST)
    public ApiResult getFile(@RequestBody DownLoadFile uploadFile, HttpServletRequest request,
                             HttpServletResponse response) throws IOException, BaseException {
        String filename = uploadFile.getFileName();
        System.out.println(filename);
        String format = sdf.format(new Date());
        String realPath = request.getServletContext().getRealPath("/") + format;
        System.out.println(realPath);
        File folder = new File(realPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        HttpSession session = request.getSession();
        List<String> fileUrl = new ArrayList<>();
        SK sk = (SK) session.getAttribute("sk");
        UserVO loginUser = (UserVO) session.getAttribute("loginUser");
        List<String> userAttr = loginUser.getAttr();
        String[] attrs = userAttr.toArray(new String[userAttr.size()]);
//        UserVO oneUser = userService.getOneUser("001");
//        List<String> userAttr = oneUser.getAttr();
//        String[] attrs = userAttr.toArray(new String[userAttr.size()]);
        for (int i = 0; i < attrs.length; i++) {
            System.out.println(attrs[i]);
        }

        UploadFile getOneFile = uploadFileService.getFile(filename);
        if(getOneFile != null){
            decryptService.TKGen(sk, attrs);//3.用户转换密钥生成
            Trans trans = decryptService.Transform(getOneFile, attrs);//7. 密文转换
            byte[] dec = decryptService.Decrypt(sk, trans);//8. 解密
            if(dec != null){
                String newName = filename;
                File sk_file = new File(folder,newName);
                String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + format + "/" + newName;
                response.reset();
                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
                response.setContentType("application/octet-stream");
                ServletOutputStream os = response.getOutputStream();

                //ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(sk_file));
                os.write(dec);
                os.flush();
                os.close();
                return ApiResultUtil.successReturn(url);
//            return ApiResultUtil.success();
            }else{
                throw new BaseException(DOWNLOAD_FAILED.getCode(),DOWNLOAD_FAILED.getMessage());
            }
        }else {
            return ApiResultUtil.errorParam("获取文件失败");
        }
    }

    @RequestMapping(value = "/deleteFile",method = RequestMethod.POST)
    public ApiResult deleteAttr(@RequestBody DownLoadFile uploadFile,HttpServletRequest request){
        String filename = uploadFile.getFileName();
        System.out.println(filename);
        uploadFileService.deleteFile(filename);
        return ApiResultUtil.success();
    }

}
