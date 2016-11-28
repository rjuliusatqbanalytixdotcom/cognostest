
package com.qbanalytix.cognostest.ui.forms;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import com.ganesha.context.Context;
import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJPasswordField;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.XTitledBorder;
import com.ganesha.desktop.component.converter.MappingFormBean;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.qbanalytix.cognostest.business.dao.DaoCollection;
import com.qbanalytix.cognostest.context.ApplicationContext;
import com.qbanalytix.cognostest.context.ThreadContext;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.resources.model.CognosInformation;

import net.miginfocom.swing.MigLayout;

public class CognosInformationDialog extends XJDialog {

	private static final long serialVersionUID = 1L;

	private DaoCollection daoCollection = (DaoCollection) ApplicationContext.getBean("daoCollection");

	private XJTextField txtCognosURL;
	private XJTextField txtCognosUsername;
	private XJPasswordField txtCognosPassword;
	private XJTextField txtCognosLogoutURL;
	private XJTextField txtNumerOfThread;
	private XJTextField txtCognosReportTestCounter;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJTextField txtNewTestURL;
	private JScrollPane scrollPane;
	private XJTable table;
	private XJButton btnAdd;
	private XJButton btnDelete;
	private XJPanel panel;
	private XJButton btnCancel;
	private XJButton btnSave;
	private XJButton btnSetToAll;
	private XJLabel lblWebClient;
	private XJComboBox comboBox;

	{
		tableParameters.put(ColumnEnum.TEST_URL, new XTableParameter(0, 10, false, "Test URL(s)", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));
	}

	public CognosInformationDialog(Window parent) {
		super(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setCloseOnEsc(false);
		setTitle("Cognos Information");

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					initForm();
				} catch (Exception ex) {
					ExceptionHandler.handleException(CognosInformationDialog.this, ex);
				}
			}
		});

		getContentPane().setLayout(new MigLayout("", "[300px,grow][300px,grow]", "[grow][grow][grow]"));

		XJPanel pnlCognosInformation = new XJPanel();
		pnlCognosInformation.setBorder(new XTitledBorder("Cognos Information"));
		getContentPane().add(pnlCognosInformation, "cell 0 0,grow");
		pnlCognosInformation.setLayout(new MigLayout("", "[100px][grow]", "[][][][]"));

		XJLabel lblCognosURL = new XJLabel();
		pnlCognosInformation.add(lblCognosURL, "cell 0 0,alignx trailing");
		lblCognosURL.setText("Cognos URL");

		txtCognosURL = new XJTextField();
		txtCognosURL.setName("cognosURL");
		pnlCognosInformation.add(txtCognosURL, "cell 1 0,growx");

		XJLabel lblCognosUsername = new XJLabel();
		pnlCognosInformation.add(lblCognosUsername, "cell 0 1,alignx trailing");
		lblCognosUsername.setText("Cognos Username");

		txtCognosUsername = new XJTextField();
		txtCognosUsername.setName("cognosUsername");
		pnlCognosInformation.add(txtCognosUsername, "cell 1 1,growx");

		XJLabel lblCognosPassword = new XJLabel();
		pnlCognosInformation.add(lblCognosPassword, "cell 0 2,alignx trailing");
		lblCognosPassword.setText("Cognos Password");

		txtCognosPassword = new XJPasswordField();
		txtCognosPassword.setName("cognosPassword");
		pnlCognosInformation.add(txtCognosPassword, "cell 1 2,growx");

		XJLabel lblCognosLogoutURL = new XJLabel();
		lblCognosLogoutURL.setText("Cognos Logout URL");
		pnlCognosInformation.add(lblCognosLogoutURL, "cell 0 3,alignx trailing");

		txtCognosLogoutURL = new XJTextField();
		txtCognosLogoutURL.setName("cognosLogoutURL");
		pnlCognosInformation.add(txtCognosLogoutURL, "cell 1 3,growx");

		XJPanel pnlTestURL = new XJPanel();
		pnlTestURL.setBorder(new XTitledBorder("Test URL(s)"));
		getContentPane().add(pnlTestURL, "cell 1 0 1 2,grow");
		pnlTestURL.setLayout(new MigLayout("", "[grow]", "[][][10px,grow,baseline][]"));

		txtNewTestURL = new XJTextField();
		pnlTestURL.add(txtNewTestURL, "cell 0 0,growx");

		btnAdd = new XJButton();
		btnAdd.setMnemonic('A');
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTestURL();
			}
		});
		btnAdd.setText("Add");
		pnlTestURL.add(btnAdd, "cell 0 1,alignx right");

		scrollPane = new JScrollPane();
		pnlTestURL.add(scrollPane, "cell 0 2,grow");

		table = new XJTable();
		XTableUtils.initTable(table, tableParameters);

		scrollPane.setViewportView(table);

		btnDelete = new XJButton();
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteTestURL();
			}
		});
		btnDelete.setMnemonic('D');
		btnDelete.setText("Delete");
		pnlTestURL.add(btnDelete, "cell 0 3,alignx right");

		XJPanel pnlTestScenario = new XJPanel();
		pnlTestScenario.setBorder(new XTitledBorder("Test Scenario"));
		getContentPane().add(pnlTestScenario, "cell 0 1,grow");
		pnlTestScenario.setLayout(new MigLayout("", "[100px][grow]", "[][][]"));

		XJLabel lblNumberOfThread = new XJLabel();
		lblNumberOfThread.setText("Number of Thread");
		pnlTestScenario.add(lblNumberOfThread, "cell 0 0,alignx trailing");

		txtNumerOfThread = new XJTextField();
		txtNumerOfThread.setName("numberOfThread");
		pnlTestScenario.add(txtNumerOfThread, "cell 1 0,growx");

		XJLabel lblNumbeOfLoop = new XJLabel();
		lblNumbeOfLoop.setText("Number of Loop");
		pnlTestScenario.add(lblNumbeOfLoop, "cell 0 1,alignx trailing");

		txtCognosReportTestCounter = new XJTextField();
		txtCognosReportTestCounter.setName("cognosReportTestCounter");
		pnlTestScenario.add(txtCognosReportTestCounter, "cell 1 1,growx");
		
		lblWebClient = new XJLabel();
		lblWebClient.setText("Web Client");
		pnlTestScenario.add(lblWebClient, "cell 0 2,alignx trailing");
		
		comboBox = new XJComboBox();
		comboBox.setName("webclient");
		comboBox.addItem(new ComboBoxObject(null, null));
		comboBox.addItem(new ComboBoxObject("Internet Explorer", "Internet Explorer"));
		comboBox.addItem(new ComboBoxObject("Mozilla Firefox", "Mozilla Firefox"));
		comboBox.addItem(new ComboBoxObject("Google Chrome", "Google Chrome"));
		comboBox.addItem(new ComboBoxObject("Opera", "Opera"));
		pnlTestScenario.add(comboBox, "cell 1 2,growx");

		panel = new XJPanel();
		getContentPane().add(panel, "cell 0 2 2 1,grow");
		panel.setLayout(new MigLayout("", "[][grow][][]", "[][]"));

		btnCancel = new XJButton();
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		btnSetToAll = new XJButton();
		btnSetToAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setToAll();
				} catch (UserException ex) {
					ExceptionHandler.handleException(CognosInformationDialog.this, ex);
				}
			}
		});
		btnSetToAll.setMnemonic('T');
		btnSetToAll.setText("Set to All Client");
		panel.add(btnSetToAll, "cell 0 0");
		btnCancel.setMnemonic('C');
		btnCancel.setText("Cancel");
		panel.add(btnCancel, "cell 2 0");

		btnSave = new XJButton();
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(CognosInformationDialog.this, ex);
				}
			}
		});
		btnSave.setMnemonic('S');
		btnSave.setText("Save");
		panel.add(btnSave, "cell 3 0");

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_ENTER:
			if (txtNewTestURL.isFocusOwner()) {
				btnAdd.doClick();
			}
			break;
		case KeyEvent.VK_DELETE:
			if (table.isFocusOwner()) {
				btnDelete.doClick();
			}
			break;
		default:
			break;
		}
	}

	private void addTestURL() {
		String newTestURL = txtNewTestURL.getText().trim();
		XTableModel tableModel = (XTableModel) table.getModel();
		tableModel.setRowCount(tableModel.getRowCount() + 1);
		int rowIndex = tableModel.getRowCount() - 1;
		tableModel.setValueAt(newTestURL, rowIndex, tableParameters.get(ColumnEnum.TEST_URL).getColumnIndex());
		txtNewTestURL.setText("");
	}

	private void deleteTestURL() {
		int row = table.getSelectedRow();
		int column = table.getSelectedColumn();
		if (row < 0) {
			return;
		}

		XTableModel tableModel = (XTableModel) table.getModel();
		tableModel.removeRow(row);

		int rowCount = table.getRowCount();
		if (rowCount <= row) {
			--row;
		}
		table.changeSelection(row, column, false, false);
	}

	private void cancel() {
		dispose();
	}

	private void save() throws UserException {
		CognosInformation cognosInformation = MappingFormBean.createInstanceFormBean(this, CognosInformation.class);
		cognosInformation.setCognosReportURLs(new ArrayList<String>());
		XTableModel tableModel = (XTableModel) table.getModel();
		for (int i = 0; i < tableModel.getRowCount(); ++i) {
			String testURL = (String) tableModel.getValueAt(i, 0);
			cognosInformation.getCognosReportURLs().add(testURL);
		}

		Context context = new ThreadContext(ApplicationContext.getInstance());
		context.put("cognosInformation", cognosInformation);

		daoCollection.getGlobalDao().saveCognosConfiguration(context);
		dispose();
	}

	private void initForm() throws UserException {
		CognosInformation cognosInformation = daoCollection.getGlobalDao()
				.loadCognosConfiguraton(new ThreadContext(ApplicationContext.getInstance()));
		if (cognosInformation != null) {
			MappingFormBean.setValueToView(cognosInformation, this);
			XTableModel tableModel = (XTableModel) table.getModel();
			while (tableModel.getRowCount() > 0) {
				tableModel.removeRow(0);
			}
			for (String testURL : cognosInformation.getCognosReportURLs()) {
				tableModel.addRow(new Object[] { testURL });
			}
		}
	}

	private void setToAll() throws UserException {
		Context context = new ThreadContext(ApplicationContext.getInstance());
		List<String> clientIdentifiers = daoCollection.getGlobalDao().getClientIdentifiers(context);
		for (String clientIdentifier : clientIdentifiers) {
			context.put("clientIdentifier", clientIdentifier);
			ClientInformation clientInformation = daoCollection.getGlobalDao().getClient(context);
			CognosInformation cognosInformation = MappingFormBean.createInstanceFormBean(this, CognosInformation.class);
			clientInformation.setWebclient(cognosInformation.getWebclient());
			clientInformation.setNumberOfThread(cognosInformation.getNumberOfThread());
			clientInformation.setCognosUsername(cognosInformation.getCognosUsername());
			clientInformation.setCognosPassword(cognosInformation.getCognosPassword());
			clientInformation.setCognosReportTestCounter(cognosInformation.getCognosReportTestCounter());
		}
	}

	private enum ColumnEnum {
		TEST_URL
	}
}
