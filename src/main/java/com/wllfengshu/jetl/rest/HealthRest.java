package com.wllfengshu.jetl.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangll
 */
@Slf4j
@Api(tags = "健康管理")
@RestController
@RequestMapping("/etl/2.0/health")
@RequiredArgsConstructor
public class HealthRest {

    @ApiOperation(value = "aliveness", httpMethod = "GET", notes = "可用")
    @GetMapping(value = "/aliveness")
    public ResponseEntity aliveness() {
        log.debug("aliveness");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "readiness", httpMethod = "GET", notes = "可用")
    @GetMapping(value = "/readiness")
    public ResponseEntity readiness() {
        log.debug("readiness");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
