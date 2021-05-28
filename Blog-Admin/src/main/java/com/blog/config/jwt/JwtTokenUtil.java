package com.blog.config.jwt;

import com.blog.constants.BaseSysConf;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * @author yujunhong
 * @date 2021/5/28 17:37
 */
@Component
@Slf4j
public class JwtTokenUtil {

    /**
     * 解析jwt
     *
     * @param token          token值
     * @param base64Security base64 解密
     * @return Claims
     * @author yujunhong
     * @date 2021/5/28 17:38
     */
    public Claims parseJwt(String token, String base64Security) {
        try {
            return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(base64Security)).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 构建jwt
     *
     * @param userName       账户名
     * @param adminUid       账户id
     * @param roleName       账户拥有角色名
     * @param audience       代表这个Jwt的接受对象
     * @param issuer         代表这个Jwt的签发主题
     * @param ttlMillis      jwt有效时间
     * @param base64Security 加密方式
     * @return token
     * @author yujunhong
     * @date 2021/5/28 17:42
     */
    public String createJwt(String userName, String adminUid, String roleName,
                            String audience, String issuer, long ttlMillis, String base64Security) {
        // HS256是一种对称算法, 双方之间仅共享一个 密钥
        // 由于使用相同的密钥生成签名和验证签名, 因此必须注意确保密钥不被泄密
        // 也可以改成RS256: 非对称加密算法，使用私钥进行加密，使用公钥来验证Token的有效性
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 获取当前时间戳
        long currentTimeMillis = System.currentTimeMillis();
        // 获取时间
        Date date = new Date(currentTimeMillis);
        // 生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        // 添加构成jwt的参数
        JwtBuilder jwtBuilder = Jwts.builder().setHeaderParam("typ", "JWT")
                .claim(BaseSysConf.ADMIN_UID, adminUid)
                .claim(BaseSysConf.ROLE, roleName)
                .claim(BaseSysConf.CREATE_TIME, date)
                .setSubject(userName)
                .setIssuer(issuer)
                .setAudience(audience)
                .signWith(signatureAlgorithm, signingKey);
        // 添加过期时间
        if (ttlMillis >= 0) {
            long expireMillis = currentTimeMillis + ttlMillis;
            Date expire = new Date(expireMillis);
            jwtBuilder.setExpiration(expire).setNotBefore(date);
        }
        // 生成jwt
        return jwtBuilder.compact();
    }

    /**
     * 判断token是否过期
     *
     * @param token          token值
     * @param base64Security base64 解密
     * @author yujunhong
     * @date 2021/5/28 17:57
     */
    public boolean isExpired(String token, String base64Security) {
        if (parseJwt(token, base64Security) != null) {
            return true;
        } else {
            return parseJwt(token, base64Security).getExpiration().before(new Date());
        }
    }
}
