package com.wllfengshu.jetl.job;

import com.wllfengshu.jetl.model.ScriptVO;
import com.wllfengshu.jetl.utils.KettleUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * 多线程执行kettle脚本
 *
 * @author wangll
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Job {

    @NonNull
    private ExecutorService executorService;

    /**
     * 执行etl
     *
     * @param scriptVosList
     */
    public void etlData(List<List<ScriptVO>> scriptVosList) {
        log.info("开始执行定时任务，一共有{}个优先级...",scriptVosList.size());
        for (int i = 0; i < scriptVosList.size(); i++) {
            List<ScriptVO> scriptVos = scriptVosList.get(i);
            log.info("------>主线程-开始优先级为{}脚本的执行，本优先级共有{}个脚本",i,scriptVos.size());
            final CountDownLatch latch = new CountDownLatch(scriptVos.size());
            scriptVos.forEach(scriptVo -> executorService.execute(() -> {
                try {
                    log.info("子线程：{}，1、正在执行kettle脚本：{}", Thread.currentThread().getName(),scriptVo);
                    KettleUtil.etlData(scriptVo);
                    log.info("子线程：{}，2、kettle脚本执行完毕：{}", Thread.currentThread().getName(),scriptVo);
                }catch (Exception e){
                    log.error("{}脚本执行发生异常",scriptVo);
                }finally {
                    latch.countDown();
                }
            }));
            try {
                log.debug("主线程-正在等待优先级为{}的脚本执行完毕",i);
                // 阻塞当前线程，直到计数器的值为0
                latch.await();
            } catch (Exception e) {
                log.error("主线程-等待优先级为{}脚本执行时发生异常",i);
            }
            log.info("------>主线程-优先级为{}脚本的执行完毕",i);
        }
        log.info("所有定时任务都执行完毕");
    }
}
