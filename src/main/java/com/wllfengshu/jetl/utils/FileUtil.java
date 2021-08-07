package com.wllfengshu.jetl.utils;

import com.alibaba.fastjson.JSON;
import com.wllfengshu.jetl.common.Constant;
import com.wllfengshu.jetl.model.ScriptVO;
import com.wllfengshu.jetl.model.enumerate.JobTimeInterval;
import com.wllfengshu.jetl.model.enumerate.KettleType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件工具类
 *
 * @author wangll
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {

    /**
     * 获取workspace中ktr和kjb文件的内容
     *
     * @param path
     * @param noKettleNames 不执行的kettle脚本名
     * @return
     */
    public static Pair<Map<String, String>, Map<String, String>> giveKettleFile(String path, List<String> noKettleNames) {
        Map<String, String> ktrsMap = new HashMap<>(16);
        Map<String, String> kjbsMap = new HashMap<>(16);
        try {
            File work = null;
            if (System.getenv().containsKey("JAR_Name")) {
                work = new File(Constant.WORKSPACE_BASE + path);
            } else {
                ClassPathResource resource = new ClassPathResource(path);
                if (resource.exists()) {
                    work = resource.getFile();
                }
            }
            if (work != null && work.exists()) {
                File[] files = Objects.requireNonNull(work.listFiles());
                for (File f : files) {
                    String fileName = f.getName();
                    if (noKettleNames.contains(fileName)) {
                        log.info("当前脚本被剔除:{}", fileName);
                    } else if (fileName.endsWith(KettleType.ktr.name())) {
                        ktrsMap.put(fileName, FileUtils.readFileToString(f, StandardCharsets.UTF_8));
                    } else if (fileName.endsWith(KettleType.kjb.name())) {
                        kjbsMap.put(fileName, FileUtils.readFileToString(f, StandardCharsets.UTF_8));
                    } else {
                        log.error("文件名不合法,它必须是ktr或kjb");
                    }
                }
            }
        } catch (Exception e) {
            log.error("本地获取kettle脚本失败", e);
        }
        log.info("本地获取kettle脚本完毕，path:{}", path);
        return new ImmutablePair<>(ktrsMap, kjbsMap);
    }

    /**
     * 获取依赖的脚本名
     *
     * @param pending   当前脚本名
     * @param scriptMap
     * @return
     */
    public static List<String> giveDependKettleNames(String pending, Map<String, String> scriptMap) {
        List<String> dependKettleNames = new ArrayList<>();
        if ((!pending.contains("-")) || scriptMap.isEmpty()) {
            log.debug("当前{}脚本，不依赖其他脚本。或原始脚本集为空", pending);
            return dependKettleNames;
        }
        // 例如：截取“5-3$4@agg_agent_work.ktr”中的“3$4”
        String[] ds = pending.split("-")[1].split("@")[0].split("\\$");
        for (String d : ds) {
            for (String scriptMapKey : scriptMap.keySet()) {
                if (scriptMapKey.startsWith(d)) {
                    dependKettleNames.add(scriptMapKey);
                    break;
                }
            }
        }
        log.debug("{}脚本giveDependKettleNames完毕，依赖的脚本：{}", pending, JSON.toJSONString(dependKettleNames));
        return dependKettleNames;
    }

    /**
     * 比较kettle脚本的文件名
     *
     * @param fName1
     * @param fName2
     */
    public static int compareKettleName(String fName1, String fName2) {
        if ((!fName1.contains("@")) || (!fName2.contains("@"))) {
            log.error("{},{}:脚本文件名必须包含@符号！", fName1, fName2);
            System.exit(-1);
        }
        // 例如：截取“5-3$4@agg_agent_work.ktr”中的“5”
        String order1 = fName1.split("@")[0].split("-")[0];
        String order2 = fName2.split("@")[0].split("-")[0];
        if (Integer.parseInt(order2) < Integer.parseInt(order1)) {
            return 1;
        }
        return -1;
    }

    /**
     * kettle脚本文件排序
     *
     * @param lists
     * @return
     */
    public static void orderFiles4ScriptVo(List<ScriptVO> lists) {
        lists.sort((o1, o2) -> FileUtil.compareKettleName(o1.getKettleName(), o2.getKettleName()));
    }

    /**
     * 把scriptMap封装成ScriptVo对象集合
     *
     * @param scriptMap
     * @return
     */
    public static List<ScriptVO> giveScriptVos(Map<String, String> scriptMap, JobTimeInterval jobTimeInterval) {
        List<ScriptVO> scriptVos = new ArrayList<>();
        scriptMap.forEach((k, v) -> {
            ScriptVO vo = new ScriptVO();
            vo.setKettleName(k);
            vo.setJobTimeInterval(jobTimeInterval);
            vo.setPriority(0);
            scriptVos.add(vo);
        });
        // 排序(按照脚本名的序号升序排列)
        FileUtil.orderFiles4ScriptVo(scriptVos);
        // 解决依赖(尤其需要注意：dependScriptVos中的对象和scriptVos中的对象是同一个!)
        scriptMap.forEach((k, v) -> scriptVos.forEach(vo -> {
            if (k.equals(vo.getKettleName())) {
                List<String> dependKettleNames = FileUtil.giveDependKettleNames(k, scriptMap);
                List<ScriptVO> dependScriptVos = new ArrayList<>();
                dependKettleNames.forEach(dk -> scriptVos.forEach(s -> {
                    if (dk.equals(s.getKettleName())) {
                        dependScriptVos.add(s);
                    }
                }));
                vo.setDependScriptVos(dependScriptVos);
            }
        }));
        // 检查是否存在循环依赖
        scriptVos.forEach(vo -> FileUtil.checkLoopDependent(vo, vo));
        // 设置脚本执行的优先级
        scriptVos.stream().filter(vo -> !vo.getDependScriptVos().isEmpty()).collect(Collectors.toList()).forEach(v -> v.setPriority(FileUtil.setPriority(v)));
        return scriptVos;
    }

    /**
     * 检查是否有循环依赖
     *
     * @param referenceVo 参考的vo(第一次调用者传过来的vo，在整个递归过程中不会改变)
     * @param pendingVo   待处理的vo
     */
    public static void checkLoopDependent(ScriptVO referenceVo, ScriptVO pendingVo) {
        if (pendingVo.getDependScriptVos().isEmpty()) {
            return;
        }
        pendingVo.getDependScriptVos().forEach(d -> {
            if (referenceVo.getKettleName().equals(d.getKettleName())) {
                log.error("检查出了循环依赖问题，{}和{}互相依赖", referenceVo.getKettleName(), d.getKettleName());
                System.exit(-1);
            } else {
                checkLoopDependent(referenceVo, d);
            }
        });
    }

    /**
     * 设置脚本执行的优先级
     * (参考树的遍历方式)
     *
     * @param pendingVo 待处理的vo
     * @return
     */
    public static int setPriority(ScriptVO pendingVo) {
        if (pendingVo.getDependScriptVos().isEmpty()) {
            return 0;
        }
        int maxPriority = 0;
        for (ScriptVO d : pendingVo.getDependScriptVos()) {
            int tempPriority = 1 + setPriority(d);
            if (tempPriority > maxPriority) {
                maxPriority = tempPriority;
            }
        }
        return maxPriority;
    }

    /**
     * 把ScriptVo对象的集合按照不依赖、第一层依赖、第二层依赖...分到不同的list中
     * （形成二维数组。同一层的脚本并行执行；不同层按照优先级的顺序依次执行）
     *
     * @param scriptMap
     * @return
     */
    public static List<List<ScriptVO>> giveScriptVosList(Map<String, String> scriptMap, JobTimeInterval jobTimeInterval) {
        List<ScriptVO> pending = FileUtil.giveScriptVos(scriptMap, jobTimeInterval);
        Map<Integer, List<ScriptVO>> collectMap = pending.stream().collect(Collectors.groupingBy(ScriptVO::getPriority));
        List<List<ScriptVO>> result = new ArrayList<>(collectMap.values());
        log.info("giveScriptVosList处理完毕，最终大小{}", result.size());
        return result;
    }

    /**
     * kettle脚本文件排序
     *
     * @param lists
     * @return
     */
    public static void orderFiles(List<File> lists) {
        lists.sort((o1, o2) -> {
            if (o1.isDirectory() && o2.isFile()) {
                log.debug("一个是目录一个是文件，无法排序");
                return 0;
            }
            if (o1.isFile() && o2.isDirectory()) {
                log.debug("一个是文件一个是目录，无法排序");
                return 0;
            }
            return FileUtil.compareKettleName(o1.getName(), o2.getName());
        });
    }

    /**
     * kettle脚本文件排序
     *
     * @param files
     * @return
     */
    public static List<File> orderFiles(File[] files) {
        List<File> lists = Arrays.asList(files);
        FileUtil.orderFiles(lists);
        return lists;
    }
}
