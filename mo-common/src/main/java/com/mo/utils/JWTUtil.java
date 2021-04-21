package com.mo.utils;

import com.mo.enums.BizCodeEnum;
import com.mo.exception.BizException;
import com.mo.model.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Created by mo on 2021/4/21
 */
@Slf4j
public class JWTUtil {

    /**
     * token过期时间，默认为7天
     */
    private static final long EXPIRED = 1000 * 60 * 60 * 24 * 7;

    /**
     * 加密的密钥
     */
    private static final String SECRET = "moshop666";

    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "moshop";

    /**
     * subject,颁布者
     */
    private static final String SUBJECT = "waynemo";


    /**
     * 根据用户信息，生成token令牌
     *
     * @param user
     * @return
     */
    public static String generateJsonWebToken(UserDTO user) {

        if (null == user) {
            throw new BizException(BizCodeEnum.ACCOUNT_UNREGISTER);
        }

        String token = Jwts.builder().setSubject(SUBJECT)
                //playload
                .claim("id", user.getId())
                .claim("user_name", user.getUserName())
                .claim("mail", user.getMail())
                .claim("head_img", user.getHeadImg())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

        token = TOKEN_PREFIX + token;

        return token;
    }

    /**
     * 校验token的方法
     * @param token
     * @return
     */
    public static Claims checkJWT(String token) {

        try {
            final Claims claims = Jwts.parser().setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();

            return claims;

        } catch (Exception e) {
            log.info("jwt token 解密失败");
            return null;
        }
    }
}
