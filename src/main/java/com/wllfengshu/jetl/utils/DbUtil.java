package com.wllfengshu.jetl.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库工具类
 *
 * @author wangll
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DbUtil {

    /**
     * 处理数据库配置
     *
     * @param dbUrl
     * @param dbUsername
     * @param dbPassword
     * @return
     */
    public static Map<String, String> dealDb(String dbUrl, String dbUsername, String dbPassword) {
        Map<String, String> map = new HashMap<>(5);
        map.putAll(dealDbUrl(dbUrl));
        map.put("dbUsername", dbUsername);
        map.put("dbPassword", dbPassword);
        log.info("数据库配置处理完毕，dbInfo={}", map);
        return map;
    }

    /**
     * 处理数据库url
     *
     * @param dbUrl
     * @return
     */
    public static Map<String, String> dealDbUrl(String dbUrl) {
        Map<String, String> map = new HashMap<>(5);
        //1 截取出ip:port/dbName
        String temp = dbUrl.substring("jdbc:mysql://".length(), dbUrl.contains("?") ? dbUrl.indexOf("?") : dbUrl.length());
        //2 分离ip
        String[] ipPortDb = temp.split(":");
        map.put("dbIp", ipPortDb[0]);
        //3 分离port和dbName
        String[] portDb = ipPortDb[1].split("/");
        map.put("dbPort", portDb[0]);
        map.put("dbName", portDb[1]);
        log.info("数据库url处理完毕，dbInfo={}", map);
        return map;
    }
}
