package com.qbanalytix.cognostest.business.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListModel;

import com.ganesha.context.Context;
import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.ComboBoxObject;
import com.qbanalytix.cognostest.application.ProgressStatus;
import com.qbanalytix.cognostest.business.dao.interfaces.IGlobalDao;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.resources.model.ClientReport;
import com.qbanalytix.cognostest.resources.model.CognosInformation;
import com.qbanalytix.cognostest.ui.forms.clientinvokerlistener.ListClientIdentifier;

public class GlobalDaoImpl implements IGlobalDao {

	private static final List<ClientReport> clientReports = new ArrayList<>();

	@Override
	public void addClientInformation(Context context) throws UserException {
		ClientInformation clientInformation = (ClientInformation) context.get("clientInformation");
		DefaultListModel<ComboBoxObject> listModel = (DefaultListModel<ComboBoxObject>) ListClientIdentifier
				.getInstance().getModel();
		listModel.addElement(new ComboBoxObject(clientInformation, clientInformation.getIdentifier()));
	}

	@Override
	public List<String> getClientIdentifiers(Context context) throws UserException {
		List<String> clientIdentifiers = new ArrayList<>();
		DefaultListModel<ComboBoxObject> listModel = (DefaultListModel<ComboBoxObject>) ListClientIdentifier
				.getInstance().getModel();
		for (int i = 0; i < listModel.getSize(); ++i) {
			clientIdentifiers.add(listModel.get(i).getText());
		}
		return clientIdentifiers;
	}

	@Override
	public ClientInformation getClient(Context context) throws UserException {
		String clientIndentifier = context.getString("clientIdentifier");
		DefaultListModel<ComboBoxObject> listModel = (DefaultListModel<ComboBoxObject>) ListClientIdentifier
				.getInstance().getModel();
		for (int i = 0; i < listModel.getSize(); ++i) {
			if (clientIndentifier.equals(listModel.getElementAt(i).getText())) {
				return (ClientInformation) listModel.get(i).getObject();
			}
		}
		return null;
	}

	@Override
	public void removeClient(Context context) throws UserException {
		String clientIndentifier = context.getString("clientIdentifier");
		DefaultListModel<ComboBoxObject> listModel = (DefaultListModel<ComboBoxObject>) ListClientIdentifier
				.getInstance().getModel();
		int targetIndex = -1;
		for (int i = 0; i < listModel.getSize(); ++i) {
			if (clientIndentifier.equals(listModel.getElementAt(i).getText())) {
				targetIndex = i;
				break;
			}
		}
		if (targetIndex >= 0) {
			listModel.remove(targetIndex);
		}
	}

	@Override
	public void saveCognosConfiguration(Context context) throws UserException {
		CognosInformation cognosInformation = (CognosInformation) context.get("cognosInformation");
		SystemSetting.save("cognosInformation", cognosInformation);
	}

	@Override
	public CognosInformation loadCognosConfiguraton(Context context) throws UserException {
		return (CognosInformation) SystemSetting.get("cognosInformation");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addClientReport(Context context) throws UserException {
		clientReports.addAll((Collection<? extends ClientReport>) context.get("clientReports"));
	}

	@Override
	public List<ClientReport> getClientReports(Context context) throws UserException {
		return new ArrayList<>(clientReports);
	}

	@Override
	public void clearClientReport(Context context) throws UserException {
		clientReports.clear();
	}

	@Override
	public void updateClientStatus(Context context) throws UserException {
		ClientInformation clientInformation = (ClientInformation) context.get("clientInformation");
		ProgressStatus.increaseLoop(clientInformation.getIdentifier(), context.getString("threadId"),
				Long.valueOf(context.getString("timeConsumed")));
	}
}
