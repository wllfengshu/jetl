package com.wllfengshu.jetl.service.impl;

import com.wllfengshu.jetl.configs.EnvConfig;
import com.wllfengshu.jetl.dao.EtlDao;
import com.wllfengshu.jetl.exception.CustomException;
import com.wllfengshu.jetl.job.*;
import com.wllfengshu.jetl.model.ScriptVO;
import com.wllfengshu.jetl.model.enumerate.JobTimeInterval;
import com.wllfengshu.jetl.service.EtlService;
import com.wllfengshu.jetl.utils.KettleUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author wllfengshu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EtlServiceImpl implements EtlService {

    @NonNull
    private EtlDao etlDao;
    @NonNull
    private JobForSecond30 jobForSecond30;
    @NonNull
    private JobForMinute15 jobForMinute;
    @NonNull
    private JobForMinute15 jobForMinute15;
    @NonNull
    private JobForHour jobForHour;
    @NonNull
    private JobForDay jobForDay;
    @NonNull
    private JobForMonth jobForMonth;

    @Override
    public Map<String, Object> etlDataForSecond30(String beginTime,String endTime) throws CustomException {
        Map<String, Object> result = new HashMap<>(2);
        jobForSecond30.execute(beginTime,endTime);
        result.put("operation", "success");
        log.info("30秒-聚合结束：result={}",result);
        return result;
    }

    @Override
    public Map<String, Object> etlDataForMinute(String beginTime,String endTime) throws CustomException {
        Map<String, Object> result = new HashMap<>(2);
        jobForMinute.execute(beginTime,endTime);
        result.put("operation", "success");
        log.info("1分钟-聚合结束：result={}",result);
        return result;
    }

    @Override
    public Map<String, Object> etlDataForMinute15(String beginTime,String endTime) throws CustomException {
        Map<String, Object> result = new HashMap<>(2);
        jobForMinute15.execute(beginTime,endTime);
        result.put("operation", "success");
        log.info("15分钟-聚合结束：result={}",result);
        return result;
    }

    @Override
    public Map<String, Object> etlDataForHour(String beginTime,String endTime) throws CustomException {
        Map<String, Object> result = new HashMap<>(2);
        jobForHour.execute(beginTime,endTime);
        result.put("operation", "success");
        log.info("小时-聚合结束：result={}",result);
        return result;
    }

    @Override
    public Map<String, Object> etlDataForDay(String beginTime,String endTime) throws CustomException {
        Map<String, Object> result = new HashMap<>(2);
        jobForDay.execute(beginTime,endTime);
        result.put("operation", "success");
        log.info("日-聚合结束：result={}",result);
        return result;
    }

    @Override
    public Map<String, Object> etlDataForMonth(String beginTime,String endTime) throws CustomException {
        Map<String, Object> result = new HashMap<>(2);
        jobForMonth.execute(beginTime,endTime);
        result.put("operation", "success");
        log.info("月-聚合结束：result={}",result);
        return result;
    }

    @Override
    public List<String> etlNoAggregation() throws CustomException {
        return etlDao.queryEtlNoAggregation();
    }

    @Override
    public Map<String, Set<String>> etlAggregation() throws CustomException {
        Map<String,Set<String>> result = new HashMap<>();
        result.put("SECOND30", EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_SECOND30.keySet());
        result.put("MINUTE",EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_MINUTE.keySet());
        result.put("MINUTE15",EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_MINUTE15.keySet());
        result.put("HOUR",EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_HOUR.keySet());
        result.put("DAY",EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_DAY.keySet());
        result.put("MONTH",EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_MONTH.keySet());
        return result;
    }

    @Override
    public Map<String, Object> runKettle(String beginTime, String endTime, String kettleName, JobTimeInterval jobTimeInterval) throws CustomException {
        Map<String, Object> result = new HashMap<>(2);

        Map<String,String> etlEnv = new HashMap<>(2);
        etlEnv.put("beginTime",beginTime);
        etlEnv.put("endTime",endTime);
        log.info("kettle脚本开始手动执行，脚本名：{}，系统变量：{}",kettleName,etlEnv);

        ScriptVO scriptVo = new ScriptVO();
        scriptVo.setKettleName(kettleName);
        scriptVo.setDependScriptVos(new ArrayList<>());
        scriptVo.setJobTimeInterval(jobTimeInterval);
        scriptVo.setEtlEnv(etlEnv);
        scriptVo.setPriority(0);

        KettleUtil.etlData(scriptVo);

        result.put("operation", "success");
        log.info("{}脚本手动执行完毕结束：result={}",kettleName,result);
        return result;
    }
}
