package com.mo.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by mo on 2021/5/5
 */
@Slf4j
public class HttpUtil {

    public static void writeData(HttpServletResponse response, JsonData jsonData) {
        try (PrintWriter writer = response.getWriter()) {

            response.setContentType("text/html;charset=utf-8");
            writer.write(jsonData.getData().toString());
            response.flushBuffer();
        } catch (IOException e) {
            log.error("写出响应页面异常:{}", e);
        }

    }
}
