/**
 * Genaro Pelipas (c) 2020
 */
package com.pelipas.batch.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.core.env.Environment;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapUtils;

import com.pelipas.batch.data.model.User;

/**
 * LDAP Item Reader
 * 
 * @author gpelipas
 *
 */
public class LdapItemReader<T> implements ItemReader<T>, ItemStream {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String[] ATTRIBUTES = new String[] { "name", "displayName", "mail" };

	private LdapTemplate ldapTemplate;

	private AttributesMapper<T> attributeMapper;

	private List<T> items;

	private String searchFilter;

	private Environment env;

	/**
	 * @param ldapTemplate
	 * @param attributeMapper
	 * @param searchFilter
	 */
	public LdapItemReader(LdapTemplate ldapTemplate, AttributesMapper<T> attributeMapper, String searchFilter) {
		this.ldapTemplate = ldapTemplate;
		this.attributeMapper = attributeMapper;
		this.searchFilter = searchFilter;
	}

	/**
	 * @param env the env to set
	 */
	public void setEnv(Environment env) {
		this.env = env;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		init();
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
	}

	@Override
	public void close() throws ItemStreamException {
		if (items != null) {
			items.clear();
		}

		items = null;
	}

	@Override
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (items != null && !items.isEmpty()) {
			return items.remove(0);
		}

		return null;
	}

	private void init() {
		items = getResult();

		int count = 0;
		if (items != null) {
			count = items.size();
		}

		logger.info(count + " items found using filter: " + searchFilter);
	}

	private List<T> getResult() {
		PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor(100, null);

		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		sc.setTimeLimit(3000);
		sc.setReturningAttributes(ATTRIBUTES);

		List<T> queryResults = new ArrayList<>();
		List<T> result = null;

		do {
			result = ldapTemplate.search(LdapUtils.emptyLdapName(), searchFilter, sc, attributeMapper, processor);

			queryResults.addAll(result);

			processor = new PagedResultsDirContextProcessor(100, processor.getCookie());

		} while (processor.getCookie().getCookie() != null);

		return queryResults;
	}

	private boolean isItemExistInLdap(String id) {
		Map<String, String> kvMap = new HashMap<>();
		kvMap.put("adEntUserId", id);
		
		String ldapFindUserStr = env.getProperty("gmp.ldap.findUser");
		String searchFilter = replaceStringValues(ldapFindUserStr, kvMap);
	
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		sc.setTimeLimit(3000);
		sc.setReturningAttributes(ATTRIBUTES);
		
		List<T> result = ldapTemplate.search(LdapUtils.emptyLdapName(), searchFilter, sc, attributeMapper);  
		
		int itemFound = 0;
		if (result != null) {
			itemFound = result.size();
		}
		
		return itemFound > 0; 
	}
	
	private static String replaceStringValues(String templateStr, Map<String, String> kvMap) {
		StringSubstitutor sub = new StringSubstitutor(kvMap);
		return sub.replace(templateStr);
	}

}
