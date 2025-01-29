package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Resource
    private AmazonS3 s3Client;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        String flag = System.currentTimeMillis() + "";
        String fileName = "files/"+ file.getOriginalFilename();

        try {
            // Upload to S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);

            // Get S3 URL
            String fileUrl = s3Client.getUrl(bucketName, fileName).toString();
            return Result.success(file.getOriginalFilename());

        } catch (Exception e) {
            System.err.println(fileName + "--文件上传失败: " + e.getMessage());
            return Result.error(ResultCodeEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 富文本文件上传
     */
    @PostMapping("/editor/upload")
    public Dict editorUpload(MultipartFile file) {
        String flag;
        synchronized (FileController.class) {
            flag = System.currentTimeMillis() + "";
            ThreadUtil.sleep(1L);
        }
        String fileName = flag + "-" + file.getOriginalFilename();

        try {
            // Upload to S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);

            // Get S3 URL
            String fileUrl = s3Client.getUrl(bucketName, fileName).toString();
            System.out.println(fileName + "--上传成功");

            // Return format required by editor
            return Dict.create()
                    .set("errno", 0)
                    .set("data", CollUtil.newArrayList(
                            Dict.create().set("url", fileUrl)));

        } catch (Exception e) {
            System.err.println(fileName + "--文件上传失败: " + e.getMessage());
            return Dict.create().set("errno", 1).set("message", "Upload failed");
        }
    }

    /**
     * 获取文件
     *
     * @param flag
     * @param response
     */
    @GetMapping("/{flag}")
    public void getFile(@PathVariable String flag, HttpServletResponse response) {
        try {
            // Get from S3
            flag = "files/" +flag;
            S3Object s3Object = s3Client.getObject(bucketName, flag);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            response.setContentType(s3Object.getObjectMetadata().getContentType());
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(flag, "UTF-8"));

            // Stream file to response
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();

        } catch (Exception e) {
            System.err.println("文件下载失败: " + e.getMessage());
            System.err.println("文件名: " + flag);
        }
    }

    @DeleteMapping("/{flag}")
    public void delFile(@PathVariable String flag) {
        try {
            s3Client.deleteObject(bucketName, flag);
            System.out.println("删除文件" + flag + "成功");
        } catch (Exception e) {
            System.err.println("删除文件失败: " + e.getMessage());
        }
    }

}
