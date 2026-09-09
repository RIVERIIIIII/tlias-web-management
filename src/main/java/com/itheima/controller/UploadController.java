package com.itheima.controller;

import com.itheima.pojo.Result;
import com.itheima.utils.AliyunOSSOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

//文件上传控制器
@Slf4j
@RestController
public class UploadController {
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    //本地磁盘存储方案
    /*@PostMapping("/upload")
    public Result upload(String name, Integer age, MultipartFile file) throws IOException {

        log.info("上传文件：{}，{}，{}", name, age, file);

        //获取原始文件名称
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        //生成新的文件名称
        String newFileName = UUID.randomUUID().toString() + extension;
        //将文件保存到本地
        file.transferTo(new File("D:/WORK/" + newFileName));

        return Result.success();
    }*/

    //阿里云OSS存储方案
    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws Exception {
        log.info("文件上传：{}", file);

        String url = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename());
        log.info("文件上传完成，返回访问地址：{}", url);

        return Result.success(url);
    }
}
