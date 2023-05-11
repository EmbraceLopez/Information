package com.le.info.utils;

import android.util.Xml;

import java.security.MessageDigest;


public class SignUtils {

    //MD5加密
    public static String MD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 密码加密
     * @param data
     * @return
     */
    public static String encode(String data) {
        //把字符串转为字节数组
        byte[] b = data.getBytes();
        for(int i=0;i<b.length;i++) {
            b[i] += 1;
        }
        return new String(b);
    }

    /**
     * 密码解密
     * @param data
     * @return
     */
    public static String decode(String data) {
        //把字符串转为字节数组
        byte[] b = data.getBytes();
        for(int i=0;i<b.length;i++) {
            b[i] -= 1;
        }
        return new String(b);
    }

}
