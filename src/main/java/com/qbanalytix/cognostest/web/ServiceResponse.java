package com.qbanalytix.cognostest.web;

import java.io.Serializable;

import com.qbanalytix.cognostest.resources.constants.Enums.ResponseStatus;

public class ServiceResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private ResponseStatus responseStatus;
	private String responseCode;
	private Object responseDetail;

	public ResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(ResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public Object getResponseDetail() {
		return responseDetail;
	}

	public void setResponseDetail(Object responseDetail) {
		this.responseDetail = responseDetail;
	}
}