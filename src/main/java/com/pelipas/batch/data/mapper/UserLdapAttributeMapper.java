/**
 * Genaro Pelipas (c) 2020
 */
package com.pelipas.batch.data.mapper;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import com.pelipas.batch.data.model.User;

/**
 * [desc]
 * 
 * @author gpelipas
 *
 */
public class UserLdapAttributeMapper implements AttributesMapper<User> {

	@Override
	public User mapFromAttributes(Attributes attribs) throws NamingException {
		User user = new User();

		user.setFirstName(getSafeAttrValue(attribs, "fname", null));
		user.setLastName(getSafeAttrValue(attribs, "lname", null));
		user.setEmail(getSafeAttrValue(attribs, "email", "test@gmail.com"));

		return user;
	}

	private String getSafeAttrValue(Attributes attribs, String key, String defaultValue) {

		try {
			Attribute attr = attribs.get(key);

			if (attr != null) {
				return String.valueOf(attr.get());
			}
		} catch (Exception e) {
		}

		return defaultValue;
	}

}
