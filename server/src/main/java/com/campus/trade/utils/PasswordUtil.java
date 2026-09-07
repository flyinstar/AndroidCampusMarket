package com.campus.trade.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * 密码加盐加密工具：SHA-256(密码 + 盐) 十六进制小写，长度 64。
 * 盐为 10 位随机字母数字（与 user.salt CHAR(10) 对应）。
 */
public final class PasswordUtil {

    private static final char[] CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final Random RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    /** 生成 10 位随机盐 */
    public static String generateSalt() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(CHARS[RANDOM.nextInt(CHARS.length)]);
        }
        return sb.toString();
    }

    /** 对明文密码加盐后做 SHA-256 并返回 64 位十六进制串 */
    public static String encryptPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((password + salt).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 不可用", e);
        }
    }
}
