package com.wllfengshu.jetl.dao;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 报表聚合
 *
 * @author wllfengshu
 */
@Repository
public interface EtlDao {

    /**
     * 获取不聚合的kettle脚本名集合
     *
     * @return
     */
    List<String> queryEtlNoAggregation();
}
