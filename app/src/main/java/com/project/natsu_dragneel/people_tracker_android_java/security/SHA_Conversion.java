package com.project.natsu_dragneel.people_tracker_android_java.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA_Conversion {
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.reset();
        md.update(password.getBytes());
        byte[] b = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b1 : b) {
            sb.append(Integer.toHexString(b1 & 0xff));
        }
        return sb.toString();
    }
}