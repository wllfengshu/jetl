package com.wllfengshu.jetl.utils;

import com.wllfengshu.jetl.exception.CustomException;
import com.wllfengshu.jetl.model.QuartzJob;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

/**
 * quartz工具类
 *
 * @author wangll
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzUtil {

    @NonNull
    private Scheduler scheduler;

    /**
     * job组
     */
    private static final String JOB_GROUP = "ETL";

    /**
     * 添加任务
     *
     * @param quartz
     * @param jobClassName
     * @throws CustomException
     */
    public void addJob(@NonNull QuartzJob quartz, @NonNull String jobClassName) throws CustomException {
        log.info("正在添加定时任务，quartz={} ...", quartz);
        try {
            //1 构建jobKey判断任务是否已经存在
            JobKey jobKey = new JobKey(quartz.getJobName(), QuartzUtil.JOB_GROUP);
            if (scheduler.checkExists(jobKey)) {
                log.info("该定时任务已存在！");
                return;
            }
            //2 构建job信息
            Class cls = Class.forName(jobClassName);
            JobDetail job = JobBuilder.newJob(cls).withIdentity(jobKey)
                                      .withDescription(quartz.getDescription()).build();
            //3 触发时间点
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression().trim());
            //4 构建triggerKey判断触发器是否已经存在
            TriggerKey triggerKey = new TriggerKey(quartz.getTriggerName(), QuartzUtil.JOB_GROUP);
            if (scheduler.checkExists(triggerKey)) {
                log.info("该触发器已存在！");
                return;
            }
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                                            .startNow().withSchedule(cronScheduleBuilder).build();
            //5 交由Scheduler安排触发
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            log.error("保存job失败", e);
            throw new CustomException("保存job失败", CustomException.ExceptionName.FailSaveJob);
        }
        log.info("定时任务添加成功！");
    }
}
