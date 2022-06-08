/**
 * Genaro Pelipas (c) 2020
 */
package com.gpelipas.batch.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * [desc]
 * 
 * @author gpelipas
 *
 */
@EnableTransactionManagement
@Configuration
@EnableBatchProcessing(modular = true)
@Import({ BaseJobConfig.class, CommonDBConfig.class, CommonLdapConfig.class, GmpTestJobsConfig.class })
public class MainAppBatchConfig extends DefaultBatchConfigurer {

	@Autowired
	private PropertySourcesPlaceholderConfigurer configurer;

	@Autowired
	private ConfigurableEnvironment configEnv;

	@Autowired
	@Qualifier("gmpTransactionManager")
	private PlatformTransactionManager gmpTransactionManager;

	@Autowired
	@Override
	public void setDataSource(@Qualifier("gmpDataSource") DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	public PlatformTransactionManager getTransactionManager() {
		return gmpTransactionManager;
	}

	@PostConstruct
	public void initPlaceholderConfigurer() {
		configEnv.setIgnoreUnresolvableNestedPlaceholders(true);
		configurer.setIgnoreUnresolvablePlaceholders(true);
	}

}
