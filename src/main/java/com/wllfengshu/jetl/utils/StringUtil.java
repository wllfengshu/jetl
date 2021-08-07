package com.wllfengshu.jetl.utils;

import com.wllfengshu.jetl.configs.EnvConfig;
import com.wllfengshu.jetl.model.ScriptVO;
import com.wllfengshu.jetl.model.enumerate.KettleType;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 字符串工具类
 *
 * @author wangll
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {

    /**
     * 根据kettle脚本名判断脚本是否是ktr类型
     *
     * @param kettleName
     * @return
     */
    public static boolean isKtr(@NonNull String kettleName){
        return kettleName.endsWith(KettleType.ktr.name());
    }

    /**
     * 根据kettle脚本名获取脚本内容
     *
     * @param scriptVO
     * @return
     */
    public static String giveKettleContent(@NonNull ScriptVO scriptVO){
        if (StringUtil.isKtr(scriptVO.getKettleName())){
            switch (scriptVO.getJobTimeInterval()){
                case Second30:
                    return EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_SECOND30.get(scriptVO.getKettleName());
                case Minute:
                    return EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_MINUTE.get(scriptVO.getKettleName());
                case Minute15:
                    return EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_MINUTE15.get(scriptVO.getKettleName());
                case Hour:
                    return EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_HOUR.get(scriptVO.getKettleName());
                case Day:
                    return EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_DAY.get(scriptVO.getKettleName());
                case Month:
                    return EnvConfig.CONSTANT_SCRIPT_MAP_KTRS_MONTH.get(scriptVO.getKettleName());
                default:
                    log.error("ktr匹配不到脚本");
                    return "-1";
            }
        }
        switch (scriptVO.getJobTimeInterval()){
            case Second30:
                return EnvConfig.CONSTANT_SCRIPT_MAP_KJBS_SECOND30.get(scriptVO.getKettleName());
            case Minute:
                return EnvConfig.CONSTANT_SCRIPT_MAP_KJBS_MINUTE.get(scriptVO.getKettleName());
            case Minute15:
                return EnvConfig.CONSTANT_SCRIPT_MAP_KJBS_MINUTE15.get(scriptVO.getKettleName());
            case Hour:
                return EnvConfig.CONSTANT_SCRIPT_MAP_KJBS_HOUR.get(scriptVO.getKettleName());
            case Day:
                return EnvConfig.CONSTANT_SCRIPT_MAP_KJBS_DAY.get(scriptVO.getKettleName());
            case Month:
                return EnvConfig.CONSTANT_SCRIPT_MAP_KJBS_MONTH.get(scriptVO.getKettleName());
            default:
                log.error("kjb匹配不到脚本");
                return "-1";
        }
    }

    /**
     * 深拷贝
     *
     * @param src
     * @param <T>
     * @return
     */
    public static <T> List<T> deepCopy(List<T> src){
        List<T> dest = null;
        try {
            @Cleanup ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            @Cleanup ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            @Cleanup ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            @Cleanup ObjectInputStream in = new ObjectInputStream(byteIn);
            dest = (List<T>) in.readObject();
        }catch (Exception e){
            log.error("深拷贝发生异常",e);
        }
        return dest;
    }
}
