package com.qbanalytix.cognostest.context;

import com.ganesha.bean.BeanFactoryManager;
import com.ganesha.context.AbstractApplicationContext;

public class ApplicationContext extends AbstractApplicationContext {

	private static ApplicationContext instance = new ApplicationContext();

	public static ApplicationContext getInstance() {
		return instance;
	}

	public static Object getService(String serviceName) {
		return BeanFactoryManager.getBeanFromConfig(serviceName, "services-context.xml");
	}

	public static Object getBean(String beanId) {
		return BeanFactoryManager.getBeanFromConfig(beanId, "application-context.xml");
	}

	private ApplicationContext() {
		/*
		 * Do nothing
		 */
	}
}
