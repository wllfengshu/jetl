package com.wllfengshu.jetl.utils;

import com.wllfengshu.jetl.configs.EnvConfig;
import com.wllfengshu.jetl.exception.CustomException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * influxdb工具类
 *
 * @author wangll
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InfluxDbUtil {

    @NonNull
    private InfluxDB influxDB;
    @NonNull
    private EnvConfig envConfig;

    /**
     * 查询
     *
     * @param command 查询语句
     * @param clazz   javaBean
     * @return
     */
    public <T> List<T> query(@NotBlank String command, Class<T> clazz) throws CustomException {
        log.info("InfluxDbUtil query 开始 ---> command:{}", command);
        final List<T> list = new ArrayList<>();
        try {
            if (influxDB.ping() == null) {
                log.error("无法连接到influxDB");
                return list;
            }
            if (command.contains("SELECT  FROM")) {
                log.debug("本次没有选择tag，将直接返回");
                return list;
            }
            QueryResult query = influxDB.query(new Query(command, envConfig.getInfluxdbName()));
            for (QueryResult.Result result : query.getResults()) {
                List<QueryResult.Series> series = result.getSeries();
                if (series == null) {
                    continue;
                }
                for (QueryResult.Series serie : series) {
                    List<List<Object>> values = serie.getValues();
                    List<String> columns = serie.getColumns();
                    Map<String, String> tags = serie.getTags();
                    for (List<Object> value : values) {
                        T object = clazz.newInstance();
                        BeanWrapperImpl bean = new BeanWrapperImpl(object);
                        if (tags != null && !tags.keySet().isEmpty()) {
                            tags.forEach(bean::setPropertyValue);
                        }
                        for (int j = 0; j < columns.size(); ++j) {
                            String k = columns.get(j);
                            Object v = value.get(j);
                            if ("time".equals(k)) {
                                //需要处理五种时间格式：2020-08-01T00:00:00+08:00  2020-08-06T00:00:00.800319164Z  2020-08-06T08:00:26.0906948+08:00  2020-08-13T23:59:00.067Z  2020-08-13T23:59:00Z
                                bean.setPropertyValue(k, StringUtils.substringBefore(StringUtils.substringBefore((String) v, "."), "+")
                                        .replace("T", " ")
                                        .replace("Z", ""));
                            } else {
                                bean.setPropertyValue(k, v);
                            }
                        }
                        list.add(object);
                    }
                }
            }
        } catch (Exception e) {
            log.error("InfluxDbUtil 查询influxDB时发生错误，或未选择查询字段导致查询sql不合法");
            throw new CustomException("InfluxDbUtil 查询influxDB时发生错误，或未选择查询字段导致查询sql不合法", CustomException.ExceptionName.IllegalParam);
        }
        log.info("InfluxDbUtil query 结束，返回数据条数：{}", list.size());
        return list;
    }

    /**
     * 插入
     *
     * @param measurement 表
     * @param tags        标签
     * @param fields      字段
     */
    public void insert(@NotBlank String measurement, Map<String, String> tags, Map<String, Object> fields) {
        log.info("InfluxDbUtil insert 开始 ---> measurement:{}", measurement);
        Point.Builder builder = Point.measurement(measurement).tag(tags).fields(fields);
        influxDB.write(envConfig.getInfluxdbName(), envConfig.getInfluxdbRetentionPolicy(), builder.build());
        log.info("InfluxDbUtil insert 结束");
    }

    /**
     * 插入
     *
     * @param pending 待插入javaBean
     */
    public <T> void insert(@NonNull T pending) {
        log.info("InfluxDbUtil insert 开始");
        Point point = Point.measurementByPOJO(pending.getClass()).addFieldsFromPOJO(pending).build();
        influxDB.write(envConfig.getInfluxdbName(), envConfig.getInfluxdbRetentionPolicy(), point);
        log.info("InfluxDbUtil insert 结束");
    }

    /**
     * 插入
     *
     * @param pendings 待插入javaBean集合
     */
    public <T> void inserts(@NotEmpty List<T> pendings) {
        log.info("InfluxDbUtil insert 开始 ---> pendings.size:{}", pendings.size());
        List<Point> points = new ArrayList<>();
        for (T t : pendings) {
            Point point = Point.measurementByPOJO(t.getClass()).addFieldsFromPOJO(t).build();
            points.add(point);
        }
        BatchPoints batchPoints = BatchPoints.database(envConfig.getInfluxdbName())
                .retentionPolicy(envConfig.getInfluxdbRetentionPolicy())
                .points(points)
                .build();
        influxDB.write(batchPoints);
        log.info("InfluxDbUtil insert 结束");
    }
}
