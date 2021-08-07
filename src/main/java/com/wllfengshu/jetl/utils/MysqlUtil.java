package com.wllfengshu.jetl.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * mysql工具类
 *
 * @author wangll
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MysqlUtil {

    /**
     * 查询
     *
     * @param url
     * @param username
     * @param password
     * @param sql
     * @param clazz    javaBean
     * @return
     */
    public static <T> List<T> doQuery(String url, String username, String password, String sql, Class<T> clazz) {
        JSONArray resultArr = new JSONArray();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            log.error("加载驱动异常", e);
            return new ArrayList<>();
        }
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            log.info("doQuery sql:{}", sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                JSONObject jsonData = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    jsonData.put(columnName, value);
                }
                resultArr.add(jsonData);
            }
        } catch (Exception e) {
            log.error("获取数据失败", e);
        }
        return JSON.parseArray(resultArr.toJSONString(), clazz);
    }

    /**
     * 查询条数（注意：返回数量的字段为count）
     *
     * @param url
     * @param username
     * @param password
     * @param sql
     * @return
     */
    public static int doQueryCount(String url, String username, String password, String sql) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            log.error("加载驱动异常", e);
            return 0;
        }
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            log.info("doQueryCount sql:{}", sql);
            return rs.getInt("count");
        } catch (Exception e) {
            log.error("获取失败", e);
        }
        return 0;
    }
}
