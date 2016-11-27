package com.qbanalytix.cognostest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.XComponentProperties;
import com.ganesha.desktop.component.permissionutils.PermissionChecker;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.desktop.exeptions.UserExceptionHandler;
import com.qbanalytix.cognostest.ui.SimplePermissionChecker;
import com.qbanalytix.cognostest.ui.forms.MainFrame;
import com.qbanalytix.cognostest.web.RequestHandler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					checkAndSetEnvironmentVariables();

					setDefaultUncaughtExceptionHandler();
					setLookAndFeel();
					// initHibernate();
					PermissionChecker.register(new SimplePermissionChecker());

					startJetty();
					runQuartz();
					
					MainFrame.getInstance().setVisible(true);

				} catch (Exception e) {
					ExceptionHandler.handleException(null, e);
					System.exit(1);
				}
			}
		});
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			setUIStyles();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			ExceptionHandler.handleException(null, e);
		}
	}

	private static void setDefaultUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				ExceptionHandler.handleException(null, e);
			}
		});
	}

	private static void setUIStyles() {
		InputStream inputStream = null;
		try {
			inputStream = Main.class.getClassLoader().getResourceAsStream("xcomponent.properties");
			XComponentProperties.loadProperties(inputStream);

		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					ExceptionHandler.handleException(null, e);
				}
			}
		}
	}

	private static void checkAndSetEnvironmentVariables() {
		String errorTitle = "Failed to start";
		if (System.getenv("cognostest.home") == null || System.getenv("cognostest.home").trim().isEmpty()) {
			UserExceptionHandler.handleException(null,
					"Variable \"qbtools.home\" must be set in the Environment Variables", errorTitle);
			System.exit(1);
		}

		setupLogging();
	}

	private static void setupLogging() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.reset();

		InputStream inputStream = null;
		try {
			inputStream = FileUtils.openInputStream(new File(SystemSetting.getProperty("logging.file.config")));

			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(loggerContext);
			configurator.doConfigure(inputStream);

		} catch (IOException e) {
			ExceptionHandler.handleException(null, e);
		} catch (JoranException e) {
			ExceptionHandler.handleException(null, e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private static void startJetty() {
		new Thread() {
			@Override
			public void run() {
				try {
					Server server = new Server(
							Formatter.formatStringToNumber(SystemSetting.getProperty("server.config.port")).intValue());
					server.setHandler(new RequestHandler());
					server.start();
					server.join();
				} catch (Exception e) {
					throw new AppException(e);
				}
			}
		}.start();
	}

	private static void runQuartz() throws SchedulerException {
		SchedulerFactory factory = new StdSchedulerFactory("quartz.properties");
		Scheduler scheduler = factory.getScheduler();
		scheduler.start();
	}
}
