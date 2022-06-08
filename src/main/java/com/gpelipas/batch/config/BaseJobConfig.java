/**
 * Genaro Pelipas (c) 2020
 */
package com.gpelipas.batch.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.pelipas.batch.util.CipherUtil;
import com.pelipas.batch.util.CipherUtil.Vault;

/**
 * [desc]
 * 
 * @author gpelipas
 *
 */
@Configuration
public class BaseJobConfig {

	private CipherUtil cipher = CipherUtil.createInstance();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Environment env;

	@Profile("local")
	@Bean
	public String localProps() {
		return "local";
	}

	@Profile("dev")
	@Bean
	public String devProps() {
		return "dev";
	}

	@Profile("prod")
	@Bean
	public String prodProps() {
		return "prod";
	}

	@Bean
	public Vault dbSourceVault() {
		return new Vault(cipher, "_W1nt3r@I$C0miNg");
	}

	@Bean
	public Vault emailVault() {
		return new Vault(cipher, "@ma!lM0B4t0_");
	}

	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(env.getProperty("gmp.emailer.smtp.host"));
		mailSender.setPort(env.getProperty("gmp.emailer.smtp.port", Integer.class));
		mailSender.setUsername(env.getProperty("gmp.emailer.smtp.userId"));
		mailSender.setPassword(emailVault().get(env.getProperty("gmp.emailer.smtp.password")));

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.debug", "true");

		return mailSender;
	}

}
