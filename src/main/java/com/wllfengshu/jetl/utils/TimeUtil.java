package com.wllfengshu.jetl.utils;

import com.wllfengshu.jetl.common.Constant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/**
 * 时间工具类
 *
 * @author wangll
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeUtil {

    private static final long ONE_MINUTE_TIME = 60000L;

    /**
     * 把时间转换为timeId
     * 注：由于数据库聚合表的时间存储的是timeId格式，故需要把时间格式转成timeId格式
     * （快速）
     *
     * @param dateTime
     * @return
     */
    public static String date2timeId2(String dateTime) {
        return dateTime.replaceAll("-|:| ", "");
    }

    /**
     * 把时间转换为timeId
     * 注：由于数据库聚合表的时间存储的是timeId格式，故需要把时间格式转成timeId格式
     *
     * @param dateTime
     * @return
     */
    public static String date2timeId(@NonNull String dateTime) {
        try {
            return DateFormatUtils.format(DateUtils.parseDate(dateTime, Constant.FORMAT_DEFAULT), "yyyyMMddHHmm");
        } catch (Exception e) {
            log.error("TimeUtil date2timeId error", e);
        }
        return null;
    }

    /**
     * 限制一次查询时间跨度
     *
     * @param callTimeBegin
     * @param callTimeEnd
     * @param min           分钟
     * @return true 检查通过
     * false 检查不通过
     */
    public static boolean checkTimeSpan(@NonNull String callTimeBegin, @NonNull String callTimeEnd, int min) {
        log.info("TimeUtil checkTimeSpan------>callTimeBegin:{},callTimeEnd:{}", callTimeBegin, callTimeEnd);
        try {
            Date bDate = DateUtils.parseDate(callTimeBegin, Constant.FORMAT_DEFAULT);
            Date eDate = DateUtils.parseDate(callTimeEnd, Constant.FORMAT_DEFAULT);
            if ((eDate.getTime() - bDate.getTime()) / ONE_MINUTE_TIME <= min) {
                return true;
            }
        } catch (Exception e) {
            log.error("TimeUtil checkTimeSpan error", e);
        }
        return false;
    }
}
