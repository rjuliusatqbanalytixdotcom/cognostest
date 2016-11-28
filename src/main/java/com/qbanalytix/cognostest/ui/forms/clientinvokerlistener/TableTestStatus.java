package com.qbanalytix.cognostest.ui.forms.clientinvokerlistener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ganesha.context.Context;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.qbanalytix.cognostest.business.dao.DaoCollection;
import com.qbanalytix.cognostest.clientinvoker.base.IClientInvokerListener;
import com.qbanalytix.cognostest.context.ApplicationContext;
import com.qbanalytix.cognostest.context.ThreadContext;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.ui.forms.TestDialog;
import com.qbanalytix.cognostest.web.ServiceResponse;

public class TableTestStatus extends XJTable implements IClientInvokerListener {

	private static final long serialVersionUID = 1L;
	private static final TableTestStatus instance = new TableTestStatus();

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.CLIENT_IDENTIFIER,
				new XTableParameter(0, 100, false, "Client", false, XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.LOOP_COUNTER, new XTableParameter(1, 10, false, "Loop Count", false,
				XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParameters.put(ColumnEnum.STATUS,
				new XTableParameter(2, 50, false, "Status", false, XTableConstants.CELL_RENDERER_CENTER, String.class));
	}

	private DaoCollection daoCollection = (DaoCollection) ApplicationContext.getBean("daoCollection");
	private TestDialog dialog;

	public static TableTestStatus getInstance() {
		return instance;
	}

	private TableTestStatus() {
		XTableUtils.initTable(this, tableParameters);
	}

	public void setDialog(TestDialog dialog) {
		this.dialog = dialog;
	}

	public boolean isRunning() throws UserException {

		boolean running = false;

		try {
			Context context = new ThreadContext(ApplicationContext.getInstance());
			List<String> clientIdenfifiers = daoCollection.getGlobalDao().getClientIdentifiers(context);

			XTableModel tableModel = (XTableModel) TableTestStatus.getInstance().getModel();
			tableModel.setRowCount(clientIdenfifiers.size());

			for (int i = 0; i < tableModel.getRowCount(); ++i) {
				String status = (String) tableModel.getValueAt(i,
						TableTestStatus.getInstance().getTableParameters().get(ColumnEnum.STATUS).getColumnIndex());
				if (status.trim().equalsIgnoreCase("RUNNING")) {
					running = true;
					break;
				}
			}
		} catch (UserException e) {
			throw new AppException(e.getCause());
		}

		dialog.setRunState(running);

		return running;
	}

	public boolean isDone() throws UserException {

		boolean done = true;

		try {
			Context context = new ThreadContext(ApplicationContext.getInstance());
			List<String> clientIdenfifiers = daoCollection.getGlobalDao().getClientIdentifiers(context);

			XTableModel tableModel = (XTableModel) TableTestStatus.getInstance().getModel();
			tableModel.setRowCount(clientIdenfifiers.size());

			for (int i = 0; i < tableModel.getRowCount(); ++i) {
				String status = (String) tableModel.getValueAt(i,
						TableTestStatus.getInstance().getTableParameters().get(ColumnEnum.STATUS).getColumnIndex());
				if (!status.trim().equalsIgnoreCase("DONE")) {
					done = false;
					break;
				}
			}
		} catch (UserException e) {
			throw new AppException(e.getCause());
		}

		return done;
	}

	public void stop() {
		try {
			if (dialog != null) {
				dialog.setRunState(false);
			}
			Context context = new ThreadContext(ApplicationContext.getInstance());
			List<String> clientIdenfifiers = daoCollection.getGlobalDao().getClientIdentifiers(context);

			XTableModel tableModel = (XTableModel) TableTestStatus.getInstance().getModel();
			tableModel.setRowCount(clientIdenfifiers.size());

			for (int i = 0; i < tableModel.getRowCount(); ++i) {
				String status = (String) tableModel.getValueAt(i,
						TableTestStatus.getInstance().getTableParameters().get(ColumnEnum.STATUS).getColumnIndex());
				if (status.trim().equalsIgnoreCase("RUNNING")) {

					tableModel.setValueAt("STOPPED", i,
							TableTestStatus.getInstance().getTableParameters().get(ColumnEnum.STATUS).getColumnIndex());
				}
			}
		} catch (UserException e) {
			throw new AppException(e.getCause());
		}
	}

	@Override
	public void handleResponse(Context context, ServiceResponse serviceResponse) {
		/*
		 * Do nothing
		 */
	}

	public Map<ColumnEnum, XTableParameter> getTableParameters() {
		return new HashMap<>(tableParameters);
	}

	@Override
	public void handleException(Context context, Exception e) {
		context.put("clientIdentifier", ((ClientInformation) context.get("clientInformation")).getIdentifier());
		try {
			daoCollection.getGlobalDao().removeClient(context);
		} catch (UserException ex) {
			throw new AppException(ex.getCause());
		}
	}

	@Override
	public void handleException(Context context, String response) {
		context.put("clientIdentifier", ((ClientInformation) context.get("clientInformation")).getIdentifier());
		try {
			daoCollection.getGlobalDao().removeClient(context);
		} catch (UserException ex) {
			throw new AppException(ex.getCause());
		}
	}

	public void loadData() throws UserException {

		Map<TableTestStatus.ColumnEnum, XTableParameter> tableParameters = TableTestStatus.getInstance()
				.getTableParameters();

		Context context = new ThreadContext(ApplicationContext.getInstance());
		List<String> clientIdenfifiers = daoCollection.getGlobalDao().getClientIdentifiers(context);

		XTableModel tableModel = (XTableModel) TableTestStatus.getInstance().getModel();
		tableModel.setRowCount(clientIdenfifiers.size());

		int rowNumber = 0;

		for (String clientIdenfifier : clientIdenfifiers) {
			tableModel.setValueAt(clientIdenfifier, rowNumber,
					tableParameters.get(ColumnEnum.CLIENT_IDENTIFIER).getColumnIndex());
			tableModel.setValueAt(0, rowNumber, tableParameters.get(ColumnEnum.LOOP_COUNTER).getColumnIndex());
			tableModel.setValueAt("-", rowNumber, tableParameters.get(ColumnEnum.STATUS).getColumnIndex());

			++rowNumber;
		}
	}

	public static enum ColumnEnum {
		CLIENT_IDENTIFIER, LOOP_COUNTER, STATUS
	}
}
