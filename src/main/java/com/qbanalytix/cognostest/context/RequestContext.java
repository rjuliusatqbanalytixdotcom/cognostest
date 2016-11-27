package com.qbanalytix.cognostest.context;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import com.ganesha.context.AbstractContext;
import com.ganesha.context.Context;
import com.ganesha.coreapps.common.UserSession;
import com.qbanalytix.cognostest.web.Constants;

public class RequestContext extends AbstractContext {

	private HttpServletRequest request;
	private String authenticationMode;
	private String basicAuthenticationUsername;
	private String basicAuthenticationPassword;

	public static RequestContext create(HttpServletRequest request) {
		return new RequestContext(request);
	}

	public void destroy() {
		clear();
		request = null;
	}

	private RequestContext(HttpServletRequest request) {
		this.request = request;
		extractBasicAuthenticationCredential();
	}

	@Override
	public Context getParent() {
		return (Context) request.getSession().getAttribute(SESSION_CONTEXT);
	}

	@Override
	public Object get(String key) {
		Object value = request.getAttribute(key);
		if (value == null) {
			value = getParent().get(key);
			if (value == null) {
				throw new NullPointerException("Key '" + key + "' is not found in the AbstractApplicationContext");
			}
		}
		return value;
	}

	@Override
	public Object get(String key, Object defaultValue) {
		Object value = request.getAttribute(key);
		return value == null ? defaultValue : value;
	}

	@Override
	public Object put(String key, Object value) {
		if (value == null) {
			throw new NullPointerException("Cannot put null value into context");
		}
		request.setAttribute(key, value);
		return value;
	}

	@Override
	public int size() {
		int size = 0;
		Enumeration<?> attrNames = request.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			++size;
		}
		return size;
	}

	@Override
	public void clear() {
		List<String> keys = (List<String>) getKeys();
		for (String key : keys) {
			request.removeAttribute(key);
		}
	}

	@Override
	public boolean containsKey(String key) {
		return request.getAttribute(key) != null;
	}

	@Override
	public Collection<String> getKeys() {
		Enumeration<?> attrNames = request.getAttributeNames();
		List<String> keys = new ArrayList<>();
		while (attrNames.hasMoreElements()) {
			String attrName = (String) attrNames.nextElement();
			keys.add(attrName);
		}
		return keys;
	}

	@Override
	public void remove(String key) {
		request.removeAttribute(key);
	}

	@Override
	public UserSession getUserSession() {
		return getParent().getUserSession();
	}

	private void extractBasicAuthenticationCredential() {
		String auth = request.getHeader("Authorization");
		if (auth != null) {
			String[] strings = auth.split(" ", 2);
			if (!strings[0].trim().isEmpty()) {
				authenticationMode = strings[0].trim();
			}
			if (authenticationMode.equals(Constants.AUTH_BASIC)) {
				if (!strings[1].trim().isEmpty()) {
					String[] credentials = new String(DatatypeConverter.parseBase64Binary(strings[1].trim()),
							Charset.forName("UTF-8")).split(":", 2);
					if (!credentials[0].trim().isEmpty()) {
						basicAuthenticationUsername = credentials[0].trim();
						if (credentials.length > 1 && !credentials[1].trim().isEmpty()) {
							basicAuthenticationPassword = credentials[1].trim();
						}
					}
				}
			}
		}
	}

	public String getAuthenticationMode() {
		return authenticationMode;
	}

	public String getBasicAuthenticationUsername() {
		return basicAuthenticationUsername;
	}

	public String getBasicAuthenticationPassword() {
		return basicAuthenticationPassword;
	}

}
