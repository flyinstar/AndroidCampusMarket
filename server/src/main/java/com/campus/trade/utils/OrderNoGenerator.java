package com.campus.trade.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单号生成器：yyyyMMddHHmmss + 6位随机数 + 用户ID后缀，总长 32 以内。
 */
public final class OrderNoGenerator {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private OrderNoGenerator() {
    }

    public static String generate(Integer userId) {
        String time = LocalDateTime.now().format(FMT);
        int rand = ThreadLocalRandom.current().nextInt(100000, 1000000);
        String suffix = userId == null ? "" : String.valueOf(userId % 10000);
        return "CT" + time + rand + suffix;
    }
}
