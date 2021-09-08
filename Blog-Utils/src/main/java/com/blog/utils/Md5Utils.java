package com.blog.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 *
 * @author yujunhong
 * @date 2021/9/7 16:00
 */
@Slf4j
public class Md5Utils {

    /**
     * MD5加码 生成32位md5码(不可逆的)
     *
     * @param inStr 加密字段
     * @return 加密后字段
     * @author yujunhong
     * @date 2021/9/7 16:01
     */
    public static String stringToMd5(String inStr) {
        MessageDigest md5;
        String string = "";
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5加密实现的错误日志-->>" + e.getMessage(), e);
            return string;
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        string = hexValue.toString();
        log.debug("MD5加密的32位密钥的调试日志-->>" + string);
        return string;
    }

    /**
     * 加密解密算法 执行一次加密，两次解密
     *
     * @param inStr 加密后字段
     * @return 解密后字段
     * @author yujunhong
     * @date 2021/9/7 16:02
     */
    public static String convertMd5(String inStr){
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String string = new String(a);
        log.debug("MD5加密的二次加密的字符串的调试日志-->>" + string);
        return string;
    }

}
