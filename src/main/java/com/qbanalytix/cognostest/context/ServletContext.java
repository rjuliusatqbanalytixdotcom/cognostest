package com.qbanalytix.cognostest.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ganesha.context.AbstractContext;
import com.ganesha.context.Context;

public class ServletContext extends AbstractContext {

	private javax.servlet.ServletContext servletContext;

	public static ServletContext create(javax.servlet.ServletContext servletContext) {
		return new ServletContext(servletContext);
	}

	public static ServletContext getContext(HttpServletRequest request) {
		return getContext(request.getSession());
	}

	public static ServletContext getContext(HttpSession httpSession) {
		return getContext(httpSession.getServletContext());
	}

	public static ServletContext getContext(javax.servlet.ServletContext servletContext) {
		return (ServletContext) servletContext.getAttribute(SERVLET_CONTEXT);
	}

	public void destroy() {
		clear();
		servletContext = null;
	}

	private ServletContext(javax.servlet.ServletContext servletContext) {
		this.servletContext = servletContext;
		this.servletContext.setAttribute(SERVLET_CONTEXT, this);
	}

	@Override
	public Context getParent() {
		return ApplicationContext.getInstance();
	}

	@Override
	public Object get(String key) {
		Object value = servletContext.getAttribute(key);
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
		Object value = servletContext.getAttribute(key);
		return value == null ? defaultValue : value;
	}

	@Override
	public Object put(String key, Object value) {
		if (value == null) {
			throw new NullPointerException("Cannot put null value into context");
		}
		servletContext.setAttribute(key, value);
		return value;
	}

	@Override
	public int size() {
		int size = 0;
		Enumeration<?> attrNames = servletContext.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			++size;
		}
		return size;
	}

	@Override
	public void clear() {
		List<String> keys = (List<String>) getKeys();
		for (String key : keys) {
			servletContext.removeAttribute(key);
		}
	}

	@Override
	public boolean containsKey(String key) {
		return servletContext.getAttribute(key) != null;
	}

	@Override
	public Collection<String> getKeys() {
		Enumeration<?> attrNames = servletContext.getAttributeNames();
		List<String> keys = new ArrayList<>();
		while (attrNames.hasMoreElements()) {
			String attrName = (String) attrNames.nextElement();
			keys.add(attrName);
		}
		return keys;
	}

	@Override
	public void remove(String key) {
		servletContext.removeAttribute(key);
	}
}
