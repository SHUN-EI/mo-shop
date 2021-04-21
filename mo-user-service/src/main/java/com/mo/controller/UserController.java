package com.mo.controller;

import com.mo.component.FileService;
import com.mo.enums.BizCodeEnum;
import com.mo.request.UserRegisterRequest;
import com.mo.service.UserService;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    @Autowired
    private UserService userService;

    @ApiOperation("用户头像上传")
    @PostMapping(value = "/upload")
    public JsonData uploadUserImg(@ApiParam(value = "文件上传", required = true)
                                  @RequestPart("file") MultipartFile file) {

        String result = fileService.uploadUserImg(file);

        return result != null ? JsonData.buildSuccess(result) : JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAILED);
    }


    @ApiOperation("用户注册")
    @PostMapping("/register")
    public JsonData register(@ApiParam("用户注册对象")@RequestBody UserRegisterRequest request) {

        JsonData jsonData = userService.register(request);
        return jsonData.buildSuccess(jsonData);
    }
}
