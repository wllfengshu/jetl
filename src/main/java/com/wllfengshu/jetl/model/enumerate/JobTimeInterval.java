package com.wllfengshu.jetl.model.enumerate;

/**
 * job执行的时间间隔的类型
 *
 * @author wangll
 */
public enum JobTimeInterval {

    /**
     * 30秒
     */
    Second30,

    /**
     * 1分钟
     */
    Minute,

    /**
     * 15分钟
     */
    Minute15,

    /**
     * 小时
     */
    Hour,

    /**
     * 天
     */
    Day,

    /**
     * 月
     */
    Month
}
