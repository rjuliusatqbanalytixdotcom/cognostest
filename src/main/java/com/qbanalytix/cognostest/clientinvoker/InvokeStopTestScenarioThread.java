package com.qbanalytix.cognostest.clientinvoker;

import com.ganesha.context.Context;
import com.ganesha.httpclient.BasicHttpClient;
import com.ganesha.httpclient.IHttpClient;
import com.ganesha.httpclient.ServiceParameter;
import com.ganesha.messaging.utils.JsonUtils;
import com.qbanalytix.cognostest.clientinvoker.base.AbstractClientInvokerThread;
import com.qbanalytix.cognostest.clientinvoker.base.IClientInvokerListener;
import com.qbanalytix.cognostest.context.ThreadContext;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.web.ServiceResponse;

public class InvokeStopTestScenarioThread extends AbstractClientInvokerThread {

	public InvokeStopTestScenarioThread(ClientInformation clientInformation,
			IClientInvokerListener clientInvokerListener) {
		super(clientInformation, clientInvokerListener);
	}

	@Override
	public void run() {

		Context context = new ThreadContext(null);
		context.put("clientInformation", getClientInformation());

		IHttpClient httpClient = new BasicHttpClient();
		String response = null;
		try {
			response = httpClient.postAndGetString(composeURL(), (ServiceParameter) null);
		} catch (Exception e) {
			getClientInvokerListener().handleException(context, e);
			return;
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}

		ServiceResponse serviceResponse = null;
		try {
			serviceResponse = new JsonUtils().jsonToObject(response, ServiceResponse.class);
		} catch (Exception e) {
			getClientInvokerListener().handleException(context, response);
			return;
		}

		try {
			getClientInvokerListener().handleResponse(context, serviceResponse);
		} catch (Exception e) {
			getClientInvokerListener().handleException(context, e);
			return;
		}
	}

	@Override
	public String getURL() {
		return "/StopTestScenariosSvc/execute";
	}
}
