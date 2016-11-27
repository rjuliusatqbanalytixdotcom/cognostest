package com.qbanalytix.cognostest.resources.model;

public class ClientInformation {

	private String hostname;
	private String ipAddress;
	private Integer port;
	private String cognosUsername;
	private String cognosPassword;
	private Integer numberOfThread;
	private Integer cognosReportTestCounter;

	public String getIdentifier() {
		return new StringBuilder(ipAddress).append(":").append(port).append(" [").append(hostname).append("]")
				.toString();
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
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

	public Integer getNumberOfThread() {
		return numberOfThread;
	}

	public void setNumberOfThread(Integer numberOfThread) {
		this.numberOfThread = numberOfThread;
	}

	public Integer getCognosReportTestCounter() {
		return cognosReportTestCounter;
	}

	public void setCognosReportTestCounter(Integer cognosReportTestCounter) {
		this.cognosReportTestCounter = cognosReportTestCounter;
	}
}