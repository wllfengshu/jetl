package com.wllfengshu.jetl.model.enumerate;

/**
 * 脚本的状态
 *
 * @author wangll
 */
public enum ScriptStat {

    /**
     * 创建
     */
    Create,

    /**
     * 就绪
     */
    Ready,

    /**
     * 执行中
     */
    Running,

    /**
     * 完成
     */
    Finish
}
