package com.qbanalytix.cognostest.ui;

import org.hibernate.Session;

import com.ganesha.hibernate.HibernateUtils;

public abstract class SessionAction extends Thread {

	@Override
	public void run() {
		Session session = HibernateUtils.getCurrentSession();
		try {
			session.beginTransaction();
			execute();
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw new RuntimeException(e);
		} finally {
			// session.close();
		}
	}

	public abstract void execute() throws Exception;
}
