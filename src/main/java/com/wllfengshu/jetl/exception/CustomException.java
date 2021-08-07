package com.wllfengshu.jetl.exception;

/**
 * 自定义异常
 *
 * @author
 */
public class CustomException extends Exception {

    protected ExceptionName exceptionName;

    public enum ExceptionName {
        //没有权限
        Unauthenticated(401),
        //非法参数
        IllegalParam(400),
        //删除job失败
        FailDeleteJob(10001),
        //启动所有定时job失败
        FailStartAllJob(10002),
        //关闭所有定时job失败
        FailShutdownAllJob(10003),
        //保存job失败
        FailSaveJob(10004),
        //触发job失败
        FailTriggerJob(10005),
        //暂停job失败
        FailPauseJob(10006),
        //恢复job失败
        FailResumeJob(10007),
        //执行kjb失败
        FailRunKjb(10008),
        //执行ktr或kjb失败
        FailRunKtrOrKjb(10009),
        //时间参数不合法
        IllegalTimeParam(10010)
        ;

        private int code;

        ExceptionName(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public CustomException(String message, ExceptionName exceptionName) {
        super(message);
        this.exceptionName = exceptionName;
    }

    public ExceptionName getExceptionName() {
        return exceptionName;
    }
}
