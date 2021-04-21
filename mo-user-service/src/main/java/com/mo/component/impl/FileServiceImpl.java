package com.mo.component.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.mo.component.FileService;
import com.mo.config.OSSConfig;
import com.mo.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by mo on 2021/4/21
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private OSSConfig ossConfig;


    @Override
    public String uploadUserImg(MultipartFile file) {

        //获取oss相关配置
        String bucketname = ossConfig.getBucketname();
        String endpoint = ossConfig.getEndpoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();
        //创建oss对象
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        //获取原始文件名 xxx.jpg
        String originalFilename = file.getOriginalFilename();

        //JDK8的日期格式，构建文件夹路径
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        //拼装路径,oss上的存储路径 2022/12/1/sdsfssdwfkshhk.jpg
        String folder = dtf.format(now);
        String fileName = CommonUtil.generateUUID();
        //文件的扩展名 .jpg
        String extensionName = originalFilename.substring(originalFilename.lastIndexOf("."));

        //在oss上创建文件夹 user文件夹  user/2022/12/1/sdsfssdwfkshhk.jpg
        String newFileName = "user/" + folder + "/" + fileName + extensionName;

        try {
            PutObjectResult result = ossClient.putObject(bucketname, newFileName, file.getInputStream());

            //返回文件访问路径
            if (null != result) {
                String imgUrl = "https://" + bucketname + "." + endpoint + "/" + newFileName;
                return imgUrl;
            }

        } catch (IOException e) {
            log.error("上传头像失败:{}", e);
        } finally {
            //oss服务关闭，不然会OOM
            ossClient.shutdown();
        }

        return null;
    }
}
