package com.wllfengshu.jetl.model.entity;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.io.Serializable;

/**
 * 中继信息-实体类
 *
 * @author wangll
 */
@Data
@Measurement(name = "etl_trunk_info")
public class TrunkInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "domain", tag = true)
    private String domain;

    @Column(name = "trunkName", tag = true)
    private String trunkName;

    /**
     * 呼入可用量
     */
    @Column(name = "inboundAbleNum")
    private Integer inboundAbleNum = 0;

    /**
     * 呼出可用量
     */
    @Column(name = "externalAbleNum")
    private Integer externalAbleNum = 0;

    /**
     * 可用量
     */
    @Column(name = "ableNum")
    private Integer ableNum = 0;

    /**
     * 呼入使用量
     */
    @Column(name = "inboundUseNum")
    private Integer inboundUseNum = 0;

    /**
     * 呼出使用量
     */
    @Column(name = "externalUseNum")
    private Integer externalUseNum = 0;

    /**
     * 使用量
     */
    @Column(name = "useNum")
    private Integer useNum = 0;
}
