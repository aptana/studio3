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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
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
 * A XAMPP install processor.<br>
 * This class is in charge of downloading and installing XAMPP for Windows operating systems.<br>
 * Note: In case we decide to support something similar for MacOSX and Linux, this processor would probably need
 * delegators set up.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class XAMPPInstallProcessor extends InstallerConfigurationProcessor
{
	protected static final String XAMPP_DEFAULT_INSTALL_DIR = "C:\\"; //$NON-NLS-1$
	protected static final String EXECUTE_SETUP_SCRIPT_ATTR = "execute_setup_script"; //$NON-NLS-1$
	private static final String XAMPP_DEFAULT_FOLDER = "xampp\\"; //$NON-NLS-1$
	private static final String XAMPP_CONTROL = "xampp-control.exe"; //$NON-NLS-1$
	private static final String XAMPP = "XAMPP"; //$NON-NLS-1$
	protected static final int XAMPP_INSTALLER_PROCESS_CANCEL_CODE = 255;
	private static boolean installationInProgress;
	private String installDir;

	/**
	 * Install XAMPP on the machine.<br>
	 * The configuration will grab the installer from the given attributes.<br>
	 * We expect an array of attributes with the same structure described at {@link #loadAttributes(Object)}.
	 * 
	 * @param attributes
	 *            First - A string array of size 1, which contains the URL for the XAMPP installer (.exe). Second -
	 *            (optional) map of additional attributes.
	 * @see com.aptana.configurations.processor.AbstractConfigurationProcessor#configure(org.eclipse.core.runtime.IProgressMonitor,
	 *      java.lang.Object)
	 * @see #loadAttributes(Object)
	 */
	@Override
	public ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes)
	{
		// Get a Class lock to avoid multiple installations at the same time even with multiple instances of this
		// RubyInstallProcessor
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
			String err = "The XAMPP installer processor is designed to work on Windows."; //$NON-NLS-1$
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
				String err = NLS.bind(Messages.InstallProcessor_wrongNumberOfInstallLinks, new Object[] { XAMPP, 1,
						urls.length });
				applyErrorAttributes(err);
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(err));
				return configurationStatus;
			}
			// Try to get the default install directory from the optional attributes
			installDir = attributesMap.get(INSTALL_DIR_ATTRIBUTE);
			if (installDir == null)
			{
				installDir = XAMPP_DEFAULT_INSTALL_DIR;
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
							NLS.bind(Messages.InstallProcessor_installerTitle, XAMPP),
							NLS.bind(Messages.InstallProcessor_installationSuccessful, XAMPP));
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
	 * @return "XAMPP"
	 */
	protected String getApplicationName()
	{
		return XAMPP;
	}

	/**
	 * Do the XAMPP installation.
	 * 
	 * @param progressMonitor
	 * @return A status indication of the process success or failure.
	 */
	protected IStatus install(IProgressMonitor progressMonitor)
	{
		if (CollectionsUtil.isEmpty(downloadedPaths) || CollectionsUtil.getFirstElement(downloadedPaths) == null)
		{
			String failureMessge = Messages.InstallProcessor_couldNotLocateInstaller;
			String err = NLS.bind(Messages.InstallProcessor_failedToInstall, XAMPP);
			displayMessageInUIThread(MessageDialog.ERROR, Messages.InstallProcessor_installationErrorTitle, err + ' '
					+ failureMessge);
			return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, err + ' ' + failureMessge);
		}
		SubMonitor subMonitor = SubMonitor.convert(progressMonitor, Messages.InstallProcessor_installerProgressInfo,
				IProgressMonitor.UNKNOWN);
		final Map<String, Object> installationAttributes = new HashMap<String, Object>();
		try
		{
			subMonitor.beginTask(NLS.bind(Messages.InstallProcessor_installingTaskName, XAMPP),
					IProgressMonitor.UNKNOWN);
			final String[] installDir = new String[1];
			Job installXAMPPDialog = new UIJob("XAMPP installer options") //$NON-NLS-1$
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					XAMPPInstallerOptionsDialog dialog = new XAMPPInstallerOptionsDialog();
					if (dialog.open() == Window.OK)
					{
						installationAttributes.putAll(dialog.getAttributes());
						return Status.OK_STATUS;
					}
					return Status.CANCEL_STATUS;
				}
			};
			installXAMPPDialog.schedule();
			try
			{
				installXAMPPDialog.join();
			}
			catch (InterruptedException e)
			{
			}
			IStatus result = installXAMPPDialog.getResult();
			if (!result.isOK())
			{
				return result;
			}

			IStatus status = installXAMPP(installationAttributes);
			if (!status.isOK())
			{
				return status;
			}
			IdeLog.logInfo(PortalUIPlugin.getDefault(),
					"Successfully installed XAMPP into " + installDir[0] + ". XAMPP installation completed."); //$NON-NLS-1$ //$NON-NLS-2$
			// note that we called the finalizeInstallation from the installXAMPP Job.
			return Status.OK_STATUS;
		}
		catch (Exception e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), "Error while installing XAMPP", e); //$NON-NLS-1$
			return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
					Messages.InstallProcessor_errorWhileInstalling, XAMPP));
		}
		finally
		{
			subMonitor.done();
		}
	}

	/**
	 * Run the XAMPP installer and install XMAPP into the given directory.
	 * 
	 * @param installationAttributes
	 *            - Attributes map that contains the installation directory and a specification whether to run the XAMPP
	 *            auto-install script.
	 * @return The status of this installation
	 */
	protected IStatus installXAMPP(final Map<String, Object> installationAttributes)
	{
		Job job = new Job(NLS.bind(Messages.InstallProcessor_installerJobName, XAMPP + ' '
				+ Messages.InstallProcessor_installerGroupTitle))
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					// extract the values from the attributes:
					String installDir = (String) installationAttributes.get(InstallerOptionsDialog.INSTALL_DIR_ATTR);
					boolean runAutoInstallScript = (Boolean) installationAttributes.get(EXECUTE_SETUP_SCRIPT_ATTR);

					SubMonitor subMonitor = SubMonitor.convert(monitor, IProgressMonitor.UNKNOWN);
					subMonitor.beginTask(NLS.bind(Messages.InstallProcessor_installingTaskName, XAMPP),
							IProgressMonitor.UNKNOWN);
					IdeLog.logInfo(PortalUIPlugin.getDefault(), "Installing XAMPP into " + installDir); //$NON-NLS-1$

					// Try to get a file lock first, before running the process. This file was just downloaded, so there
					// is a chance it's still being held by the OS or by the downloader.
					IPath downloadPath = downloadedPaths.get(0);
					IStatus fileLockStatus = LockUtils.waitForLockRelease(downloadPath.toOSString(), 10000L);
					if (!fileLockStatus.isOK())
					{
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
								Messages.InstallProcessor_failedToInstallSeeLog, XAMPP));
					}
					// Run the XAMPP installer, as specified in this link:
					// http://www.apachefriends.org/en/xampp-windows.html#522
					List<String> command = new ArrayList<String>(4);
					command.add(downloadPath.toOSString());
					command.add("-d" + installDir); //$NON-NLS-1$
					command.add("-s2"); //$NON-NLS-1$
					if (runAutoInstallScript)
					{
						command.add("-spauto"); //$NON-NLS-1$
					}
					ProcessBuilder processBuilder = new ProcessBuilder(command);
					Process process = processBuilder.start();
					int res = process.waitFor();
					if (res == XAMPP_INSTALLER_PROCESS_CANCEL_CODE)
					{
						IdeLog.logInfo(PortalUIPlugin.getDefault(), "XAMPP installation cancelled"); //$NON-NLS-1$
						return Status.CANCEL_STATUS;
					}
					if (res != 0)
					{
						// We had an error while installing
						IdeLog.logError(
								PortalUIPlugin.getDefault(),
								"Failed to install XAMPP. The XAMPP installer process returned a termination code of " + res); //$NON-NLS-1$
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, res, NLS.bind(
								Messages.InstallProcessor_installationErrorMessage, XAMPP, XAMPP), null);
					}
					else if (!new File(installDir).exists())
					{
						// Just to be sure that we got everything in place
						IdeLog.logError(PortalUIPlugin.getDefault(),
								"Failed to install XAMPP. The " + installDir + " directory was not created"); //$NON-NLS-1$ //$NON-NLS-2$
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, res, NLS.bind(
								Messages.InstallProcessor_installationError_installDirMissing, XAMPP), null);
					}
					// In case we had the auto-setup script on, open the XAMPP control
					if (runAutoInstallScript)
					{
						openXAMPPConsole(installDir + XAMPP_DEFAULT_FOLDER);
					}
					finalizeInstallation(installDir + XAMPP_DEFAULT_FOLDER);
					return Status.OK_STATUS;
				}
				catch (Exception e)
				{
					IdeLog.logError(PortalUIPlugin.getDefault(), e);
					return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
							Messages.InstallProcessor_failedToInstallSeeLog, XAMPP), e);
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
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
			return Status.CANCEL_STATUS;
		}
		return job.getResult();
	}

	/**
	 * Opens the XAMPP console right after XAMPP was installed.
	 * 
	 * @param installDir
	 */
	protected void openXAMPPConsole(final String installDir)
	{
		Job job = new Job(Messages.XAMPPInstallProcessor_openXAMPPConsoleJobName)
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{

				try
				{
					ProcessBuilder processBuilder = new ProcessBuilder(installDir + XAMPP_CONTROL);
					// We might stumble into 'Access Denied' errors when running this one. So this will try to
					// re-initiate it several times.
					int attempts = 5;
					long timeOut = 3000L;
					Throwable lastException = null;
					do
					{
						if (monitor.isCanceled())
						{
							break;
						}
						try
						{
							processBuilder.start();
							lastException = null;
							break;
						}
						catch (Throwable t)
						{
							attempts--;
							lastException = t;
						}
						Thread.sleep(timeOut);
					}
					while (attempts > 0);
					if (lastException != null)
					{
						IdeLog.logError(PortalUIPlugin.getDefault(), lastException);
					}
				}
				catch (Throwable t)
				{
					// Just log any error here, but don't display any error message
					IdeLog.logError(PortalUIPlugin.getDefault(), t);
				}
				return Status.OK_STATUS;
			}

		};
		job.schedule(3000L);
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
		properties.put("xampp_install", urls[0]); //$NON-NLS-1$
		FileOutputStream fileOutputStream = null;
		try
		{
			fileOutputStream = new FileOutputStream(propertiesFile);
			properties.store(fileOutputStream, NLS.bind(Messages.InstallProcessor_aptanaInstallationComment, XAMPP));
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

	private class XAMPPInstallerOptionsDialog extends InstallerOptionsDialog
	{
		private Button executeScriptBt;

		public XAMPPInstallerOptionsDialog()
		{
			super(Display.getDefault().getActiveShell(), XAMPP);
			setTitleImage(PortalUIPlugin.getDefault().getImageRegistry().get(PortalUIPlugin.XAMPP_IMAGE));
		}

		@Override
		protected void setAttributes()
		{
			attributes.put(INSTALL_DIR_ATTR, installDir);
			attributes.put(EXECUTE_SETUP_SCRIPT_ATTR, Boolean.TRUE);
		}

		/**
		 * Add the 'Auto-Setup' checkbox.
		 */
		@Override
		protected Composite createInstallerGroupControls(Composite group)
		{
			Composite control = super.createInstallerGroupControls(group);
			executeScriptBt = new Button(group, SWT.CHECK);
			executeScriptBt.setText(Messages.XAMPPInstallProcessor_executeXAMPPAutoSetup);
			executeScriptBt.setSelection(true);
			executeScriptBt.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					attributes.put(EXECUTE_SETUP_SCRIPT_ATTR, executeScriptBt.getSelection());
				}
			});
			return control;
		}
	}
}
