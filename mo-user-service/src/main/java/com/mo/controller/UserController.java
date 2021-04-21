package com.mo.controller;

import com.mo.component.FileService;
import com.mo.enums.BizCodeEnum;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by mo on 2021/4/21
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("/api/user/v1")
public class UserController {

    @Autowired
    private FileService fileService;

    @ApiOperation("用户头像上传")
    @PostMapping(value = "/upload")
    public JsonData uploadUserImg(@ApiParam(value = "文件上传", required = true)
                                  @RequestPart("file") MultipartFile file) {

        String result = fileService.uploadUserImg(file);

        return result!=null? JsonData.buildSuccess(result):JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAILED);
    }
}
