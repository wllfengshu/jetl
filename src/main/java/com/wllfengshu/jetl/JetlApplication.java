package com.wllfengshu.jetl;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * springboot启动类
 *
 * @author
 */
@SpringBootApplication
@EnableSwagger2
@EnableAspectJAutoProxy
@MapperScan(value = "com.wllfengshu.jetl.dao")
public class JetlApplication {

    public static void main(String[] args) {
        SpringApplication.run(JetlApplication.class, args);
    }

}

