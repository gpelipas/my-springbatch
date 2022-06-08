/**
 * Genaro Pelipas (c) 2020
 */
package com.gpelipas.batch.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.pelipas.batch.util.CipherUtil.Vault;

/**
 * [desc]   
 * 
 * @author gpelipas
 *
 */
@Configuration
@ImportResource("classpath:/AppSqlQueries.xml")
public class CommonDBConfig {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private Environment env;
	
	@Autowired
	private Vault dbSourceVault;
	
	@Autowired
	@Qualifier("gmpQueries")
	private HashMap<String, String> gmpQueries;
	
	@Bean(name="gmpDataSource")
	@Primary
	public DataSource gmpDataSoruce() {
		String cls = env.getProperty("gmp.dbsource.driver-class-name");
		String uid = env.getProperty("gmp.dbsource.username");
		String pwd = env.getProperty("gmp.dbsource.password");
		String url = env.getProperty("gmp.dbsource.url");
		
		return DataSourceBuilder.create().driverClassName(cls).url(url).username(uid).password(dbSourceVault.get(pwd)).build();
	}
	
	public JdbcTemplate jdbcTemplate(@Qualifier("gmpDataSource") DataSource ds) {
		return new JdbcTemplate(ds);
	}
	
	@Bean(name="gmpTransactionManager")
	public PlatformTransactionManager gmpTransactionManager() {
		return new ResourcelessTransactionManager();
	}
	
}
