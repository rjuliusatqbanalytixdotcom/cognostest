package com.qbanalytix.cognostest.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ganesha.context.Context;
import com.qbanalytix.cognostest.context.ApplicationContext;
import com.qbanalytix.cognostest.context.RequestContext;

public class RequestHandler extends AbstractHandler {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			processRequest(request, response);
		} finally {
	        response.setStatus(HttpServletResponse.SC_OK);
	        baseRequest.setHandled(true);
		}
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Context context = createRequestContext(request);
			processService(request, response, context);
			context.destroy();
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			writeToOutputStream(e.toString(), response.getOutputStream());
		}
	}

	private void processService(HttpServletRequest request, HttpServletResponse response, Context context) {

		String pathInfo = request.getPathInfo();
		String[] token = pathInfo.split("/");

		if (token.length != 3) {
			StringBuilder builder = new StringBuilder("Pattern '").append("' for service call is not supported");
			throw new RuntimeException(builder.toString());
		}

		String beanId = token[1];
		String methodName = token[2];

		AbstractRequestHandler abstractRequestHandler = (AbstractRequestHandler) ApplicationContext.getService(beanId);
		abstractRequestHandler.executeMethod(methodName, request, response, context);
	}

	private void writeToOutputStream(String stringToWrite, OutputStream outputStream) throws IOException {
		byte[] strArr = stringToWrite.getBytes();
		outputStream.write(strArr);
		outputStream.flush();
	}

	private Context createRequestContext(HttpServletRequest request) {
		return RequestContext.create(request);
	}
}