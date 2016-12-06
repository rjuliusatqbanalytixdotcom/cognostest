package com.qbanalytix.cognostest.ui.forms;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTableDialog;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.qbanalytix.cognostest.application.ProgressStatus;
import com.qbanalytix.cognostest.business.application.DownloadReport;

import net.miginfocom.swing.MigLayout;

public class TestDialog extends XJTableDialog {

	private static final long serialVersionUID = 1401014426195840845L;

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
		getContentPane().setLayout(new MigLayout("", "[700px,grow]", "[200px][]"));

		scrollPane = new JScrollPane(ProgressStatus.getInstance());
		getContentPane().add(scrollPane, "cell 0 0,grow");

		ProgressStatus.setDialog(this);

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
				try {
					ProgressStatus.stop();
					setRunState(false);
				} catch (UserException ex) {
					ExceptionHandler.handleException(TestDialog.this, ex);
				}
			}
		});
		btnStop.setMnemonic('O');
		btnStop.setText("Stop");
		pnlButton.add(btnStop, "cell 1 0");

		btnRun = new XJButton();
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ProgressStatus.start();
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
					DownloadReport.exportProgressStatusToCSV();
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
		setRunState(ProgressStatus.isRunning());
	}

	public void setRunState(boolean runState) throws UserException {
		btnRun.setEnabled(!runState);
		btnStop.setEnabled(runState);
	}

	@Override
	public void loadData() throws UserException {
		/*
		 * Do nothing
		 */
	}
}