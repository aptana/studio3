/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.ide.core.io.LockUtils;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.configurationProcessors.installer.InstallerOptionsDialog;

/**
 * A Python install processor.<br>
 * This class is in charge of downloading and installing Python for Windows operating systems.<br>
 * Note: In case we decide to support something similar for MacOSX and Linux, this processor would probably need
 * delegators set up.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class PythonInstallProcessor extends InstallerConfigurationProcessor
{
	protected static final String PYTHON_DEFAULT_INSTALL_DIR = "C:\\Python"; //$NON-NLS-1$
	protected static final String INSTALL_FOR_ALL_USERS_ATTR = "install_for_all"; //$NON-NLS-1$
	private static final String PYTHON = "Python"; //$NON-NLS-1$
	protected static final int PYTHON_INSTALLER_PROCESS_CANCEL_CODE = 1602;
	private static boolean installationInProgress;
	private String installDir;

	/**
	 * Install Python on the machine.<br>
	 * The configuration will grab the installer from the given attributes.<br>
	 * We expect an array of attributes with the same structure described at {@link #loadAttributes(Object)}.
	 * 
	 * @param attributes
	 *            First - A string array of size 1, which contains the URL for the Python installer (.exe). Second -
	 *            (optional) map of additional attributes.
	 * @see com.aptana.configurations.processor.AbstractConfigurationProcessor#configure(org.eclipse.core.runtime.IProgressMonitor,
	 *      java.lang.Object)
	 * @see #loadAttributes(Object)
	 */
	@Override
	public ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes)
	{
		// Get a Class lock to avoid multiple installations at the same time even with multiple instances of this
		// processor
		synchronized (this.getClass())
		{
			if (installationInProgress)
			{
				return configurationStatus;
			}
			installationInProgress = true;
		}
		if (!Platform.OS_WIN32.equals(Platform.getOS()))
		{
			String err = "The Python installer processor is designed to work on Windows."; //$NON-NLS-1$
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(err));
			applyErrorAttributes(err);
			installationInProgress = false;
			return configurationStatus;
		}
		try
		{
			configurationStatus.removeAttribute(CONFIG_ATTR);
			clearErrorAttributes();

			// Load the installer's attributes
			IStatus loadingStatus = loadAttributes(attributes);
			if (!loadingStatus.isOK())
			{
				String message = loadingStatus.getMessage();
				applyErrorAttributes(message);
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
				return configurationStatus;
			}

			// Check that we got the expected single install URL
			if (urls.length != 1)
			{
				// structure error
				String err = NLS.bind(Messages.InstallProcessor_wrongNumberOfInstallLinks, new Object[] { PYTHON, 1,
						urls.length });
				applyErrorAttributes(err);
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(err));
				return configurationStatus;
			}
			// Try to get the default install directory from the optional attributes
			installDir = attributesMap.get(INSTALL_DIR_ATTRIBUTE);
			if (installDir == null)
			{
				installDir = PYTHON_DEFAULT_INSTALL_DIR;
			}
			// Start the installation...
			configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
			IStatus status = download(urls, progressMonitor);
			if (status.isOK())
			{
				status = install(progressMonitor);
			}
			switch (status.getSeverity())
			{
				case IStatus.OK:
				case IStatus.INFO:
				case IStatus.WARNING:
					displayMessageInUIThread(MessageDialog.INFORMATION,
							NLS.bind(Messages.InstallProcessor_installerTitle, PYTHON),
							NLS.bind(Messages.InstallProcessor_installationSuccessful, PYTHON));
					configurationStatus.setStatus(ConfigurationStatus.OK);
					break;
				case IStatus.ERROR:
					configurationStatus.setStatus(ConfigurationStatus.ERROR);
					break;
				case IStatus.CANCEL:
					configurationStatus.setStatus(ConfigurationStatus.INCOMPLETE);
					break;
				default:
					configurationStatus.setStatus(ConfigurationStatus.UNKNOWN);
			}
			return configurationStatus;
		}
		finally
		{
			synchronized (this.getClass())
			{
				installationInProgress = false;
			}
		}
	}

	/**
	 * Returns the application name.
	 * 
	 * @return "PYTHON"
	 */
	protected String getApplicationName()
	{
		return PYTHON;
	}

	/**
	 * Do the PYTHON installation.
	 * 
	 * @param progressMonitor
	 * @return A status indication of the process success or failure.
	 */
	protected IStatus install(IProgressMonitor progressMonitor)
	{
		if (CollectionsUtil.isEmpty(downloadedPaths))
		{
			String failureMessge = Messages.InstallProcessor_couldNotLocateInstaller;
			String err = NLS.bind(Messages.InstallProcessor_failedToInstall, PYTHON);
			displayMessageInUIThread(MessageDialog.ERROR, Messages.InstallProcessor_installationErrorTitle, err + ' '
					+ failureMessge);
			return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, err + ' ' + failureMessge);
		}
		SubMonitor subMonitor = SubMonitor.convert(progressMonitor, Messages.InstallProcessor_installerProgressInfo,
				IProgressMonitor.UNKNOWN);
		final Map<String, Object> installationAttributes = new HashMap<String, Object>();
		try
		{
			subMonitor.beginTask(NLS.bind(Messages.InstallProcessor_installingTaskName, PYTHON),
					IProgressMonitor.UNKNOWN);
			final String[] installDir = new String[1];
			Job installPythonDialog = new UIJob("Python installer options") //$NON-NLS-1$
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					PythonInstallerOptionsDialog dialog = new PythonInstallerOptionsDialog();
					if (dialog.open() == Window.OK)
					{
						installationAttributes.putAll(dialog.getAttributes());
						return Status.OK_STATUS;
					}
					return Status.CANCEL_STATUS;
				}
			};
			installPythonDialog.schedule();
			try
			{
				installPythonDialog.join();
			}
			catch (InterruptedException e)
			{
			}
			IStatus result = installPythonDialog.getResult();
			if (!result.isOK())
			{
				return result;
			}

			IStatus status = installPYTHON(installationAttributes);
			if (!status.isOK())
			{
				return status;
			}
			IdeLog.logInfo(PortalUIPlugin.getDefault(), MessageFormat.format(
					"Successfully installed PYTHON into {0}. PYTHON installation completed.", installDir[0])); //$NON-NLS-1$
			// note that we called the finalizeInstallation from the installPYTHON Job.
			return Status.OK_STATUS;
		}
		catch (Exception e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), "Error while installing PYTHON", e); //$NON-NLS-1$
			return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
					Messages.InstallProcessor_errorWhileInstalling, PYTHON));
		}
		finally
		{
			subMonitor.done();
		}
	}

	/**
	 * Run the PYTHON installer and install XMAPP into the given directory.
	 * 
	 * @param installationAttributes
	 *            - Attributes map that contains the installation directory and a specification whether to run the
	 *            PYTHON auto-install script.
	 * @return The status of this installation
	 */
	protected IStatus installPYTHON(final Map<String, Object> installationAttributes)
	{
		Job job = new Job(NLS.bind(Messages.InstallProcessor_installerJobName, PYTHON + ' '
				+ Messages.InstallProcessor_installerGroupTitle))
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					// extract the values from the attributes:
					String installDir = (String) installationAttributes.get(InstallerOptionsDialog.INSTALL_DIR_ATTR);
					// This installer requires Windows path slashes style (backslashes)
					installDir = installDir.replaceAll("/", "\\\\"); //$NON-NLS-1$ //$NON-NLS-2$

					SubMonitor subMonitor = SubMonitor.convert(monitor, IProgressMonitor.UNKNOWN);
					subMonitor.beginTask(NLS.bind(Messages.InstallProcessor_installingTaskName, PYTHON),
							IProgressMonitor.UNKNOWN);
					IdeLog.logInfo(PortalUIPlugin.getDefault(), "Installing Python into " + installDir); //$NON-NLS-1$

					// Try to get a file lock first, before running the process. This file was just downloaded, so there
					// is a chance it's still being held by the OS or by the downloader.
					IStatus fileLockStatus = LockUtils.waitForLockRelease(downloadedPaths.get(0).toOSString(), 10000L);
					if (!fileLockStatus.isOK())
					{
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
								Messages.InstallProcessor_failedToInstallSeeLog, PYTHON));
					}
					// Run the Python installer, as specified in this link:
					// http://www.python.org/download/releases/2.5/msi/
					List<String> command = new ArrayList<String>(4);
					command.add("msiexec"); //$NON-NLS-1$
					command.add("/i"); //$NON-NLS-1$
					command.add(downloadedPaths.get(0).toOSString());
					command.add("/qr"); //$NON-NLS-1$
					command.add("TARGETDIR=\"" + installDir + '\"'); //$NON-NLS-1$
					if (Boolean.FALSE.toString().equals(attributesMap.get(INSTALL_FOR_ALL_USERS_ATTR)))
					{
						command.add("ALLUSERS=0"); //$NON-NLS-1$
					}
					else
					{
						command.add("ALLUSERS=1"); //$NON-NLS-1$
					}
					ProcessBuilder processBuilder = new ProcessBuilder(command);
					Process process = processBuilder.start();
					int res = process.waitFor();
					if (res == PYTHON_INSTALLER_PROCESS_CANCEL_CODE)
					{
						IdeLog.logInfo(PortalUIPlugin.getDefault(), "Python installation cancelled"); //$NON-NLS-1$
						return Status.CANCEL_STATUS;
					}
					if (res != 0)
					{
						// We had an error while installing
						IdeLog.logError(
								PortalUIPlugin.getDefault(),
								"Failed to install Python. The PYTHON installer process returned a termination code of " + res); //$NON-NLS-1$
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, res, NLS.bind(
								Messages.InstallProcessor_installationErrorMessage, PYTHON, PYTHON), null);
					}
					else if (!new File(installDir).exists())
					{
						// Just to be sure that we got everything in place
						IdeLog.logError(
								PortalUIPlugin.getDefault(),
								"Failed to install Python. The " + installDir + " directory was not created", (Throwable) null); //$NON-NLS-1$ //$NON-NLS-2$
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, res, NLS.bind(
								Messages.InstallProcessor_installationError_installDirMissing, PYTHON), null);
					}

					finalizeInstallation(installDir);
					return Status.OK_STATUS;
				}
				catch (Exception e)
				{
					IdeLog.logError(PortalUIPlugin.getDefault(), e.getMessage(), e);
					return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
							Messages.InstallProcessor_failedToInstallSeeLog, PYTHON), e);
				}
				finally
				{
					monitor.done();
				}
			}
		};
		// Give it a little delay, just in case the downloader still holds on to the installer file.
		job.schedule(1000);
		try
		{
			job.join();
		}
		catch (InterruptedException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e.getMessage(), e);
			return Status.CANCEL_STATUS;
		}
		return job.getResult();
	}

	/**
	 * Finalize the installation by placing a .aptana file in the installed directory, specifying some properties.
	 * 
	 * @param installDir
	 */
	protected void finalizeInstallation(String installDir)
	{
		super.finalizeInstallation(installDir);
		File propertiesFile = new File(installDir, APTANA_PROPERTIES_FILE_NAME);
		Properties properties = new Properties();
		properties.put("PYTHON_install", urls[0]); //$NON-NLS-1$
		FileOutputStream fileOutputStream = null;
		try
		{
			fileOutputStream = new FileOutputStream(propertiesFile);
			properties.store(fileOutputStream, NLS.bind(Messages.InstallProcessor_aptanaInstallationComment, PYTHON));
		}
		catch (IOException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
		finally
		{
			if (fileOutputStream != null)
			{
				try
				{
					fileOutputStream.flush();
					fileOutputStream.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}

	private class PythonInstallerOptionsDialog extends InstallerOptionsDialog
	{
		private Button installForAllUsersBt;

		public PythonInstallerOptionsDialog()
		{
			super(Display.getDefault().getActiveShell(), PYTHON);
			setTitleImage(PortalUIPlugin.getDefault().getImageRegistry().get(PortalUIPlugin.PYTHON_IMAGE));
		}

		@Override
		protected void setAttributes()
		{
			attributes.put(INSTALL_DIR_ATTR, installDir);
			attributes.put(INSTALL_FOR_ALL_USERS_ATTR, Boolean.TRUE);
		}

		/**
		 * Add the 'Auto-Setup' checkbox.
		 */
		@Override
		protected Composite createInstallerGroupControls(Composite group)
		{
			Composite control = super.createInstallerGroupControls(group);
			installForAllUsersBt = new Button(group, SWT.CHECK);
			installForAllUsersBt.setText(Messages.InstallProcessor_InstallForAllUsers);
			installForAllUsersBt.setSelection(true);
			installForAllUsersBt.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					attributes.put(INSTALL_FOR_ALL_USERS_ATTR, installForAllUsersBt.getSelection());
				}
			});
			return control;
		}
	}
}