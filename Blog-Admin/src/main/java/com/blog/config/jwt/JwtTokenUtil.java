package com.blog.config.jwt;

import com.blog.constants.BaseSysConf;
import com.blog.entity.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
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
     * @param base64Security base64 规则
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

    /**
     * 获取token过期时间
     *
     * @param token          token值
     * @param base64Security base64 规则
     * @return 过期时间
     * @author yujunhong
     * @date 2021/5/31 10:32
     */
    public Date getExpire(String token, String base64Security) {
        return parseJwt(token, base64Security).getExpiration();
    }

    /**
     * 更新token
     *
     * @param token          旧有的token值
     * @param base64Security base64 规则
     * @param ttlMillis      jwt有效时间
     * @return 新的token值
     * @author yujunhong
     * @date 2021/5/31 10:49
     */
    public String refreshToken(String token, String base64Security, long ttlMillis) {
        String refreshedToken;
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            // 获取当前日期
            long currentTimeMillis = System.currentTimeMillis();
            Date nowDate = new Date(currentTimeMillis);
            // 生成签名密钥
            byte[] bytes = DatatypeConverter.parseBase64Binary(base64Security);
            // 获取对应的key
            Key secretKeySpec = new SecretKeySpec(bytes, signatureAlgorithm.getJcaName());
            Claims claims = parseJwt(token, base64Security);
            claims.put("createDate", new Date());
            // 获取JwtBuilder实体对象
            JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                    .setClaims(claims)
                    .setSubject(getUsername(token, base64Security))
                    .setIssuer(getIssuer(token, base64Security))
                    .setAudience(getAudience(token, base64Security))
                    .signWith(signatureAlgorithm, secretKeySpec);
            // 添加过期时间
            if (ttlMillis > 0) {
                // 过期时间戳
                long expireMillis = currentTimeMillis + ttlMillis;
                Date expireDate = new Date(expireMillis);
                builder.setExpiration(expireDate).setNotBefore(nowDate);
            }
            refreshedToken = builder.compact();
        } catch (Exception e) {
            refreshedToken = null;
        }
        log.info("刷新后的token: {}", refreshedToken);
        return refreshedToken;
    }

    /**
     * 从token中获取用户名
     *
     * @param token          token值
     * @param base64Security base64 规则
     * @return 用户名
     * @author yujunhong
     * @date 2021/5/31 11:08
     */
    public String getUsername(String token, String base64Security) {
        return parseJwt(token, base64Security).getSubject();
    }

    /**
     * 从token中获取用户UID
     *
     * @param token          token值
     * @param base64Security base64 规则
     * @return 用户uuid
     */
    public String getUserUid(String token, String base64Security) {
        return parseJwt(token, base64Security).get(BaseSysConf.ADMIN_UID, String.class);
    }

    /**
     * 从token中获取issuer
     *
     * @param token          token值
     * @param base64Security base64 规则
     * @return issuer
     * @author yujunhong
     * @date 2021/5/31 11:09
     */
    public String getIssuer(String token, String base64Security) {
        return parseJwt(token, base64Security).getIssuer();
    }

    /**
     * 从token中获取audience
     *
     * @param token          token值
     * @param base64Security base64 规则
     * @return audience
     * @author yujunhong
     * @date 2021/5/31 11:10
     */
    public String getAudience(String token, String base64Security) {
        return parseJwt(token, base64Security).getAudience();
    }

    /**
     * @param token          token值
     * @param userDetails    用户明细
     * @param base64Security base64 规则
     * @return token是否合法
     * @author yujunhong
     * @date 2021/5/31 11:49
     */
    public Boolean validateToken(String token, UserDetails userDetails, String base64Security) {
        SecurityUser securityUser = (SecurityUser) userDetails;
        final String username = getUsername(token, base64Security);
        final boolean expiration = isExpired(token, base64Security);
        return (
                username.equals(securityUser.getUsername())
                        && !expiration);
    }
}
