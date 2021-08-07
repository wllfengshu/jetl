package com.wllfengshu.jetl.rest;

import com.wllfengshu.jetl.exception.CustomException;
import com.wllfengshu.jetl.model.enumerate.JobTimeInterval;
import com.wllfengshu.jetl.service.EtlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangll
 */
@Slf4j
@Api(tags = "报表聚合")
@RestController
@RequestMapping("/etl/2.0/etl")
@RequiredArgsConstructor
public class EtlRest {

    @NonNull
    private EtlService etlService;

    @ApiOperation(value = "聚合30秒的数据(针对所有30秒的报表)",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "开始时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 01:30:00", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 02:30:00", paramType = "query")
    })
    @GetMapping(value = "/second30")
    public ResponseEntity etlDataForSecond30(@RequestParam(value = "beginTime") String beginTime,
                                             @RequestParam(value = "endTime") String endTime) throws CustomException {
        return new ResponseEntity(etlService.etlDataForSecond30(beginTime,endTime),HttpStatus.OK);
    }

    @ApiOperation(value = "聚合1分钟的数据(针对所有1分钟的报表)",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "开始时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 01:30:00", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 02:30:00", paramType = "query")
    })
    @GetMapping(value = "/minute")
    public ResponseEntity etlDataForMinute(@RequestParam(value = "beginTime") String beginTime,
                                             @RequestParam(value = "endTime") String endTime) throws CustomException {
        return new ResponseEntity(etlService.etlDataForMinute(beginTime,endTime),HttpStatus.OK);
    }

    @ApiOperation(value = "聚合15分钟的数据(针对所有15分钟的报表)",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "开始时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 01:30:00", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 02:30:00", paramType = "query")
    })
    @GetMapping(value = "/minute15")
    public ResponseEntity etlDataForMinute15(@RequestParam(value = "beginTime") String beginTime,
                                             @RequestParam(value = "endTime") String endTime) throws CustomException {
        return new ResponseEntity(etlService.etlDataForMinute15(beginTime,endTime),HttpStatus.OK);
    }

    @ApiOperation(value = "聚合1小时的数据(针对所有时报表)",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "开始时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 01:30:00", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 02:30:00", paramType = "query")
    })
    @GetMapping(value = "/hour")
    public ResponseEntity etlDataForHour(@RequestParam(value = "beginTime") String beginTime,
                                         @RequestParam(value = "endTime") String endTime) throws CustomException {
        return new ResponseEntity(etlService.etlDataForHour(beginTime,endTime),HttpStatus.OK);
    }

    @ApiOperation(value = "聚合1天的数据(针对所有日报表)",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "开始时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 01:30:00", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 02:30:00", paramType = "query")
    })
    @GetMapping(value = "/day")
    public ResponseEntity etlDataForDay(@RequestParam(value = "beginTime") String beginTime,
                                        @RequestParam(value = "endTime") String endTime) throws CustomException {
        return new ResponseEntity(etlService.etlDataForDay(beginTime,endTime),HttpStatus.OK);
    }

    @ApiOperation(value = "聚合1月的数据(针对所有月报表)",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "开始时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 01:30:00", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 02:30:00", paramType = "query")
    })
    @GetMapping(value = "/month")
    public ResponseEntity etlDataForMonth(@RequestParam(value = "beginTime") String beginTime,
                                          @RequestParam(value = "endTime") String endTime) throws CustomException{
        return new ResponseEntity(etlService.etlDataForMonth(beginTime,endTime),HttpStatus.OK);
    }

    @ApiOperation(value = "查看不聚合的kettle脚本名（也可看数据库etl_no_aggregation表）",httpMethod = "GET")
    @GetMapping(value = "/etlNoAggregation")
    public ResponseEntity etlNoAggregation() throws CustomException{
        return new ResponseEntity(etlService.etlNoAggregation(),HttpStatus.OK);
    }

    @ApiOperation(value = "查看所有聚合的kettle脚本名",httpMethod = "GET")
    @GetMapping(value = "/etlAggregation")
    public ResponseEntity etlAggregation() throws CustomException{
        return new ResponseEntity(etlService.etlAggregation(),HttpStatus.OK);
    }

    @ApiOperation(value = "聚合指定的报表",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "开始时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string",example = "2019-09-01 01:30:00", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式:yyyy-MM-dd HH:mm:ss)",required = true, dataType = "string", example = "2019-09-01 02:30:00", paramType = "query"),
            @ApiImplicitParam(name = "kettleName", value = "脚本名称（带后缀）",required = true, dataType = "string", example = "3@agg_agent_detail.ktr", paramType = "query"),
            @ApiImplicitParam(name = "jobTimeInterval", value = "job执行的时间间隔的类型",required = true, dataType = "enum", paramType = "query")
    })
    @GetMapping(value = "/runKettle")
    public ResponseEntity runKettle(@RequestParam(value = "beginTime") String beginTime,
                                    @RequestParam(value = "endTime") String endTime,
                                    @RequestParam(value = "kettleName") String kettleName,
                                    @RequestParam(value = "jobTimeInterval") JobTimeInterval jobTimeInterval) throws CustomException{
        log.info("手动执行kettle脚本：beginTime={}，endTime={},kettleName={},jobTimeInterval={}",beginTime,endTime,kettleName,jobTimeInterval);
        return new ResponseEntity(etlService.runKettle(beginTime,endTime,kettleName,jobTimeInterval),HttpStatus.OK);
    }
}
