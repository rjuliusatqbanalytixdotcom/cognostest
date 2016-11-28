package com.qbanalytix.cognostest.ui.forms;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import com.ganesha.context.Context;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTableDialog;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.qbanalytix.cognostest.business.application.DownloadReport;
import com.qbanalytix.cognostest.business.dao.DaoCollection;
import com.qbanalytix.cognostest.clientinvoker.InvokeLaunchTestScenarioThread;
import com.qbanalytix.cognostest.clientinvoker.base.IClientInvokerListener;
import com.qbanalytix.cognostest.context.ApplicationContext;
import com.qbanalytix.cognostest.context.ThreadContext;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.ui.forms.clientinvokerlistener.TableTestStatus;
import com.qbanalytix.cognostest.ui.forms.clientinvokerlistener.TableTestStatus.ColumnEnum;
import com.qbanalytix.cognostest.web.ServiceResponse;

import net.miginfocom.swing.MigLayout;

public class TestDialog extends XJTableDialog implements IClientInvokerListener {

	private static final long serialVersionUID = 1401014426195840845L;

	private DaoCollection daoCollection = (DaoCollection) ApplicationContext.getBean("daoCollection");

	private JScrollPane scrollPane;
	private XJButton btnStop;
	private XJButton btnRun;
	private XJButton btnClose;
	private XJButton btnGenerateResults;

	public TestDialog(Window parent) {
		super(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Test Status");

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					initForm();
				} catch (Exception ex) {
					ExceptionHandler.handleException(TestDialog.this, ex);
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
				try {
				} catch (Exception ex) {
					ExceptionHandler.handleException(TestDialog.this, ex);
				}
			}
		});
		getContentPane().setLayout(new MigLayout("", "[500px,grow]", "[200px][]"));

		scrollPane = new JScrollPane(TableTestStatus.getInstance());
		getContentPane().add(scrollPane, "cell 0 0,grow");

		TableTestStatus.getInstance().setDialog(this);

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][][][]", "[]"));

		btnClose = new XJButton();
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnClose.setMnemonic('C');
		btnClose.setText("Close");
		pnlButton.add(btnClose, "cell 0 0");

		btnStop = new XJButton();
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TableTestStatus.getInstance().stop();
			}
		});
		btnStop.setMnemonic('O');
		btnStop.setText("Stop");
		pnlButton.add(btnStop, "cell 1 0");

		btnRun = new XJButton();
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (!TableTestStatus.getInstance().isRunning()) {
						triggerRunToClient();
					}
					setRunState(true);
				} catch (UserException ex) {
					ExceptionHandler.handleException(TestDialog.this, ex);
				}
			}
		});
		btnRun.setMnemonic('R');
		btnRun.setText("Run");
		pnlButton.add(btnRun, "cell 2 0");

		btnGenerateResults = new XJButton();
		btnGenerateResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					DownloadReport.download();
				} catch (UserException ex) {
					ExceptionHandler.handleException(TestDialog.this, ex);
				}
			}
		});
		btnGenerateResults.setMnemonic('G');
		btnGenerateResults.setText("Generate Results");
		pnlButton.add(btnGenerateResults, "cell 3 0");

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		default:
			break;
		}
	}

	private void initForm() throws UserException {
		setRunState(TableTestStatus.getInstance().isRunning());
		loadDataInThread();
	}

	public void setRunState(boolean runState) throws UserException {
		btnRun.setEnabled(!runState);
		btnStop.setEnabled(runState);
	}

	private void triggerRunToClient() throws UserException {

		daoCollection.getGlobalDao().clearClientReport(null);

		List<String> clientIdentifiers = daoCollection.getGlobalDao().getClientIdentifiers(null);
		int numberOfThread = clientIdentifiers.size();
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThread);

		for (int i = 0; i < numberOfThread; ++i) {
			Context context = new ThreadContext(ApplicationContext.getInstance());
			context.put("clientIdentifier", clientIdentifiers.get(i));
			InvokeLaunchTestScenarioThread thread = new InvokeLaunchTestScenarioThread(
					daoCollection.getGlobalDao().getClient(context), this);
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
	public void loadData() throws UserException {
		TableTestStatus.getInstance().loadData();
	}

	@Override
	public void handleResponse(Context context, ServiceResponse serviceResponse) {
		ClientInformation clientInformation = (ClientInformation) context.get("clientInformation");
		updateClientStatus(clientInformation, "RUNNING");
	}

	@Override
	public void handleException(Context context, Exception e) {
		ClientInformation clientInformation = (ClientInformation) context.get("clientInformation");
		updateClientStatus(clientInformation, "ERROR");
	}

	@Override
	public void handleException(Context context, String response) {
		ClientInformation clientInformation = (ClientInformation) context.get("clientInformation");
		updateClientStatus(clientInformation, "ERROR");
	}

	private void updateClientStatus(ClientInformation clientInformation, String status) {
		XTableModel tableModel = (XTableModel) TableTestStatus.getInstance().getModel();
		for (int i = 0; i < tableModel.getRowCount(); ++i) {
			String identifier = (String) tableModel.getValueAt(i, TableTestStatus.getInstance().getTableParameters()
					.get(ColumnEnum.CLIENT_IDENTIFIER).getColumnIndex());
			if (identifier.equals(clientInformation.getIdentifier())) {
				tableModel.setValueAt(status, i,
						TableTestStatus.getInstance().getTableParameters().get(ColumnEnum.STATUS).getColumnIndex());
			}
		}
	}
}