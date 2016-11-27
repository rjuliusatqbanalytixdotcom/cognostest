package com.qbanalytix.cognostest.ui.forms.clientinvokerlistener;

import com.ganesha.context.Context;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.XJList;
import com.qbanalytix.cognostest.business.dao.DaoCollection;
import com.qbanalytix.cognostest.clientinvoker.base.IClientInvokerListener;
import com.qbanalytix.cognostest.context.ApplicationContext;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.web.ServiceResponse;

public class ListClientIdentifier extends XJList implements IClientInvokerListener {

	private static final long serialVersionUID = 1L;

	private static final ListClientIdentifier instance = new ListClientIdentifier();

	private DaoCollection daoCollection = (DaoCollection) ApplicationContext.getBean("daoCollection");

	public static ListClientIdentifier getInstance() {
		return instance;
	}

	private ListClientIdentifier() {
		/*
		 * Do nothing
		 */
	}

	@Override
	public void handleResponse(Context context, ServiceResponse serviceResponse) {
		/*
		 * Do nothing
		 */
	}

	@Override
	public void handleException(Context context, Exception e) {
		context.put("clientIdentifier", ((ClientInformation) context.get("clientInformation")).getIdentifier());
		try {
			daoCollection.getGlobalDao().removeClient(context);
		} catch (UserException ex) {
			throw new AppException(ex.getCause());
		}
	}

	@Override
	public void handleException(Context context, String response) {
		context.put("clientIdentifier", ((ClientInformation) context.get("clientInformation")).getIdentifier());
		try {
			daoCollection.getGlobalDao().removeClient(context);
		} catch (UserException ex) {
			throw new AppException(ex.getCause());
		}
	}
}
