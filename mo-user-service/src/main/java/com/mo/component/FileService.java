package com.mo.component;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by mo on 2021/4/21
 */
public interface FileService {

    String uploadUserImg(MultipartFile file);
}
