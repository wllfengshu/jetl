package com.wllfengshu.jetl.model.entity;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * 不聚合的kettle脚本名集合-实体类
 *
 * @author wangll
 */
@Data
public class EtlNoAggregation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 不聚合的kettle脚本名
     */
    @Id
    private String kettleName;
}
