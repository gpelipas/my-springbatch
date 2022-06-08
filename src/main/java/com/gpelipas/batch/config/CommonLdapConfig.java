/**
 * Genaro Pelipas (c) 2020
 */
package com.gpelipas.batch.config;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * [desc]
 * 
 * @author gpelipas
 *
 */
@Configuration
public class CommonLdapConfig {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Environment env;

	@Bean
	public LdapTemplate ldapTemplate() {
		LdapTemplate ldapTemplate = new LdapTemplate();
		ldapTemplate.setContextSource(contextSource());

		return ldapTemplate;
	}

	private ContextSource contextSource() {
		LdapContextSource ldapCtx = new LdapContextSource();

		final Map<String, Object> baseEnvProps = new HashMap<String, Object>();
		baseEnvProps.put(Context.SECURITY_PROTOCOL, "none");
		baseEnvProps.put(Context.SECURITY_AUTHENTICATION, "simple");
		baseEnvProps.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

		final String skipLdapSslCheck = env.getProperty("skip.ldapssl.check");
		final String ldapUrl = env.getProperty("gmp.ldal.url");

		if (StringUtils.contains(ldapUrl, "ldaps") && "true".equalsIgnoreCase(skipLdapSslCheck)) {
			baseEnvProps.put("java.naming.ldap.factory.socket", "com.pelipas.batch.util.LdapDummySocketFactory");
		}

		ldapCtx.setAnonymousReadOnly(false);
		ldapCtx.setPooled(true);
		ldapCtx.setUrl(ldapUrl);
		ldapCtx.setBase(env.getProperty("gmp.ldap.baseDn"));
		ldapCtx.setUserDn(env.getProperty("gmp.ldap.userDn"));
		ldapCtx.setPassword(env.getProperty("gmp.ldap.password"));

		ldapCtx.setBaseEnvironmentProperties(baseEnvProps);
		ldapCtx.afterPropertiesSet();

		logger.info("ldapPath=" + ldapUrl);
		logger.info("ldapName=" + ldapCtx.getBaseLdapName());

		return ldapCtx;
	}

}
