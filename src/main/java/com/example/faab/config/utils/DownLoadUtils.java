package com.example.faab.config.utils;

import com.example.faab.exception.BaseException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static com.example.faab.domain.StatusCodeEnum.DOWNLOAD_FAILED;

/**
 * @version : 1.0
 * @Author : zh
 * @Date ：2021/3/23
 * @Description ：
 **/
public class DownLoadUtils {

    public static HttpServletResponse download(File file, HttpServletResponse response) {
        try {

            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            throw new BaseException(DOWNLOAD_FAILED.getCode(),DOWNLOAD_FAILED.getMessage());
        }
        return response;
    }

    public void downloadLocal(HttpServletResponse response) throws FileNotFoundException {
// 下载本地文件
        String fileName = "Operator.doc".toString(); // 文件的默认保存名
// 读到流中
        InputStream inStream = new FileInputStream("c:/Operator.doc");// 文件的存放路径
// 设置输出的格式
        response.reset();
        response.setContentType("bin");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
// 循环取出流中的数据
        byte[] b = new byte[100];
        int len;
        try {
            while ((len = inStream.read(b)) > 0)
                response.getOutputStream().write(b, 0, len);
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadNet(HttpServletResponse response) throws MalformedURLException {
// 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        URL url = new URL("windine.blogdriver.com/logo.gif");

        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream("c:/abc.gif");

            byte[] buffer = new byte[1204];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
