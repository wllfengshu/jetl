package com.wllfengshu.jetl.service;

import com.wllfengshu.jetl.exception.CustomException;
import com.wllfengshu.jetl.model.enumerate.JobTimeInterval;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wllfengshu
 */
@Service
public interface EtlService {

    /**
     * 30秒-聚合
     * @param beginTime
     * @param endTime
     * @return
     * @throws CustomException
     */
    Map<String, Object> etlDataForSecond30(String beginTime,String endTime) throws CustomException;

    /**
     * 1分钟-聚合
     * @param beginTime
     * @param endTime
     * @return
     * @throws CustomException
     */
    Map<String, Object> etlDataForMinute(String beginTime,String endTime) throws CustomException;

    /**
     * 15分钟-聚合
     * @param beginTime
     * @param endTime
     * @return
     * @throws CustomException
     */
    Map<String, Object> etlDataForMinute15(String beginTime,String endTime) throws CustomException;

    /**
     * 小时-聚合
     * @param beginTime
     * @param endTime
     * @return
     * @throws CustomException
     */
    Map<String, Object> etlDataForHour(String beginTime,String endTime) throws CustomException;

    /**
     * 每天-聚合
     * @param beginTime
     * @param endTime
     * @return
     * @throws CustomException
     */
    Map<String, Object> etlDataForDay(String beginTime,String endTime) throws CustomException;

    /**
     * 每月-聚合
     * @param beginTime
     * @param endTime
     * @return
     * @throws CustomException
     */
    Map<String, Object> etlDataForMonth(String beginTime,String endTime) throws CustomException;

    /**
     * 查看不聚合的kettle脚本名
     * @return
     * @throws CustomException
     */
    List<String> etlNoAggregation() throws CustomException;

    /**
     * 查看所有聚合的kettle脚本名
     * @return
     * @throws CustomException
     */
    Map<String, Set<String>> etlAggregation() throws CustomException;

    /**
     * 聚合指定的报表
     * @param beginTime
     * @param endTime
     * @param kettleName
     * @param jobTimeInterval
     * @return
     * @throws CustomException
     */
    Map<String, Object> runKettle(String beginTime, String endTime, String kettleName, JobTimeInterval jobTimeInterval) throws CustomException;
}
