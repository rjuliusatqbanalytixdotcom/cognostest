package com.qbanalytix.cognostest.web;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ganesha.context.Context;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.interfaces.ILogger;
import com.ganesha.messaging.utils.JsonUtils;
import com.google.gson.JsonSyntaxException;
import com.qbanalytix.cognostest.common.FieldExclusionStrategy;
import com.qbanalytix.cognostest.resources.constants.Enums.ResponseStatus;

public abstract class AbstractRequestHandler implements ILogger {

	public static final String RESPONSE_CODE_APP_EXCEPTION = "2";
	public static final String RESPONSE_CODE_USER_EXCEPTION = "1";

	protected final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	protected JsonUtils jsonUtils;

	public AbstractRequestHandler() {
		jsonUtils = createJsonUtils();
	}

	public void executeMethod(String methodName, HttpServletRequest request, HttpServletResponse response,
			Context context) {

		try {
			requestToContext(request, context);
		} catch (Exception e) {
			sendAppExceptionResponse(e, response);
			return;
		}

		Class<? extends AbstractRequestHandler> clazz = getClass();
		// Session session = HibernateUtils.getCurrentSession();
		try {

			// session.beginTransaction();

			Method method = clazz.getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class,
					Context.class);

			method.invoke(this, request, response, context);

			// session.getTransaction().commit();

		} catch (InvocationTargetException e) {
			// session.getTransaction().rollback();
			Throwable throwable = (Throwable) e.getTargetException();
			if (throwable instanceof UserException) {
				sendUserExceptionResponse(throwable.getMessage(), response);
			} else if (throwable instanceof AppException) {
				sendAppExceptionResponse(getRootCause(throwable), response);
			} else {
				sendAppExceptionResponse(throwable, response);
			}
		} catch (RuntimeException | NoSuchMethodException | IllegalAccessException e) {
			// session.getTransaction().rollback();
			sendAppExceptionResponse(getRootCause(e), response);
		}
	}

	protected void sendSuccessResponse(Object responseDetail, HttpServletResponse response) {
		try {
			ServiceResponse serviceResponse = new ServiceResponse();
			serviceResponse.setResponseStatus(ResponseStatus.SUCCESS);
			serviceResponse.setResponseCode("0");
			serviceResponse.setResponseDetail(responseDetail);
			response.setContentType("application/json");
			jsonUtils.writeToStream(serviceResponse, response.getOutputStream());
		} catch (IOException e) {
			throw new AppException(e);
		}
	}

	protected void sendAppExceptionResponse(Throwable throwable, HttpServletResponse response) {
		try {
			logger.error(throwable.getMessage(), throwable);
			ServiceResponse serviceResponse = new ServiceResponse();
			serviceResponse.setResponseStatus(ResponseStatus.FAILED);
			serviceResponse.setResponseCode(RESPONSE_CODE_APP_EXCEPTION);
			serviceResponse.setResponseDetail(throwable.toString());
			response.setContentType("application/json");
			jsonUtils.writeToStream(serviceResponse, response.getOutputStream());
		} catch (IOException e) {
			throw new AppException(e);
		}
	}

	protected void sendUserExceptionResponse(String errorMessage, HttpServletResponse response) {
		try {
			ServiceResponse serviceResponse = new ServiceResponse();
			serviceResponse.setResponseStatus(ResponseStatus.FAILED);
			serviceResponse.setResponseCode(RESPONSE_CODE_USER_EXCEPTION);
			serviceResponse.setResponseDetail(errorMessage);
			response.setContentType("application/json");
			jsonUtils.writeToStream(serviceResponse, response.getOutputStream());
		} catch (IOException e) {
			throw new AppException(e);
		}
	}

	private JsonUtils createJsonUtils() {
		JsonUtils jsonUtils = new JsonUtils();
		jsonUtils.setExclusionStrategy(new FieldExclusionStrategy("children"));
		return jsonUtils;
	}

	private void requestToContext(HttpServletRequest request, Context context) throws IOException {
		context.clear();
		requestStreamToContext(request, context);
		requestParametersToContext(request, context);
	}

	private void requestStreamToContext(HttpServletRequest request, Context context) throws IOException {
		try {
			/*
			 * Lets assume the content format is json. If it is not json then do
			 * nothing, no need to throw any error
			 */
			@SuppressWarnings("unchecked")
			Map<String, Object> map = jsonUtils.readFromStream(request.getInputStream(),
					new HashMap<String, Object>().getClass());
			if (map != null) {
				Iterator<String> iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					Object value = map.get(key);
					context.put(key, value);
				}
			}
		} catch (JsonSyntaxException e) {
			/*
			 * Do nothing
			 */
		}
	}

	private void requestParametersToContext(HttpServletRequest request, Context context) {
		Enumeration<?> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = request.getParameter(key);
			context.put(key, value);
		}
	}

	private Throwable getRootCause(Throwable throwable) {
		if (throwable.getCause() != null) {
			return getRootCause(throwable.getCause());
		} else {
			return throwable;
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
