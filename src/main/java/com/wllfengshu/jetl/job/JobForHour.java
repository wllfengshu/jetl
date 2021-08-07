package com.wllfengshu.jetl.job;

import com.wllfengshu.jetl.configs.EnvConfig;
import com.wllfengshu.jetl.model.ScriptVO;
import com.wllfengshu.jetl.utils.StringUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小时定时任务
 *
 * @author wangll
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobForHour extends QuartzJobBean {

    @NonNull
    private Job job;

    /**
     * 小时定时任务
     * 注意：
     *    1、定时任务延时超过1小时，会丢失数据
     *    2、这里的beginTime和endTime，包含begin，不包含end
     * @param jobExecutionContext
     */
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext){
        Calendar now = Calendar.getInstance();
        String endTime = DateFormatUtils.format(now,"yyyy-MM-dd HH") + ":00:00";
        now.add(Calendar.HOUR_OF_DAY, -1);
        String beginTime = DateFormatUtils.format(now,"yyyy-MM-dd HH") + ":00:00";
        this.execute(beginTime,endTime);
    }

    public void execute(String beginTime,String endTime){
        Map<String,String> etlEnv = new HashMap<>(2);
        etlEnv.put("beginTime",beginTime);
        etlEnv.put("endTime",endTime);
        log.info("kettle脚本开始执行，系统变量：{}",etlEnv);

        List<List<ScriptVO>> ktrs = StringUtil.deepCopy(EnvConfig.CONSTANT_SCRIPTVO_LIST_KTRS_HOUR);
        ktrs.forEach(list -> list.forEach(scriptVO -> scriptVO.setEtlEnv(etlEnv)));
        job.etlData(ktrs);

        List<List<ScriptVO>> kjbs = StringUtil.deepCopy(EnvConfig.CONSTANT_SCRIPTVO_LIST_KJBS_HOUR);
        kjbs.forEach(list -> list.forEach(scriptVO -> scriptVO.setEtlEnv(etlEnv)));
        job.etlData(kjbs);

        log.info("kettle脚本都执行完毕");
    }
}
