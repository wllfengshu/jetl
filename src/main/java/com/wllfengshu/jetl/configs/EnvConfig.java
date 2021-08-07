package com.wllfengshu.jetl.configs;

import com.alibaba.fastjson.JSON;
import com.wllfengshu.jetl.common.Constant;
import com.wllfengshu.jetl.dao.EtlDao;
import com.wllfengshu.jetl.model.ScriptVO;
import com.wllfengshu.jetl.model.enumerate.JobTimeInterval;
import com.wllfengshu.jetl.utils.DbUtil;
import com.wllfengshu.jetl.utils.FileUtil;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务环境的统一配置，服务中使用的环境变量都要在此类中定义
 *
 * @author wangll
 */
@Slf4j
@Data
@Component
@Order(0)
public class EnvConfig implements InitializingBean {

    /**
     * 定时任务
     */
    @Value("${second30_cron:1,31 * * * * ?}")
    private String second30Cron;
    @Value("${minute_cron:0 */1 * * * ?}")
    private String minuteCron;
    @Value("${minute15_cron:0 1,16,31,46 * * * ?}")
    private String minute15Cron;
    @Value("${hour_cron:0 15 * * * ?}")
    private String hourCron;
    @Value("${day_cron:0 0 1 * * ?}")
    private String dayCron;
    @Value("${month_cron:0 0 2 1 * ?}")
    private String monthCron;

    /**
     * 输入数据库
     */
    @Value("${mysql_slave_dbUrl:jdbc:mysql://192.168.40.82:3306/wellcloud?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true&useSSL=false&serverTimezone=GMT%2B8}")
    private String inputDbUrl;
    @Value("${mysql_slave_userName:root}")
    private String inputDbUsername;
    @Value("${mysql_slave_password:Welljoint,123}")
    private String inputDbPassword;
    private String inputDbIp;
    private String inputDbPort;
    private String inputDbName;

    /**
     * 输出数据库
     */
    @Value("${mysql_dbUrl:jdbc:mysql://192.168.40.82:3306/wellcloud?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true&useSSL=false&serverTimezone=GMT%2B8}")
    private String outputDbUrl;
    @Value("${mysql_userName:root}")
    private String outputDbUsername;
    @Value("${mysql_password:Welljoint,123}")
    private String outputDbPassword;
    private String outputDbIp;
    private String outputDbPort;
    private String outputDbName;

    /**
     * influxDB配置
     */
    @Value("${influxdb_dbname:cloud2}")
    private String influxdbName;
    @Value(value = "${influxdb_retention_policy:cloud2}")
    private String influxdbRetentionPolicy;

    /**
     * fs的core数据库
     */
    @Value("${mysql_wss_dbUrl:jdbc:mysql://192.168.40.82:3306/core_2_4?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8}")
    private String fsCoreDbUrl;
    @Value("${mysql_wss_userName:root}")
    private String fsCoreDbUsername;
    @Value("${mysql_wss_password:Welljoint,123}")
    private String fsCoreDbPassword;

    /**
     * flyway版本控制相关参数
     */
    @Value("${flyway_schema_version:2.0.0.0}")
    private String flywaySchemaVersion;
    @Value("${flyway_schema_table_name:etl_schema_history}")
    private String flywaySchemaTableName;

    /**
     * 定时任务线程池大小
     */
    @Value("${job_thread_pool_size:4}")
    private String jobThreadPoolSize;

    /**
     * kettle执行的环境变量集合(包含输入、输出的数据库配置等)
     */
    public static final Map<String,String> ETL_ENV = new HashMap<>(16);

    /**
     * kettle脚本集合(格式<脚本名称，脚本内容>)
     */
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KTRS_SECOND30 = new HashMap<>(16);
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KTRS_MINUTE = new HashMap<>(16);
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KTRS_MINUTE15 = new HashMap<>(16);
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KTRS_HOUR = new HashMap<>(16);
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KTRS_DAY = new HashMap<>(16);
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KTRS_MONTH = new HashMap<>(16);
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KJBS_SECOND30 = new HashMap<>(16);
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KJBS_MINUTE = new HashMap<>(16);
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KJBS_MINUTE15 = new HashMap<>(16);
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KJBS_HOUR = new HashMap<>(16);
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KJBS_DAY = new HashMap<>(16);
    public static final Map<String,String> CONSTANT_SCRIPT_MAP_KJBS_MONTH = new HashMap<>(16);

    /**
     * 待执行的脚本集合（已经封装成待执行的对象；Map的key为脚本执行的优先级）
     */
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KTRS_SECOND30 = new ArrayList<>(16);
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KTRS_MINUTE = new ArrayList<>(16);
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KTRS_MINUTE15 = new ArrayList<>(16);
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KTRS_HOUR = new ArrayList<>(16);
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KTRS_DAY = new ArrayList<>(16);
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KTRS_MONTH = new ArrayList<>(16);
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KJBS_SECOND30 = new ArrayList<>(16);
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KJBS_MINUTE = new ArrayList<>(16);
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KJBS_MINUTE15 = new ArrayList<>(16);
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KJBS_HOUR = new ArrayList<>(16);
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KJBS_DAY = new ArrayList<>(16);
    public static final List<List<ScriptVO>> CONSTANT_SCRIPTVO_LIST_KJBS_MONTH = new ArrayList<>(16);

    @NonNull
    private EtlDao etlDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        //1 处理数据库
        Map<String, String> inputMap = DbUtil.dealDbUrl(inputDbUrl);
        inputDbIp = inputMap.get("dbIp");
        inputDbPort = inputMap.get("dbPort");
        inputDbName = inputMap.get("dbName");
        Map<String, String> outputMap = DbUtil.dealDbUrl(outputDbUrl);
        outputDbIp = outputMap.get("dbIp");
        outputDbPort = outputMap.get("dbPort");
        outputDbName = outputMap.get("dbName");
        //2 处理kettle环境变量
        ETL_ENV.put("inputDbIp",inputDbIp);
        ETL_ENV.put("inputDbPort",inputDbPort);
        ETL_ENV.put("inputDbName",inputDbName);
        ETL_ENV.put("inputDbUsername",inputDbUsername);
        ETL_ENV.put("inputDbPassword",inputDbPassword);
        ETL_ENV.put("outputDbIp",outputDbIp);
        ETL_ENV.put("outputDbPort",outputDbPort);
        ETL_ENV.put("outputDbName",outputDbName);
        ETL_ENV.put("outputDbUsername",outputDbUsername);
        ETL_ENV.put("outputDbPassword",outputDbPassword);
        //3 处理kettle脚本
        List<String> noKettleNames = etlDao.queryEtlNoAggregation();
        Pair<Map<String,String>,Map<String,String>> second30Map = FileUtil.giveKettleFile(Constant.WORKSPACE_PATH_SECOND30,noKettleNames);
        CONSTANT_SCRIPT_MAP_KTRS_SECOND30.putAll(second30Map.getKey());
        CONSTANT_SCRIPT_MAP_KJBS_SECOND30.putAll(second30Map.getValue());
        Pair<Map<String,String>,Map<String,String>> minuteMap = FileUtil.giveKettleFile(Constant.WORKSPACE_PATH_MINUTE,noKettleNames);
        CONSTANT_SCRIPT_MAP_KTRS_MINUTE.putAll(minuteMap.getKey());
        CONSTANT_SCRIPT_MAP_KJBS_MINUTE.putAll(minuteMap.getValue());
        Pair<Map<String,String>,Map<String,String>> minute15Map = FileUtil.giveKettleFile(Constant.WORKSPACE_PATH_MINUTE15,noKettleNames);
        CONSTANT_SCRIPT_MAP_KTRS_MINUTE15.putAll(minute15Map.getKey());
        CONSTANT_SCRIPT_MAP_KJBS_MINUTE15.putAll(minute15Map.getValue());
        Pair<Map<String,String>,Map<String,String>> hourMap = FileUtil.giveKettleFile(Constant.WORKSPACE_PATH_HOUR,noKettleNames);
        CONSTANT_SCRIPT_MAP_KTRS_HOUR.putAll(hourMap.getKey());
        CONSTANT_SCRIPT_MAP_KJBS_HOUR.putAll(hourMap.getValue());
        Pair<Map<String,String>,Map<String,String>> dayMap = FileUtil.giveKettleFile(Constant.WORKSPACE_PATH_DAY,noKettleNames);
        CONSTANT_SCRIPT_MAP_KTRS_DAY.putAll(dayMap.getKey());
        CONSTANT_SCRIPT_MAP_KJBS_DAY.putAll(dayMap.getValue());
        Pair<Map<String,String>,Map<String,String>> monthMap = FileUtil.giveKettleFile(Constant.WORKSPACE_PATH_MONTH,noKettleNames);
        CONSTANT_SCRIPT_MAP_KTRS_MONTH.putAll(monthMap.getKey());
        CONSTANT_SCRIPT_MAP_KJBS_MONTH.putAll(monthMap.getValue());
        //4 把kettle脚本封装成scriptVO对象
        CONSTANT_SCRIPTVO_LIST_KTRS_SECOND30.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KTRS_SECOND30, JobTimeInterval.Second30));
        CONSTANT_SCRIPTVO_LIST_KJBS_SECOND30.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KJBS_SECOND30, JobTimeInterval.Second30));
        CONSTANT_SCRIPTVO_LIST_KTRS_MINUTE.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KTRS_MINUTE, JobTimeInterval.Minute));
        CONSTANT_SCRIPTVO_LIST_KJBS_MINUTE.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KJBS_MINUTE, JobTimeInterval.Minute));
        CONSTANT_SCRIPTVO_LIST_KTRS_MINUTE15.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KTRS_MINUTE15, JobTimeInterval.Minute15));
        CONSTANT_SCRIPTVO_LIST_KJBS_MINUTE15.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KJBS_MINUTE15, JobTimeInterval.Minute15));
        CONSTANT_SCRIPTVO_LIST_KTRS_HOUR.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KTRS_HOUR, JobTimeInterval.Hour));
        CONSTANT_SCRIPTVO_LIST_KJBS_HOUR.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KJBS_HOUR, JobTimeInterval.Hour));
        CONSTANT_SCRIPTVO_LIST_KTRS_DAY.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KTRS_DAY, JobTimeInterval.Day));
        CONSTANT_SCRIPTVO_LIST_KJBS_DAY.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KJBS_DAY, JobTimeInterval.Day));
        CONSTANT_SCRIPTVO_LIST_KTRS_MONTH.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KTRS_MONTH, JobTimeInterval.Month));
        CONSTANT_SCRIPTVO_LIST_KJBS_MONTH.addAll(FileUtil.giveScriptVosList(CONSTANT_SCRIPT_MAP_KJBS_MONTH, JobTimeInterval.Month));
        //5 输出日志
        log.info("定时任务变量：second30Cron:{},minuteCron:{},minute15Cron:{},hourCron:{},dayCron:{},monthCron:{}",second30Cron,minuteCron,minute15Cron,hourCron,dayCron,monthCron);
        log.info("数据库变量：inputDbUrl:{},inputDbUsername:{},outputDbUrl:{},outputDbUsername:{}",inputDbUrl,inputDbUsername,outputDbUrl,outputDbUsername);
        log.info("influxdb变量：influxdbName:{}",influxdbName);
        log.info("kettle环境变量：{}",JSON.toJSONString(ETL_ENV));
        log.info("不聚合的kettle脚本名集合：{}", JSON.toJSONString(noKettleNames));
        log.info("kettle的ktr脚本数量：second30Ktrs:{},minuteKtrs:{},minute15Ktrs:{},hourKtrs:{},dayKtrs:{},monthKtrs:{}",
                CONSTANT_SCRIPT_MAP_KTRS_SECOND30.size(),CONSTANT_SCRIPT_MAP_KTRS_MINUTE.size(),CONSTANT_SCRIPT_MAP_KTRS_MINUTE15.size(),
                CONSTANT_SCRIPT_MAP_KTRS_HOUR.size(),CONSTANT_SCRIPT_MAP_KTRS_DAY.size(),CONSTANT_SCRIPT_MAP_KTRS_MONTH.size());
        log.info("kettle的kjb脚本数量：second30Kjbs:{},minuteKjbs:{},minute15Kjbs:{},hourKjbs:{},dayKjbs:{},monthKjbs:{}",
                CONSTANT_SCRIPT_MAP_KJBS_SECOND30.size(),CONSTANT_SCRIPT_MAP_KJBS_MINUTE.size(),CONSTANT_SCRIPT_MAP_KJBS_MINUTE15.size(),
                CONSTANT_SCRIPT_MAP_KJBS_HOUR.size(),CONSTANT_SCRIPT_MAP_KJBS_DAY.size(),CONSTANT_SCRIPT_MAP_KJBS_MONTH.size());
        log.info("封装后kettle的ktr对象集合：second30ScriptVoKtrs:{},minuteScriptVoKtrs:{},minute15ScriptVoKtrs:{},hourScriptVoKtrs:{},dayScriptVoKtrs:{},monthScriptVoKtrs:{}",
                JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KTRS_SECOND30),JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KTRS_MINUTE),JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KTRS_MINUTE15),
                JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KTRS_HOUR),JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KTRS_DAY),JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KTRS_MONTH));
        log.info("封装后kettle的kjb对象集合：second30ScriptVoKjbs:{},minuteScriptVoKjbs:{},minute15ScriptVoKjbs:{},hourScriptVoKjbs:{},dayScriptVoKjbs:{},monthScriptVoKjbs:{}",
                JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KJBS_SECOND30),JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KJBS_MINUTE),JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KJBS_MINUTE15),
                JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KJBS_HOUR),JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KJBS_DAY),JSON.toJSONString(CONSTANT_SCRIPTVO_LIST_KJBS_MONTH));
        log.info("flyway版本控制相关参数：flywaySchemaVersion:{},flywaySchemaTableName:{}",flywaySchemaVersion,flywaySchemaTableName);
        log.info("fs的core数据库配置：fsCoreDbUrl:{},fsCoreDbUsername:{},fsCoreDbPassword:{}",fsCoreDbUrl,fsCoreDbUsername,fsCoreDbPassword);
    }
}


