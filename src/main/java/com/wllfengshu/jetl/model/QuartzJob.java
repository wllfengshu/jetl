package com.wllfengshu.jetl.model;

import lombok.Data;

import java.io.Serializable;

/**
 * job实体类
 *
 * @author wangll
 */
@Data
public class QuartzJob implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 执行类
     */
    private String jobClassName;

    /**
     * 执行时间
     */
    private String cronExpression;

    /**
     * 触发器名
     */
    private String triggerName;

    /**
     * 触发器状态
     */
    private String triggerState;

}

