package com.wllfengshu.jetl.init;

import com.wllfengshu.jetl.configs.EnvConfig;
import com.wllfengshu.jetl.exception.CustomException;
import com.wllfengshu.jetl.job.*;
import com.wllfengshu.jetl.model.QuartzJob;
import com.wllfengshu.jetl.utils.QuartzUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 系统初始化
 *
 * @author wangll
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitRunner implements CommandLineRunner {

    @NonNull
    private EnvConfig envConfig;
    @NonNull
    private QuartzUtil quartzUtil;

    @Override
    public void run(String... args) throws CustomException {
        log.info("正在初始化全部定时任务...");

        QuartzJob s30 = new QuartzJob();
        s30.setJobName("etl_second30");
        s30.setTriggerName("etl_second30");
        s30.setCronExpression(envConfig.getSecond30Cron());
        s30.setDescription("etl 30秒的定时任务");
        quartzUtil.addJob(s30, JobForSecond30.class.getName());
        log.info("30秒-定时任务添加完毕");

        QuartzJob m = new QuartzJob();
        m.setJobName("etl_minute");
        m.setTriggerName("etl_minute");
        m.setCronExpression(envConfig.getMinuteCron());
        m.setDescription("etl 1分钟的定时任务");
        quartzUtil.addJob(m, JobForMinute.class.getName());
        log.info("1分钟-定时任务添加完毕");

        QuartzJob m15 = new QuartzJob();
        m15.setJobName("etl_minute15");
        m15.setTriggerName("etl_minute15");
        m15.setCronExpression(envConfig.getMinute15Cron());
        m15.setDescription("etl 15分钟的定时任务");
        quartzUtil.addJob(m15, JobForMinute15.class.getName());
        log.info("15分钟-定时任务添加完毕");

        QuartzJob h = new QuartzJob();
        h.setJobName("etl_hour");
        h.setTriggerName("etl_hour");
        h.setCronExpression(envConfig.getHourCron());
        h.setDescription("etl 小时的定时任务");
        quartzUtil.addJob(h, JobForHour.class.getName());
        log.info("小时-定时任务添加完毕");

        QuartzJob d = new QuartzJob();
        d.setJobName("etl_day");
        d.setTriggerName("etl_day");
        d.setCronExpression(envConfig.getDayCron());
        d.setDescription("etl 每天的定时任务");
        quartzUtil.addJob(d, JobForDay.class.getName());
        log.info("日-定时任务添加完毕");

        QuartzJob mo = new QuartzJob();
        mo.setJobName("etl_month");
        mo.setTriggerName("etl_month");
        mo.setCronExpression(envConfig.getMonthCron());
        mo.setDescription("etl 每月的定时任务");
        quartzUtil.addJob(mo, JobForMonth.class.getName());
        log.info("月-定时任务添加完毕");

        log.info("全部定时任务已添加完毕！");
    }
}