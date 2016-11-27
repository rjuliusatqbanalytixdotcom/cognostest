package com.qbanalytix.cognostest.clientinvoker.base;

import com.ganesha.context.Context;
import com.qbanalytix.cognostest.web.ServiceResponse;

public interface IClientInvokerListener {

	public void handleResponse(Context context, ServiceResponse serviceResponse);

	public void handleException(Context context, Exception e);

	public void handleException(Context context, String response);

}
