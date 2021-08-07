package com.wllfengshu.jetl.model;

import com.wllfengshu.jetl.model.enumerate.JobTimeInterval;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 脚本实体类
 *
 * @author wangll
 */
@Data
public class ScriptVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 脚本名称（即workspace目录中脚本的名称；唯一）
     */
    private String kettleName;

    /**
     * 依赖的脚本
     */
    private List<ScriptVO> dependScriptVos;

    /**
     * job执行的时间间隔的类型
     */
    private JobTimeInterval jobTimeInterval;

    /**
     * 脚本执行所需的环境变量（不得包含EnvConfig中ETL_ENV中的）
     */
    private Map<String,String> etlEnv;

    /**
     * 脚本执行的优先级（0,1,2...；0最高；当且仅当该脚本不依赖其他脚本时该值为0）
     */
    private Integer priority;
}
