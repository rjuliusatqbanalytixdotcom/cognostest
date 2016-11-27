package com.qbanalytix.cognostest.resources.model;

public class ClientReport {

	private ClientInformation clientInformation;
	private long threadId;
	private String url;
	private long startTime;
	private long endTime;

	public ClientInformation getClientInformation() {
		return clientInformation;
	}

	public void setClientInformation(ClientInformation clientInformation) {
		this.clientInformation = clientInformation;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
