package com.qbanalytix.cognostest.quartz;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ganesha.context.Context;
import com.ganesha.core.exception.UserException;
import com.qbanalytix.cognostest.business.dao.DaoCollection;
import com.qbanalytix.cognostest.clientinvoker.InvokeClientConfigurationIsReadyThread;
import com.qbanalytix.cognostest.context.ApplicationContext;
import com.qbanalytix.cognostest.context.ThreadContext;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.ui.forms.clientinvokerlistener.ListClientIdentifier;

public class InvokeClientConfigurationIsReayJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(InvokeClientConfigurationIsReayJob.class);

	private DaoCollection daoCollection = (DaoCollection) ApplicationContext.getBean("daoCollection");

	@Override
	public void execute(JobExecutionContext context) {

		logger.debug("Scheduler " + InvokeClientConfigurationIsReayJob.class.getName() + " is started");

		try {
			start();
		} catch (UserException e) {
			logger.error(e.toString(), e);
		}

		logger.debug("Scheduler " + InvokeClientConfigurationIsReayJob.class.getName() + " is finished");
	}

	private void start() throws UserException {

		List<String> clientIdentifiers = daoCollection.getGlobalDao().getClientIdentifiers(null);

		if (clientIdentifiers.size() > 0) {
			ExecutorService executor = Executors.newFixedThreadPool(clientIdentifiers.size());
			for (String clientIdentifier : clientIdentifiers) {
				Context context = new ThreadContext(ApplicationContext.getInstance());
				context.put("clientIdentifier", clientIdentifier);
				ClientInformation clientInformation = daoCollection.getGlobalDao().getClient(context);
				InvokeClientConfigurationIsReadyThread thread = new InvokeClientConfigurationIsReadyThread(
						clientInformation, ListClientIdentifier.getInstance());
				executor.execute(thread);
			}

			executor.shutdown();
		}
	}
}