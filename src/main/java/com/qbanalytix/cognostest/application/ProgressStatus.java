package com.qbanalytix.cognostest.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.ganesha.context.Context;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.qbanalytix.cognostest.business.dao.DaoCollection;
import com.qbanalytix.cognostest.clientinvoker.InvokeLaunchTestScenarioThread;
import com.qbanalytix.cognostest.clientinvoker.InvokeStopTestScenarioThread;
import com.qbanalytix.cognostest.clientinvoker.base.IClientInvokerListener;
import com.qbanalytix.cognostest.context.ApplicationContext;
import com.qbanalytix.cognostest.context.ThreadContext;
import com.qbanalytix.cognostest.ui.forms.TestDialog;
import com.qbanalytix.cognostest.web.ServiceResponse;

public class ProgressStatus extends XJTable implements IClientInvokerListener {

	private static final long serialVersionUID = 1L;

	private static DaoCollection daoCollection = (DaoCollection) ApplicationContext.getBean("daoCollection");

	private static final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	static {
		tableParameters.put(ColumnEnum.ROWNUM,
				new XTableParameter(0, 2, false, "No", false, XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParameters.put(ColumnEnum.CLIENT_IDENTIFIER,
				new XTableParameter(1, 50, false, "Client", false, XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.THREAD_ID,
				new XTableParameter(2, 6, false, "Thread", false, XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.LOOP_COUNTER, new XTableParameter(3, 10, false, "Loop Count", false,
				XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParameters.put(ColumnEnum.NUMBER_OF_REPORTS_PER_LOOP, new XTableParameter(4, 20, false,
				"Num of Reports @Loop", false, XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParameters.put(ColumnEnum.TOTAL_REPORTS_LOADED, new XTableParameter(5, 20, false, "Total Reports Loaded",
				false, XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParameters.put(ColumnEnum.TIME_CONSUMED_LONG, new XTableParameter(6, 20, false, "Time Consumed (Long)",
				true, XTableConstants.CELL_RENDERER_CENTER, Long.class));

		tableParameters.put(ColumnEnum.TIME_CONSUMED_STRING, new XTableParameter(7, 12, false, "Time Consumed", false,
				XTableConstants.CELL_RENDERER_CENTER, String.class));
	}

	private static final ProgressStatus instance = new ProgressStatus();

	private TestDialog dialog;
	private boolean running;

	public static ProgressStatus getInstance() {
		return instance;
	}

	private ProgressStatus() {
		XTableUtils.initTable(this, tableParameters);
	}

	public static void setDialog(TestDialog dialog) {
		instance.dialog = dialog;
	}

	public static Object[] getStatus(String clientIdentifier, String threadId) {
		int rowIndex = getRowIndex(clientIdentifier, threadId);
		if (rowIndex < 0) {
			return null;
		} else {
			XTableModel tableModel = (XTableModel) instance.getModel();
			Object[] objects = new Object[tableModel.getColumnCount()];
			for (int j = 0; j < objects.length; ++j) {
				objects[j] = tableModel.getValueAt(getRowIndex(clientIdentifier, threadId), j);
			}
			return objects;
		}
	}

	public static List<Object[]> getAll() {
		List<Object[]> all = new ArrayList<>();
		XTableModel tableModel = (XTableModel) instance.getModel();
		for (int row = 0; row < tableModel.getRowCount(); ++row) {
			Object[] objects = new Object[tableModel.getColumnCount()];
			for (int column = 0; column < tableModel.getColumnCount(); ++column) {
				objects[column] = tableModel.getValueAt(row, column);
			}
			all.add(objects);
		}
		return all;
	}

	public static synchronized void setStatus(String clientIdentifier, String threadId, ColumnEnum columnEnum,
			Object value) {
		int rowIndex = getRowIndex(clientIdentifier, threadId);
		if (rowIndex < 0) {
			throw new AppException(new StringBuilder(clientIdentifier).append(" with thread id ").append(threadId)
					.append(" is not found").toString());
		} else {
			XTableModel tableModel = (XTableModel) instance.getModel();
			tableModel.setValueAt(value, rowIndex, getColumnIndex(columnEnum));
		}
	}

	public static synchronized void addStatus(String clientIdentifier, String threadId, long timeConsumed) {

		Context context = new ThreadContext(null);
		context.put("clientIdentifier", clientIdentifier);

		XTableModel tableModel = (XTableModel) instance.getModel();
		int rowNum = tableModel.getRowCount();

		Object[] objects = new Object[tableModel.getColumnCount()];
		objects[getColumnIndex(ColumnEnum.ROWNUM)] = rowNum;
		objects[getColumnIndex(ColumnEnum.CLIENT_IDENTIFIER)] = clientIdentifier;
		objects[getColumnIndex(ColumnEnum.THREAD_ID)] = threadId;
		objects[getColumnIndex(ColumnEnum.TIME_CONSUMED_LONG)] = timeConsumed;
		objects[getColumnIndex(ColumnEnum.TIME_CONSUMED_STRING)] = "0";

		try {
			objects[getColumnIndex(ColumnEnum.LOOP_COUNTER)] = 0;
			objects[getColumnIndex(ColumnEnum.NUMBER_OF_REPORTS_PER_LOOP)] = daoCollection.getGlobalDao()
					.loadCognosConfiguraton(context).getCognosReportURLs().size();
			objects[getColumnIndex(ColumnEnum.TOTAL_REPORTS_LOADED)] = 0;
		} catch (UserException e) {
			throw new AppException(e.getCause());
		}

		tableModel.addRow(objects);
	}

	public static enum ColumnEnum {
		ROWNUM, CLIENT_IDENTIFIER, THREAD_ID, LOOP_COUNTER, NUMBER_OF_REPORTS_PER_LOOP, TOTAL_REPORTS_LOADED, TIME_CONSUMED_LONG, TIME_CONSUMED_STRING
	}

	public static int getColumnIndex(ColumnEnum columnEnum) {
		return tableParameters.get(columnEnum).getColumnIndex();
	}

	public static synchronized void increaseLoop(String clientIdentifier, String threadId, long timeConsumed) {

		Object[] status = getStatus(clientIdentifier, threadId);

		int rowIndex = getRowIndex(clientIdentifier, threadId);
		if (rowIndex < 0) {
			addStatus(clientIdentifier, threadId, timeConsumed);
		} else {
			XTableModel tableModel = (XTableModel) instance.getModel();

			tableModel.setValueAt(((int) status[getColumnIndex(ColumnEnum.LOOP_COUNTER)]) + 1, rowIndex,
					getColumnIndex(ColumnEnum.LOOP_COUNTER));

			tableModel.setValueAt(
					((int) status[getColumnIndex(ColumnEnum.TOTAL_REPORTS_LOADED)])
							+ ((int) status[getColumnIndex(ColumnEnum.NUMBER_OF_REPORTS_PER_LOOP)]),
					rowIndex, getColumnIndex(ColumnEnum.TOTAL_REPORTS_LOADED));

			tableModel.setValueAt(((long) status[getColumnIndex(ColumnEnum.TIME_CONSUMED_LONG)]) + timeConsumed,
					rowIndex, getColumnIndex(ColumnEnum.TIME_CONSUMED_LONG));

			tableModel.setValueAt(
					Formatter.formatNumberToString(
							((long) status[getColumnIndex(ColumnEnum.TIME_CONSUMED_LONG)]) / 1000.0),
					rowIndex, getColumnIndex(ColumnEnum.TIME_CONSUMED_STRING));
		}
	}

	public static boolean isRunning() {
		return instance.running;
	}

	public static void start() throws UserException {
		XTableModel tableModel = (XTableModel) instance.getModel();
		while (tableModel.getRowCount() > 0) {
			tableModel.removeRow(0);
		}
		instance.running = true;
		triggerRunToClient();
	}

	public static void stop() throws UserException {
		instance.running = false;
		triggerStopToClient();
	}

	public static void exportToFile() {

	}

	private static int getRowIndex(String clientIdentifier, String threadId) {
		XTableModel tableModel = (XTableModel) instance.getModel();
		for (int i = 0; i < tableModel.getRowCount(); ++i) {
			if (clientIdentifier.equals(tableModel.getValueAt(i, getColumnIndex(ColumnEnum.CLIENT_IDENTIFIER)))
					&& threadId.equals(tableModel.getValueAt(i, getColumnIndex(ColumnEnum.THREAD_ID)))) {
				return i;
			}
		}
		return -1;
	}

	private static void triggerRunToClient() throws UserException {

		daoCollection.getGlobalDao().clearClientReport(null);

		List<String> clientIdentifiers = daoCollection.getGlobalDao().getClientIdentifiers(null);
		int numberOfThread = clientIdentifiers.size();
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThread);

		for (int i = 0; i < numberOfThread; ++i) {
			Context context = new ThreadContext(ApplicationContext.getInstance());
			context.put("clientIdentifier", clientIdentifiers.get(i));
			InvokeLaunchTestScenarioThread thread = new InvokeLaunchTestScenarioThread(
					daoCollection.getGlobalDao().getClient(context), instance);
			executor.execute(thread);
		}

		executor.shutdown();

		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			throw new AppException(e);
		}
	}

	private static void triggerStopToClient() throws UserException {

		List<String> clientIdentifiers = daoCollection.getGlobalDao().getClientIdentifiers(null);
		int numberOfThread = clientIdentifiers.size();
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThread);

		for (int i = 0; i < numberOfThread; ++i) {
			Context context = new ThreadContext(ApplicationContext.getInstance());
			context.put("clientIdentifier", clientIdentifiers.get(i));
			InvokeStopTestScenarioThread thread = new InvokeStopTestScenarioThread(
					daoCollection.getGlobalDao().getClient(context), instance);
			executor.execute(thread);
		}

		executor.shutdown();

		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			throw new AppException(e);
		}
	}

	@Override
	public void handleResponse(Context context, ServiceResponse serviceResponse) {
		/*
		 * Do nothing
		 */
	}

	@Override
	public void handleException(Context context, Exception e) {
		try {
			stop();
			ExceptionHandler.handleException(dialog, e);
		} catch (UserException e1) {
			ExceptionHandler.handleException(dialog, e1);
		}

	}

	@Override
	public void handleException(Context context, String response) {
		try {
			stop();
			ExceptionHandler.handleException(dialog, new Exception(response));
		} catch (UserException e) {
			ExceptionHandler.handleException(dialog, e);
		}
	}
}
