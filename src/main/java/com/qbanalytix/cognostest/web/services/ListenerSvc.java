package com.qbanalytix.cognostest.web.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ganesha.context.Context;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.messaging.utils.JsonUtils;
import com.google.common.reflect.TypeToken;
import com.qbanalytix.cognostest.business.dao.DaoCollection;
import com.qbanalytix.cognostest.context.ApplicationContext;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.resources.model.ClientReport;
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

		TypeToken<List<ClientReport>> typeToken = new TypeToken<List<ClientReport>>() {
			private static final long serialVersionUID = 1L;
		};

		List<ClientReport> clientReports = new JsonUtils().jsonToObject(context.getString("clientReports"),
				typeToken.getType());

		context.put("clientReports", clientReports);

		daoCollection.getGlobalDao().addClientReport(context);
		sendSuccessResponse(null, response);
	}

	public void isReady(HttpServletRequest request, HttpServletResponse response, Context context)
			throws UserException {
		sendSuccessResponse(null, response);
	}

	public void clientUpdateStatus(HttpServletRequest request, HttpServletResponse response, Context context)
			throws UserException {

		String clientIpAddress = request.getRemoteAddr();

		ClientInformation clientInformation = new ClientInformation();
		clientInformation.setHostname(context.getString("hostname"));
		clientInformation.setIpAddress(clientIpAddress);
		clientInformation.setPort(Formatter.formatStringToNumber(context.getString("port")).intValue());
		context.put("clientInformation", clientInformation);

		daoCollection.getGlobalDao().updateClientStatus(context);
		sendSuccessResponse(null, response);
	}

}
