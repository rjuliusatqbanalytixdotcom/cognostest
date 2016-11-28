package com.qbanalytix.cognostest.ui.forms;

import java.awt.Color;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.ResourceUtils;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJFrame;
import com.ganesha.desktop.component.XJMenu;
import com.ganesha.desktop.component.XJMenuItem;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.qbanalytix.cognostest.common.ImagePanel;
import com.qbanalytix.cognostest.resources.constants.Constants;

import net.miginfocom.swing.MigLayout;

public class MainFrame extends XJFrame {
	private static final long serialVersionUID = 5527217675003046133L;

	private static MainFrame instance;
	private ImagePanel imagePanel;
	private JMenuBar menuBar;

	public static MainFrame getInstance() {
		if (instance == null) {
			instance = new MainFrame();
		}
		return instance;
	}

	private MainFrame() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					loadImages();
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		getContentPane().setBackground(Color.BLACK);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(Constants.APPLICATION_NAME + " " + Constants.VERSION);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		XJMenu mnSetting = createMenu("Setting", null);
		createMenuItem(mnSetting, CognosInformationDialog.class, "Cognos Information", false, null);
		createMenuItem(mnSetting, ClientConfigurationDialog.class, "Client Configuration", false, null);
		
		XJMenu mnRun = createMenu("Run", null);
		createMenuItem(mnRun, TestDialog.class, "Run Test", false, null);

		getContentPane()
				.setLayout(new MigLayout("", "[200px,grow][][200px,grow]", "[50px,grow][][100px,grow,baseline]"));

		imagePanel = new ImagePanel();
		getContentPane().add(imagePanel, "cell 1 1,alignx center");
		imagePanel.setLayout(new MigLayout("", "[254:254]", "[204:204]"));

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		default:
			break;
		}
	}

	private void loadIcon() {
		try {
			List<Image> images = new ArrayList<>();
			images.add(
					ImageIO.read(new File(ResourceUtils.getImageBase(), SystemSetting.getProperty("image.icon.20"))));
			images.add(
					ImageIO.read(new File(ResourceUtils.getImageBase(), SystemSetting.getProperty("image.icon.24"))));
			images.add(
					ImageIO.read(new File(ResourceUtils.getImageBase(), SystemSetting.getProperty("image.icon.28"))));
			images.add(
					ImageIO.read(new File(ResourceUtils.getImageBase(), SystemSetting.getProperty("image.icon.32"))));
			images.add(
					ImageIO.read(new File(ResourceUtils.getImageBase(), SystemSetting.getProperty("image.icon.36"))));
			images.add(
					ImageIO.read(new File(ResourceUtils.getImageBase(), SystemSetting.getProperty("image.icon.40"))));
			images.add(
					ImageIO.read(new File(ResourceUtils.getImageBase(), SystemSetting.getProperty("image.icon.44"))));
			images.add(
					ImageIO.read(new File(ResourceUtils.getImageBase(), SystemSetting.getProperty("image.icon.48"))));
			images.add(
					ImageIO.read(new File(ResourceUtils.getImageBase(), SystemSetting.getProperty("image.icon.64"))));
			setIconImages(images);
		} catch (IOException e) {
			throw new AppException(e);
		}
	}

	private void loadBgLogo() {
		try {
			File logoBig = new File(ResourceUtils.getImageBase(), SystemSetting.getProperty("image.logo.big"));
			BufferedImage bufferedImage = ImageIO.read(logoBig);
			imagePanel.showImage(bufferedImage);
		} catch (IOException e) {
			throw new AppException(e);
		}
	}

	private void loadImages() {
		loadIcon();
		loadBgLogo();
	}

	private XJMenu createMenu(String label, XJMenu parent) {
		XJMenu menu = new XJMenu(label);
		if (parent == null) {
			menuBar.add(menu);
		} else {
			parent.add(menu);
		}
		return menu;
	}

	private XJMenuItem createMenuItem(XJMenu parent, final Class<? extends XJDialog> clazz, String label,
			final boolean permissionRequired, String permissionCode) {

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					XJDialog dialog = newDialog(clazz);
					dialog.setPermissionRequired(permissionRequired);
					dialog.setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		};

		return createMenuItem(parent, actionListener, label, permissionRequired, permissionCode);
	}

	private XJMenuItem createMenuItem(XJMenu parent, ActionListener actionListener, String label,
			final boolean permissionRequired, String permissionCode) {

		XJMenuItem menuItem = new XJMenuItem(label, permissionCode);
		menuItem.addActionListener(actionListener);
		parent.add(menuItem);

		return menuItem;
	}

	private XJDialog newDialog(Class<? extends XJDialog> clazz) throws AppException, UserException {
		try {
			Constructor<? extends XJDialog> constructor = clazz.getConstructor(Window.class);
			XJDialog dialog = constructor.newInstance(this);
			return dialog;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException e) {
			throw new AppException(e);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof AppException) {
				throw (AppException) e.getTargetException();
			} else if (e.getTargetException() instanceof UserException) {
				throw (UserException) e.getTargetException();
			} else {
				throw new AppException(e.getTargetException());
			}
		}
	}
}
