package com.wllfengshu.jetl.utils;

import com.wllfengshu.jetl.configs.EnvConfig;
import com.wllfengshu.jetl.model.ScriptVO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * kettle工具类
 * 使用静态块的原因：
 * 1.执行ktr和kjb都要执行初始化，为了代码的复用；
 * 2.由于quartz的定时任务是异步的，在springBoot刚刚启动完成就可能会执行ktr和kjb，所以kettle的初始化工作
 * 必须在springBoot启动完成前执行，故使用静态块；
 * 注意：etl中不使用kjb脚本，ktr的执行顺序由程序控制
 *
 * @author wangll
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KettleUtil {

    static {
        log.info("正在初始化kettle ...");
        try {
            KettleUtil.dealWriteNull();
            KettleEnvironment.init();
            EnvUtil.environmentInit();
        } catch (Exception e) {
            log.error("初始化kettle失败", e);
            System.exit(-1);
        }
        log.info("初始化kettle成功！");
    }

    /**
     * 执行kettle脚本
     *
     * @param scriptVO 脚本对象
     */
    public static void etlData(@NonNull ScriptVO scriptVO) {
        String kettleContent = StringUtil.giveKettleContent(scriptVO);
        if ("-1".equals(kettleContent)) {
            log.error("根据脚本名无法找到脚本内容");
            return;
        }
        if (StringUtil.isKtr(scriptVO.getKettleName())) {
            KettleUtil.runTrans(kettleContent, scriptVO.getEtlEnv());
        } else {
            KettleUtil.runJob(kettleContent, scriptVO.getEtlEnv());
        }
    }

    /**
     * 执行ktr文件内容
     *
     * @param file
     * @param etlEnv
     */
    private static void runTrans(String file, Map<String, String> etlEnv) {
        log.info("正在执行ktr ...");
        try {
            TransMeta transMeta = new TransMeta(new ByteArrayInputStream(file.getBytes(StandardCharsets.UTF_8)), null, true, null, null);
            Trans trans = new Trans(transMeta);
            Map<String, String> params = KettleUtil.dealParams(etlEnv);
            params.forEach(trans::setVariable);
            trans.prepareExecution(null);
            trans.startThreads();
            trans.waitUntilFinished();
            if (trans.getErrors() != 0) {
                log.error("执行ktr过程中存在错误");
            }
        } catch (Exception e) {
            log.error("runTrans失败", e);
        }
        log.info("ktr执行完毕！");
    }

    /**
     * 执行kjb文件内容
     *
     * @param file
     * @param etlEnv
     * @deprecated 请不要使用job
     */
    @Deprecated
    private static void runJob(String file, Map<String, String> etlEnv) {
        log.info("正在执行kjb ...");
        try {
            JobMeta jobMeta = new JobMeta(new ByteArrayInputStream(file.getBytes(StandardCharsets.UTF_8)), null, null);
            Job job = new Job(null, jobMeta);
            Map<String, String> params = KettleUtil.dealParams(etlEnv);
            params.forEach(job::setVariable);
            job.start();
            job.waitUntilFinished();
            if (job.getErrors() != 0) {
                log.error("执行kjb过程中存在错误");
            }
        } catch (Exception e) {
            log.error("runJob失败", e);
        }
        log.info("kjb执行完毕...");
    }

    /**
     * 处理kettle执行的环境变量（可以在kettle脚本中使用“$变量名”使用该变量）
     *
     * @param etlEnv
     */
    private static Map<String, String> dealParams(Map<String, String> etlEnv) {
        Map<String, String> params = new HashMap<>(16);
        params.putAll(EnvConfig.ETL_ENV);
        params.putAll(etlEnv);
        if (params.containsKey("beginTime")) {
            String beginTime = params.get("beginTime");
            // 时间戳格式，例如：输入是2020-10-30 10:11:22，返回202010301011
            params.put("beginTimeId", TimeUtil.date2timeId(beginTime));
            // 指定时间的0点，例如：输入是2020-10-30 10:11:22，返回2020-10-30 00:00:00
            params.put("beginTimeZeroPoint", beginTime.split(" ")[0] + " 00:00:00");
        }
        if (params.containsKey("endTime")) {
            String endTime = params.get("endTime");
            params.put("endTimeId", TimeUtil.date2timeId(endTime));
            // 指定时间的23点59分59秒，例如：输入是2020-10-30 10:11:22，返回2020-10-30 23:59:59
            params.put("endTime235959", endTime.split(" ")[0] + " 23:59:59");
        }
        log.debug("kettle参数：{}", params);
        return params;
    }

    /**
     * 解决kettle无法写入空字符串的问题
     * 例如：window环境中，需要在C:\Users\wangl\.kettle\kettle.properties中写入如下配置；
     * linux环境中，需要在/root/.kettle/kettle.properties中写入如下配置
     */
    private static void dealWriteNull() {
        String directory = Const.getKettleDirectory();
        File dir = new File(directory);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.error("dealWriteNull创建配置文件所在的文件夹失败：{}", dir.getAbsolutePath());
                System.exit(-1);
            }
        }
        KettleClientEnvironment.createKettleHome();
        String kpFile = directory + Const.FILE_SEPARATOR + "kettle.properties";
        File file = new File(kpFile);
        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("KETTLE_EMPTY_STRING_DIFFERS_FROM_NULL=Y");
        } catch (Exception e) {
            log.error("kettle初始化dealWriteNull异常，无法创建配置文件!");
            System.exit(-1);
        }
    }
}
