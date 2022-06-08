/**
 * Genaro Pelipas (c) 2020
 */
package com.gpelipas.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * [desc]
 * 
 * @author gpelipas
 *
 */
@Configuration
public class GmpTestJobsConfig {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Autowired
	private Environment env;

//	@Bean
//	public Job gmpTestJob1() {
//		return jobs.get("gmpTestJob1").incrementer(new RunIdIncrementer()).start(gmpTestStep1())
//	}
//	
//	
//	private Step gmpTestStep1() {
//		return steps.get("gmpTestStep1").
//	}
}
