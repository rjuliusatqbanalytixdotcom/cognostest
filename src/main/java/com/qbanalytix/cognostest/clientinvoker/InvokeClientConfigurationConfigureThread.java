package com.qbanalytix.cognostest.clientinvoker;

import com.ganesha.context.Context;
import com.ganesha.httpclient.BasicHttpClient;
import com.ganesha.httpclient.IHttpClient;
import com.ganesha.messaging.utils.JsonUtils;
import com.qbanalytix.cognostest.clientinvoker.base.AbstractClientInvokerThread;
import com.qbanalytix.cognostest.clientinvoker.base.IClientInvokerListener;
import com.qbanalytix.cognostest.context.ThreadContext;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.resources.model.CognosInformation;
import com.qbanalytix.cognostest.web.ServiceResponse;

public class InvokeClientConfigurationConfigureThread extends AbstractClientInvokerThread {

	private CognosInformation cognosInformation;

	public InvokeClientConfigurationConfigureThread(CognosInformation cognosInformation,
			ClientInformation clientInformation, IClientInvokerListener clientInvokerListener) {
		super(clientInformation, clientInvokerListener);
		this.cognosInformation = cognosInformation;
	}

	@Override
	public void run() {

		Context context = new ThreadContext(null);
		context.put("clientInformation", getClientInformation());

		IHttpClient httpClient = new BasicHttpClient();
		String response = null;
		try {
			String jsonString = new JsonUtils().objectToJson(cognosInformation);
			response = httpClient.postAndGetString(composeURL(), jsonString.getBytes());
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
		return "/ClientConfigurationSvc/configure";
	}
}
