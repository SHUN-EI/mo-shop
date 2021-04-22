package com.mo.interceptor;

import com.mo.enums.BizCodeEnum;
import com.mo.model.UserDTO;
import com.mo.utils.CommonUtil;
import com.mo.utils.JWTUtil;
import com.mo.utils.JsonData;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by mo on 2021/4/22
 * 登录拦截器
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private static ThreadLocal<UserDTO> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("token");
        if (null == token) {
            token = request.getParameter("token");
        }

        if (StringUtils.isNoneBlank(token)) {
            Claims claims = JWTUtil.checkJWT(token);

            if (null == claims) {
                //用户未登录
                CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
                return false;
            }

            Long userId = Long.valueOf(claims.get("id").toString());
            String userName = (String) claims.get("user_name");
            String mail = (String) claims.get("mail");
            String headImg = (String) claims.get("head_img");

            UserDTO userDTO = new UserDTO(userId, userName, headImg, mail);

            //通过 attribute传递用户信息
            //request.setAttribute("LoginUserDTO", userDTO);

            //通过threadLocal 传递用户登录信息
            threadLocal.set(userDTO);

            return true;

        }

        CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
