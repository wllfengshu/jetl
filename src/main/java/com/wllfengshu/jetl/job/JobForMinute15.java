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
 * 15分钟定时任务
 *
 * @author wangll
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobForMinute15 extends QuartzJobBean {

    @NonNull
    private Job job;

    /**
     * 分钟定时任务
     * 注意：
     *    1、假如设置的定时时间为15分钟，且定时任务被延时超过15分钟，则聚合会丢失数据。
     *       例如：数据库定时任务是10:16，但是由于网络等问题，等到该定时任务执行到本方法时时间到了10:31，那么本方法
     *            在计算start和end时，得出的结果是10:15-10:30，明显不对，正确的应该是10:00-10:15，该数据丢失。
     *            但是该情况出现的概率非常低，暂不考虑。
     *    3、这里的beginTime和endTime必须是一边包含一边不包含，否则会数据重复
     *       本项目统一操作：包含begin，不包含end
     * @param jobExecutionContext
     */
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext){
        // 以下先按照15分钟算
        Calendar now = Calendar.getInstance();
        int min = (now.get(Calendar.MINUTE))/15;
        String retainTime = DateFormatUtils.format(now,"yyyy-MM-dd HH");
        String beginTime = "";
        String endTime = "";
        switch (min){
            // 当前时间分钟数是[0,15)，则：开始时间是上一个小时的45分钟，结束时间是当前小时的0分钟
            case 0:
                endTime = retainTime + ":00:00";
                now.add(Calendar.HOUR_OF_DAY, -1);
                retainTime = DateFormatUtils.format(now,"yyyy-MM-dd HH");
                beginTime = retainTime + ":45:00";
                break;
            // 当前时间分钟数是[15,30)，则：开始时间是0分钟，结束时间是15分钟
            case 1:
                beginTime = retainTime + ":00:00";
                endTime = retainTime + ":15:00";
                break;
            case 2:
                beginTime = retainTime + ":15:00";
                endTime = retainTime + ":30:00";
                break;
            case 3:
                beginTime = retainTime + ":30:00";
                endTime = retainTime + ":45:00";
                break;
            default:
                return;
        }
        this.execute(beginTime,endTime);
    }

    public void execute(String beginTime,String endTime){
        Map<String,String> etlEnv = new HashMap<>(2);
        etlEnv.put("beginTime",beginTime);
        etlEnv.put("endTime",endTime);
        log.info("kettle脚本开始执行，系统变量：{}",etlEnv);

        List<List<ScriptVO>> ktrs = StringUtil.deepCopy(EnvConfig.CONSTANT_SCRIPTVO_LIST_KTRS_MINUTE15);
        ktrs.forEach(list -> list.forEach(scriptVO -> scriptVO.setEtlEnv(etlEnv)));
        job.etlData(ktrs);

        List<List<ScriptVO>> kjbs = StringUtil.deepCopy(EnvConfig.CONSTANT_SCRIPTVO_LIST_KJBS_MINUTE15);
        kjbs.forEach(list -> list.forEach(scriptVO -> scriptVO.setEtlEnv(etlEnv)));
        job.etlData(kjbs);

        log.info("kettle脚本都执行完毕");
    }
}
