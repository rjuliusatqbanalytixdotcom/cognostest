
package com.qbanalytix.cognostest.ui.forms;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJPasswordField;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.XTitledBorder;
import com.ganesha.desktop.component.converter.MappingFormBean;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.qbanalytix.cognostest.business.dao.DaoCollection;
import com.qbanalytix.cognostest.context.ApplicationContext;
import com.qbanalytix.cognostest.context.ThreadContext;
import com.qbanalytix.cognostest.resources.model.ClientInformation;
import com.qbanalytix.cognostest.resources.model.CognosInformation;
import com.qbanalytix.cognostest.ui.forms.clientinvokerlistener.ListClientIdentifier;

import net.miginfocom.swing.MigLayout;

public class ClientConfigurationDialog extends XJDialog {

	private static final long serialVersionUID = 1L;

	private DaoCollection daoCollection = (DaoCollection) ApplicationContext.getBean("daoCollection");
	private XJTextField txtCognosUsername;
	private XJPasswordField txtCognosPassword;
	private XJTextField txtNumerOfThread;
	private XJTextField txtCognosReportTestCounter;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJPanel pnlClientConfiguration;
	private XJLabel lblHostname;
	private XJTextField txtHostname;
	private XJLabel lblIpAddress;
	private XJTextField txtIpAddress;
	private XJLabel lblPort;
	private XJTextField txtPort;
	private XJPanel pnlClientList;
	private JScrollPane scrollPane;
	private ListClientIdentifier listClientIdentifier;
	private XJButton btnDefault;
	private XJPanel pnlButton;
	private XJButton btnCancel;
	private XJButton btnSave;

	private CognosInformation defaultCognosInformation;
	private XJPanel pnlCognosInformation;

	{
		tableParameters.put(ColumnEnum.TEST_URL, new XTableParameter(0, 10, false, "Test URL(s)", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));
	}

	public ClientConfigurationDialog(Window parent) {
		super(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setCloseOnEsc(false);
		setTitle("Client Configuration");

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					initForm();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ClientConfigurationDialog.this, ex);
				}
			}
		});

		getContentPane().setLayout(new MigLayout("", "[200px,grow][300px,grow]", "[grow][grow][grow]"));

		pnlClientList = new XJPanel();
		pnlClientList.setBorder(new XTitledBorder("Client List"));
		getContentPane().add(pnlClientList, "cell 0 0 1 2,grow");
		pnlClientList.setLayout(new MigLayout("", "[grow]", "[grow]"));

		scrollPane = new JScrollPane();
		pnlClientList.add(scrollPane, "cell 0 0,grow");

		listClientIdentifier = ListClientIdentifier.getInstance();
		while (listClientIdentifier.getListSelectionListeners().length > 0) {
			listClientIdentifier.removeListSelectionListener(listClientIdentifier.getListSelectionListeners()[0]);
		}
		listClientIdentifier.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				clientSelected();
			}
		});
		scrollPane.setViewportView(listClientIdentifier);

		pnlClientConfiguration = new XJPanel();
		pnlClientConfiguration.setBorder(new XTitledBorder("Client Information"));
		getContentPane().add(pnlClientConfiguration, "cell 1 0,grow");
		pnlClientConfiguration.setLayout(new MigLayout("", "[100px][grow]", "[][][][]"));

		lblHostname = new XJLabel();
		lblHostname.setText("Hostname");
		pnlClientConfiguration.add(lblHostname, "cell 0 0,alignx trailing");

		txtHostname = new XJTextField();
		txtHostname.setName("hostname");
		txtHostname.setEditable(false);
		pnlClientConfiguration.add(txtHostname, "cell 1 0,growx,aligny top");

		lblIpAddress = new XJLabel();
		lblIpAddress.setText("Ip Address");
		pnlClientConfiguration.add(lblIpAddress, "cell 0 1,alignx trailing");

		txtIpAddress = new XJTextField();
		txtIpAddress.setName("ipAddress");
		txtIpAddress.setEditable(false);
		pnlClientConfiguration.add(txtIpAddress, "cell 1 1,growx,aligny top");

		lblPort = new XJLabel();
		lblPort.setText("Port");
		pnlClientConfiguration.add(lblPort, "cell 0 2,alignx trailing");

		txtPort = new XJTextField();
		txtPort.setName("port");
		txtPort.setEditable(false);
		pnlClientConfiguration.add(txtPort, "cell 1 2,growx");

		pnlCognosInformation = new XJPanel();
		pnlCognosInformation.setBorder(new XTitledBorder("Cognos Information"));
		getContentPane().add(pnlCognosInformation, "cell 1 1,grow");
		pnlCognosInformation.setLayout(new MigLayout("", "[100px][grow]", "[][][][][]"));

		XJLabel lblCognosUsername = new XJLabel();
		pnlCognosInformation.add(lblCognosUsername, "cell 0 0,alignx trailing");
		lblCognosUsername.setText("Cognos Username");

		txtCognosUsername = new XJTextField();
		txtCognosUsername.setName("cognosUsername");
		pnlCognosInformation.add(txtCognosUsername, "cell 1 0,growx");

		XJLabel lblCognosPassword = new XJLabel();
		pnlCognosInformation.add(lblCognosPassword, "cell 0 1,alignx trailing");
		lblCognosPassword.setText("Cognos Password");

		txtCognosPassword = new XJPasswordField();
		txtCognosPassword.setName("cognosPassword");
		pnlCognosInformation.add(txtCognosPassword, "cell 1 1,growx");

		XJLabel lblNumberOfThread = new XJLabel();
		pnlCognosInformation.add(lblNumberOfThread, "cell 0 2,alignx trailing");
		lblNumberOfThread.setText("Number of Thread");

		txtNumerOfThread = new XJTextField();
		pnlCognosInformation.add(txtNumerOfThread, "cell 1 2,growx");
		txtNumerOfThread.setName("numberOfThread");

		XJLabel lblNumbeOfLoop = new XJLabel();
		pnlCognosInformation.add(lblNumbeOfLoop, "cell 0 3,alignx trailing");
		lblNumbeOfLoop.setText("Number of Loop");

		txtCognosReportTestCounter = new XJTextField();
		pnlCognosInformation.add(txtCognosReportTestCounter, "cell 1 3,growx");
		txtCognosReportTestCounter.setName("cognosReportTestCounter");

		btnDefault = new XJButton();
		btnDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDefault();
			}
		});
		btnDefault.setMnemonic('D');
		btnDefault.setText("Default");
		pnlCognosInformation.add(btnDefault, "flowx,cell 1 4,alignx right");

		btnSave = new XJButton();
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		btnSave.setMnemonic('S');
		btnSave.setText("Save");
		pnlCognosInformation.add(btnSave, "cell 1 4");

		pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 2 2 1,alignx trailing,growy");
		pnlButton.setLayout(new MigLayout("", "[]", "[][]"));

		btnCancel = new XJButton();
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		btnCancel.setMnemonic('C');
		btnCancel.setText("Close");
		pnlButton.add(btnCancel, "cell 0 0");

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

	private void setDefault() {
		int row = listClientIdentifier.getSelectedIndex();
		if (row < 0) {
			return;
		}
		MappingFormBean.setValueToView(defaultCognosInformation, this);
	}

	private void clientSelected() {
		int row = listClientIdentifier.getSelectedIndex();
		if (row < 0) {
			pnlClientConfiguration.setVisible(false);
			pnlCognosInformation.setVisible(false);
			return;
		} else {
			pnlClientConfiguration.setVisible(true);
			pnlCognosInformation.setVisible(true);
		}
		MappingFormBean.setValueToView(listClientIdentifier.getSelectedValue().getObject(), this);
	}

	private void save() {
		int row = listClientIdentifier.getSelectedIndex();
		if (row < 0) {
			return;
		}
		ClientInformation clientInformation = MappingFormBean.createInstanceFormBean(this, ClientInformation.class);
		listClientIdentifier.getSelectedValue().setObject(clientInformation);
	}

	private void cancel() {
		dispose();
	}

	private void initForm() throws UserException {
		defaultCognosInformation = daoCollection.getGlobalDao()
				.loadCognosConfiguraton(new ThreadContext(ApplicationContext.getInstance()));

		if (defaultCognosInformation == null) {
			defaultCognosInformation = new CognosInformation();
		}

		clientSelected();
	}

	private enum ColumnEnum {
		TEST_URL
	}
}
