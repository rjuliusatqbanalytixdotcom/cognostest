package com.qbanalytix.cognostest.web.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ganesha.context.Context;
import com.ganesha.core.exception.UserException;
import com.qbanalytix.cognostest.business.dao.DaoCollection;
import com.qbanalytix.cognostest.context.ApplicationContext;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.web.AbstractRequestHandler;

public class ListenerSvc extends AbstractRequestHandler {

	private DaoCollection daoCollection = (DaoCollection) ApplicationContext.getBean("daoCollection");

	public void clientRegister(HttpServletRequest request, HttpServletResponse response, Context context)
			throws UserException {

		String clientIpAddress = request.getRemoteAddr();

		ClientInformation clientInformation = new ClientInformation();
		clientInformation.setHostname(context.getString("hostname"));
		clientInformation.setIpAddress(clientIpAddress);
		clientInformation.setPort(context.getInteger("port"));
		context.put("clientInformation", clientInformation);

		daoCollection.getGlobalDao().addClientInformation(context);
		sendSuccessResponse(null, response);
	}

	public void clientReport(HttpServletRequest request, HttpServletResponse response, Context context)
			throws UserException {
		daoCollection.getGlobalDao().addClientReport(context);
		sendSuccessResponse(null, response);
	}

	public void isReady(HttpServletRequest request, HttpServletResponse response, Context context)
			throws UserException {
		sendSuccessResponse(null, response);
	}

}
