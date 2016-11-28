package com.qbanalytix.cognostest.business.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.io.IOUtils;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.ResourceUtils;
import com.qbanalytix.cognostest.business.dao.DaoCollection;
import com.qbanalytix.cognostest.context.ApplicationContext;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.resources.model.ClientReport;

public class DownloadReport {

	private static DaoCollection daoCollection = (DaoCollection) ApplicationContext.getBean("daoCollection");

	private static final File DIR = new File(ResourceUtils.getResourceBase(), "reports");
	private static final String FILE_ALL_DATA = "01_all_data.csv";

	private static final DateFormat MILLES_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

	public static void download() throws UserException {

		OutputStream os = null;
		PrintWriter pw = null;
		try {

			File file = new File(DIR, FILE_ALL_DATA);
			FileUtils.forceMkdir(DIR);

			os = new FileOutputStream(file);
			pw = new PrintWriter(os);

			List<ClientReport> clientReports = daoCollection.getGlobalDao().getClientReports(null);
			
			for (ClientReport clientReport : clientReports) {
				ClientInformation clientInformation = clientReport.getClientInformation();
				String identifier = clientInformation.getIdentifier();
				long threadId = clientReport.getThreadId();
				String url = clientReport.getUrl();
				String startTime = MILLES_FORMAT.format(clientReport.getStartTime());
				String endTime = MILLES_FORMAT.format(clientReport.getEndTime());
				long elapsedTime = clientReport.getEndTime() - clientReport.getStartTime();

				StringBuilder builder = new StringBuilder();

				append(builder, identifier);
				append(builder, threadId);
				append(builder, url);
				append(builder, startTime);
				append(builder, endTime);
				append(builder, elapsedTime);

				pw.println(builder.toString().replaceFirst("|", ""));
			}

		} catch (IOException e) {
			throw new AppException(e);
		} finally {
			IOUtils.closeQuietly(pw);
			IOUtils.closeQuietly(os);
		}

	}

	private static void append(StringBuilder builder, String token) {
		builder.append("|").append("\"").append(token).append("\"");
	}

	private static void append(StringBuilder builder, Number token) {
		builder.append("|").append(token);
	}
}
