package com.qbanalytix.cognostest.resources.model;

import java.io.Serializable;
import java.util.List;

public class CognosInformation implements Serializable {

	private static final long serialVersionUID = 1L;

	private String webclient;
	private Integer numberOfThread;
	private String cognosURL;
	private String cognosUsername;
	private String cognosPassword;
	private List<String> cognosReportURLs;
	private Integer cognosReportTestCounter;
	private String cognosLogoutURL;

	public String getWebclient() {
		return webclient;
	}

	public void setWebclient(String webclient) {
		this.webclient = webclient;
	}

	public Integer getNumberOfThread() {
		return numberOfThread;
	}

	public void setNumberOfThread(Integer numberOfThread) {
		this.numberOfThread = numberOfThread;
	}

	public String getCognosURL() {
		return cognosURL;
	}

	public void setCognosURL(String cognosURL) {
		this.cognosURL = cognosURL;
	}

	public String getCognosUsername() {
		return cognosUsername;
	}

	public void setCognosUsername(String cognosUsername) {
		this.cognosUsername = cognosUsername;
	}

	public String getCognosPassword() {
		return cognosPassword;
	}

	public void setCognosPassword(String cognosPassword) {
		this.cognosPassword = cognosPassword;
	}

	public List<String> getCognosReportURLs() {
		return cognosReportURLs;
	}

	public void setCognosReportURLs(List<String> cognosReportURLs) {
		this.cognosReportURLs = cognosReportURLs;
	}

	public Integer getCognosReportTestCounter() {
		return cognosReportTestCounter;
	}

	public void setCognosReportTestCounter(Integer cognosReportTestCounter) {
		this.cognosReportTestCounter = cognosReportTestCounter;
	}

	public String getCognosLogoutURL() {
		return cognosLogoutURL;
	}

	public void setCognosLogoutURL(String cognosLogoutURL) {
		this.cognosLogoutURL = cognosLogoutURL;
	}
}