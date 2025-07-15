package com.tangdeng.hssystem.utils;

import org.springframework.util.DigestUtils;

public class MD5Utils {
    public static String createMD5(String s) {
        return DigestUtils.md5DigestAsHex(s.getBytes());
    }

    public static boolean checkMD5(String source, String target) {
        String md5 = createMD5(source);
        if(md5.equals(target) ) return true;
        else return false;
    }
}
