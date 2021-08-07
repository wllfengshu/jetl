package com.wllfengshu.jetl.job;

import com.wllfengshu.jetl.configs.EnvConfig;
import com.wllfengshu.jetl.model.ScriptVO;
import com.wllfengshu.jetl.model.entity.TrunkInfo;
import com.wllfengshu.jetl.utils.InfluxDbUtil;
import com.wllfengshu.jetl.utils.MysqlUtil;
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
 * 30秒定时任务
 *
 * @author wangll
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobForSecond30 extends QuartzJobBean {

    @NonNull
    private Job job;
    @NonNull
    private EnvConfig envConfig;
    @NonNull
    private InfluxDbUtil influxDbUtil;

    /**
     * 30秒定时任务
     * 注意：
     *    1、定时任务延时超过30s会丢失数据
     *    2、这里的beginTime和endTime，包含begin，不包含end
     * @param jobExecutionContext
     */
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext){
        Calendar now = Calendar.getInstance();
        int s = (now.get(Calendar.SECOND))/30;
        String retainTime = DateFormatUtils.format(now,"yyyy-MM-dd HH:mm");
        String beginTime = "";
        String endTime = "";
        switch (s){
            case 0:
                endTime = retainTime + ":00";
                now.add(Calendar.MINUTE,-1);
                retainTime = DateFormatUtils.format(now,"yyyy-MM-dd HH:mm");
                beginTime = retainTime + ":30";
                break;
            case 1:
                beginTime = retainTime + ":00";
                endTime = retainTime + ":30";
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

        List<List<ScriptVO>> ktrs = StringUtil.deepCopy(EnvConfig.CONSTANT_SCRIPTVO_LIST_KTRS_SECOND30);
        ktrs.forEach(list -> list.forEach(scriptVO -> scriptVO.setEtlEnv(etlEnv)));
        job.etlData(ktrs);

        List<List<ScriptVO>> kjbs = StringUtil.deepCopy(EnvConfig.CONSTANT_SCRIPTVO_LIST_KJBS_SECOND30);
        kjbs.forEach(list -> list.forEach(scriptVO -> scriptVO.setEtlEnv(etlEnv)));
        job.etlData(kjbs);

        log.info("kettle脚本都执行完毕");
        this.writeTrunkInfo();
        log.info("写writeTrunkInfo定时任务完成");
    }

    /**
     * 从fs的core数据库中获取中继信息，并写入influxDb
     */
    private void writeTrunkInfo(){
        //TODO 1 需要修改该sql；2 还需要把可用数也写入
        String sqlUse = "SELECT tmp2.domain, tmp2.trunkName, COUNT(1) AS useNum , COUNT(tmp2.dir = 'callout') AS externalUseNum FROM ( SELECT replace(SUBSTRING_INDEX(tmp.profiles, '|', 1), 'domain#', '') AS domain , replace(SUBSTRING_INDEX(SUBSTRING_INDEX(tmp.profiles, '|', 2), '|', -1), 'trunk#', '') AS trunkName , replace(SUBSTRING_INDEX(tmp.profiles, '|', -1), 'dir#', '') AS dir FROM ( SELECT d.`profiles` FROM dialog d WHERE d.`profiles` IS NOT NULL AND d.`profiles` LIKE 'domain%' ) tmp ) tmp2 GROUP BY tmp2.domain, tmp2.trunkName;";
        List<TrunkInfo> trunkInfos = MysqlUtil.doQuery(envConfig.getFsCoreDbUrl(),envConfig.getFsCoreDbUsername(),envConfig.getFsCoreDbPassword(),sqlUse,TrunkInfo.class);
        for (TrunkInfo t:trunkInfos){
            t.setInboundAbleNum(t.getUseNum() - t.getExternalUseNum());
        }
        influxDbUtil.inserts(trunkInfos);
    }
}
