package com.qbanalytix.cognostest.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.ResourceUtils;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.exeptions.ExceptionHandler;

public class XJBrowseButton extends XJButton {

	private static final long serialVersionUID = 1L;

	public static final int FILES_ONLY = JFileChooser.FILES_ONLY;
	public static final int DIRECTORIES_ONLY = JFileChooser.DIRECTORIES_ONLY;
	public static final int FILES_AND_DIRECTORIES = JFileChooser.FILES_AND_DIRECTORIES;

	private JTextField textField;
	private FileFilter[] fileFilters;
	private FocusAdapter focusAdapter = new MyFocusAdapter();

	public XJBrowseButton() {
		this(FILES_ONLY);
	}

	public XJBrowseButton(int fileSelectionMode, FileFilter... fileFilters) {
		this.fileFilters = fileFilters;
		final int localSelectionMode = fileSelectionMode;
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDialog(localSelectionMode);
			}
		});
	}

	public void setRelatedTextField(JTextField textField) {
		JTextField oldTextField = this.textField;
		if (oldTextField != null) {
			oldTextField.removeFocusListener(focusAdapter);
		}

		this.textField = textField;
		this.textField.addFocusListener(focusAdapter);
		if (this.textField.getText().trim().isEmpty()) {
			this.textField.setText(ResourceUtils.getResourceBase().getAbsolutePath());
		}
	}

	public JTextField getRelatedTextField() {
		return textField;
	}

	private void openDialog(int fileSelectionMode) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(fileSelectionMode);

		if (textField != null) {
			fileChooser.setCurrentDirectory(new File(textField.getText()).getParentFile());
		}

		fileChooser.setAcceptAllFileFilterUsed(false);
		for (FileFilter fileFilter : fileFilters) {
			fileChooser.addChoosableFileFilter(fileFilter);
		}
		fileChooser.setAcceptAllFileFilterUsed(true);

		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			checkSelection(selectedFile);
		} else {
			onCancel();
		}
	}

	private void checkSelection(File selectedFile) {
		// Default is set to "true" to make sure if there is no file filter
		// then the selected file must be mark as valid
		boolean accept = true;

		for (FileFilter fileFilter : fileFilters) {
			if (fileFilter.accept(selectedFile)) {
				accept = true;
				break;
			} else {
				accept = false;
			}
		}

		if (!accept) {
			onWrongSelection(selectedFile);
		} else {
			if (textField != null) {
				textField.setText(selectedFile.getAbsolutePath());
			}
			onApprove(selectedFile);
		}
	}

	public void onCancel() {
	}

	public void onApprove(File selectedFile) {
	}

	public void onWrongSelection(File selectedFile) {
		ExceptionHandler.handleException(SwingUtilities.getWindowAncestor(this),
				new UserException("Wrong file selection"));
	}

	private class MyFocusAdapter extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e) {
			checkSelection(new File(XJBrowseButton.this.textField.getText()));
		}
	}
}
