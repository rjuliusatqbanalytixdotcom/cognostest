package com.qbanalytix.cognostest.clientinvoker.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ganesha.core.interfaces.ILogger;
import com.qbanalytix.cognostest.resources.model.ClientInformation;

public abstract class AbstractClientInvokerThread extends Thread implements ILogger {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private ClientInformation clientInformation;
	private IClientInvokerListener clientInvokerListener;

	public AbstractClientInvokerThread(ClientInformation clientInformation,
			IClientInvokerListener clientInvokerListener) {
		this.clientInformation = clientInformation;
		this.clientInvokerListener = clientInvokerListener;
	}

	public abstract String getURL();

	public String composeURL() {
		String url = getURL();
		StringBuilder builder = new StringBuilder("http://");
		builder.append(getClientInformation().getIpAddress()).append(":").append(getClientInformation().getPort());
		if (url.startsWith("/")) {
			builder.append(url);
		} else {
			builder.append("/").append(url);
		}
		return builder.toString();
	}

	public ClientInformation getClientInformation() {
		return clientInformation;
	}

	public IClientInvokerListener getClientInvokerListener() {
		return clientInvokerListener;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
