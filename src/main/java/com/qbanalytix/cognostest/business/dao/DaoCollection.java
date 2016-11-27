package com.qbanalytix.cognostest.business.dao;

import com.qbanalytix.cognostest.business.dao.interfaces.IGlobalDao;

public class DaoCollection {

	private IGlobalDao globalDao;

	public IGlobalDao getGlobalDao() {
		return globalDao;
	}

	public void setGlobalDao(IGlobalDao globalDao) {
		this.globalDao = globalDao;
	}

}
