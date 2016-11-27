package com.qbanalytix.cognostest.business.dao.interfaces;

import java.util.List;

import com.ganesha.context.Context;
import com.ganesha.core.exception.UserException;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.resources.model.ClientReport;
import com.qbanalytix.cognostest.resources.model.CognosInformation;

public interface IGlobalDao {

	public void addClientInformation(Context context) throws UserException;

	public List<String> getClientIdentifiers(Context context) throws UserException;
	
	public ClientInformation getClient(Context context) throws UserException;

	public void removeClient(Context context) throws UserException;

	public void saveCognosConfiguration(Context context) throws UserException;

	public CognosInformation loadCognosConfiguraton(Context context) throws UserException;

	public void addClientReport(Context context) throws UserException;

	public List<ClientReport> getClientReports(Context context) throws UserException;

	public void clearClientReport(Context context) throws UserException;
}
