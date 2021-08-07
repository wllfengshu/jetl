package com.wllfengshu.jetl.configs;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * flyway配置
 *
 * @author wangll
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FlywayConfig {

	@NonNull
	private DataSource dataSource;
	@NonNull
    private EnvConfig envConfig;

	@Bean
    public void migrate(){
        if (System.getenv().containsKey("JAR_Name")) {
			String currentVersion = null;
			try {
				FluentConfiguration configuration = new FluentConfiguration();
				configuration.dataSource(dataSource).table(envConfig.getFlywaySchemaTableName()).validateOnMigrate(true);
				Flyway flyway = new Flyway(configuration);
				currentVersion = flyway.info().current().getVersion().getVersion();
				log.info("检查当前数据库脚本版本: {}", currentVersion);
			} catch (Exception e) {
				log.error("检查数据库脚本失败,{} ,系统将退出！", e.getMessage());
				System.exit(-1);
			}
			if (currentVersion != null && currentVersion.compareTo(envConfig.getFlywaySchemaVersion()) < 0) {
				log.error("检查当前数据库脚本版本过低,{} --> {} ,系统将退出！", currentVersion, envConfig.getFlywaySchemaVersion());
				System.exit(-1);
			}
		}else {
			log.info("当前为本地环境，不启动flyway校验版本");
		}
    }
}
