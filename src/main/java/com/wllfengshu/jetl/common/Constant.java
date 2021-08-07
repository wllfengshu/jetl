package com.wllfengshu.jetl.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 公共常量集合
 *
 * @author wangll
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constant {

    /**
     * docker中存放的脚本基础路径
     */
    public static final String WORKSPACE_BASE = "/var/app";

    /**
     * ktr和kjb文件存放路径
     */
    public static final String WORKSPACE_PATH = "/workspace";

    /**
     * ktr和kjb文件存放路径-30秒
     */
    public static final String WORKSPACE_PATH_SECOND30 = WORKSPACE_PATH + "/second30";

    /**
     * ktr和kjb文件存放路径-1分钟
     */
    public static final String WORKSPACE_PATH_MINUTE = WORKSPACE_PATH + "/minute";

    /**
     * ktr和kjb文件存放路径-15分钟
     */
    public static final String WORKSPACE_PATH_MINUTE15 = WORKSPACE_PATH + "/minute15";

    /**
     * ktr和kjb文件存放路径-小时
     */
    public static final String WORKSPACE_PATH_HOUR = WORKSPACE_PATH + "/hour";

    /**
     * ktr和kjb文件存放路径-天
     */
    public static final String WORKSPACE_PATH_DAY = WORKSPACE_PATH + "/day";

    /**
     * ktr和kjb文件存放路径-月
     */
    public static final String WORKSPACE_PATH_MONTH = WORKSPACE_PATH + "/month";

    /**
     * 时间格式常量
     */
    public static final String FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
}
