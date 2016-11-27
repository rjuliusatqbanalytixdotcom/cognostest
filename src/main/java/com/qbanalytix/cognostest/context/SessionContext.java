package com.qbanalytix.cognostest.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ganesha.context.AbstractContext;
import com.ganesha.context.Context;
import com.ganesha.coreapps.common.UserSession;
import com.qbanalytix.cognostest.web.Constants;

public class SessionContext extends AbstractContext {

	private HttpSession httpSession;

	public static SessionContext create(HttpSession httpSession) {
		return new SessionContext(httpSession);
	}

	public static SessionContext getContext(HttpServletRequest request) {
		return getContext(request.getSession());
	}

	public static SessionContext getContext(HttpSession httpSession) {
		return (SessionContext) httpSession.getAttribute(SESSION_CONTEXT);
	}

	@Override
	public void destroy() {
		ServletContext servletContext = ServletContext.getContext(this.httpSession);
		@SuppressWarnings("unchecked")
		Map<String, HttpSession> sessionContainer = (Map<String, HttpSession>) servletContext
				.get(Constants.SESSION_CONTAINER);
		sessionContainer.remove(this.httpSession.getId());

		clear();
		httpSession = null;
	}

	private SessionContext(HttpSession httpSession) {
		this.httpSession = httpSession;
		this.httpSession.setAttribute(SESSION_CONTEXT, this);

		ServletContext servletContext = ServletContext.getContext(this.httpSession);
		@SuppressWarnings("unchecked")
		Map<String, HttpSession> sessionContainer = (Map<String, HttpSession>) servletContext
				.get(Constants.SESSION_CONTAINER);
		sessionContainer.put(this.httpSession.getId(), httpSession);
	}

	@Override
	public Context getParent() {
		return (Context) httpSession.getAttribute(SERVLET_CONTEXT);
	}

	@Override
	public Object get(String key) {
		Object value = httpSession.getAttribute(key);
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
		Object value = httpSession.getAttribute(key);
		return value == null ? defaultValue : value;
	}

	@Override
	public Object put(String key, Object value) {
		if (value == null) {
			throw new NullPointerException("Cannot put null value into context");
		}
		httpSession.setAttribute(key, value);
		return value;
	}

	@Override
	public int size() {
		int size = 0;
		Enumeration<?> attrNames = httpSession.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			++size;
		}
		return size;
	}

	@Override
	public void clear() {
		List<String> keys = (List<String>) getKeys();
		for (String key : keys) {
			httpSession.removeAttribute(key);
		}
	}

	@Override
	public boolean containsKey(String key) {
		return httpSession.getAttribute(key) != null;
	}

	@Override
	public Collection<String> getKeys() {
		Enumeration<?> attrNames = httpSession.getAttributeNames();
		List<String> keys = new ArrayList<>();
		while (attrNames.hasMoreElements()) {
			String attrName = (String) attrNames.nextElement();
			keys.add(attrName);
		}
		return keys;
	}

	@Override
	public void remove(String key) {
		httpSession.removeAttribute(key);
	}

	public void setUserSession(UserSession userSession) {
		httpSession.setAttribute(USERSESSION, userSession);
	}

	public String getSessionId() {
		return httpSession.getId();
	}
}
