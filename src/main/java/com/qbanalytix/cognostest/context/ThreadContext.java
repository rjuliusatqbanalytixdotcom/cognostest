package com.qbanalytix.cognostest.context;

import java.util.Collection;

import com.ganesha.context.AbstractContext;
import com.ganesha.context.Context;
import com.ganesha.context.impl.SimpleContextImpl;

public class ThreadContext extends AbstractContext {

	private static final ThreadLocal<SimpleContextImpl> threadLocal = new ThreadLocal<>();

	public ThreadContext(Context parent) {
		threadLocal.set(new SimpleThreadContextImpl(parent));
	}

	@Override
	public Context getParent() {
		return threadLocal.get().getParent();
	}

	@Override
	public Object get(String key) {
		return threadLocal.get().get(key);
	}

	@Override
	public Object get(String key, Object value) {
		return threadLocal.get().get(key, value);
	}

	@Override
	public Object put(String key, Object value) {
		return threadLocal.get().put(key, value);
	}

	@Override
	public int size() {
		return threadLocal.get().size();
	}

	@Override
	public void clear() {
		threadLocal.get().clear();
	}

	@Override
	public boolean containsKey(String key) {
		return threadLocal.get().containsKey(key);
	}

	@Override
	public Collection<String> getKeys() {
		return threadLocal.get().getKeys();
	}

	@Override
	public void remove(String key) {
		threadLocal.get().remove(key);
	}

	@Override
	public void destroy() {
		threadLocal.get().destroy();
	}

	private class SimpleThreadContextImpl extends SimpleContextImpl {
		public SimpleThreadContextImpl(Context parent) {
			this.parent = parent;
		}
	}
}
