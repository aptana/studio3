/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.logging.IdeLog;
import com.aptana.ide.core.io.LockUtils;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.configurationProcessors.installer.InstallerOptionsDialog;

/**
 * A Ruby install processor.<br>
 * This class is in charge of downloading and installing Ruby and DevKit for Windows operating systems.<br>
 * Note: In case we decide to support something similar for MacOSX and Linux, this processor would probably need
 * delegators set up.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class RubyInstallProcessor extends InstallerConfigurationProcessor
{
	private static final String RUBY = "Ruby"; //$NON-NLS-1$
	private static final String DEVKIT = "DevKit"; //$NON-NLS-1$
	protected static final String RUBY_DEFAULT_INSTALL_DIR = "C:\\Ruby"; //$NON-NLS-1$
	protected static final String DEVKIT_FSTAB_PATH = "\\devkit\\msys\\1.0.11\\etc\\fstab"; //$NON-NLS-1$
	protected static final String DEVKIT_FSTAB_LOCATION_PREFIX = "C:/Ruby/"; //$NON-NLS-1$
	// The process return code for a Ruby installer cancel.
	private static final int RUBY_INSTALLER_PROCESS_CANCEL = 5;
	private static boolean installationInProgress;

	private String installDir;

	/**
	 * Install Ruby on the machine.<br>
	 * The configuration will grab the installer and the DevKit from the given attributes.<br>
	 * We expect an array of attributes with the same structure described at {@link #loadAttributes(Object)}.
	 * 
	 * @param attributes
	 *            An array of strings holding an optional attributes map and an array of rubyinstaller and the devkit
	 *            URLs.
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
			String err = "The Ruby installer processor is designed to work on Windows."; //$NON-NLS-1$
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

			// Check that we got the expected two install URLs
			// TODO - Once we place DevKit back again, this should hold a value of 2 URLs.
			if (urls.length != 1)
			{
				// structure error
				String err = NLS.bind(Messages.InstallProcessor_wrongNumberOfInstallLinks, new Object[] { RUBY, 1,
						urls.length });
				applyErrorAttributes(err);
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(err));
				return configurationStatus;
			}
			// Try to get the default install directory from the optional attributes
			installDir = attributesMap.get(INSTALL_DIR_ATTRIBUTE);
			if (installDir == null)
			{
				installDir = RUBY_DEFAULT_INSTALL_DIR;
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
							NLS.bind(Messages.InstallProcessor_installerTitle, RUBY),
							NLS.bind(Messages.InstallProcessor_installationSuccessful, RUBY));
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
	 * @return "Ruby"
	 */
	protected String getApplicationName()
	{
		return RUBY;
	}

	/**
	 * Install Ruby and DevKit.
	 * 
	 * @param progressMonitor
	 * @return
	 */
	protected IStatus install(IProgressMonitor progressMonitor)
	{
		if (downloadedPaths == null || downloadedPaths[0] == null)
		{
			String failureMessge = Messages.InstallProcessor_couldNotLocateInstaller;
			if (downloadedPaths != null && downloadedPaths[0] != null)
			{
				failureMessge = NLS.bind(Messages.InstallProcessor_couldNotLocatePackage, DEVKIT);
			}
			String err = NLS.bind(Messages.InstallProcessor_failedToInstall, RUBY);
			displayMessageInUIThread(MessageDialog.ERROR, Messages.InstallProcessor_installationErrorTitle, err + ' '
					+ failureMessge);
			return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, err + ' ' + failureMessge);
		}
		SubMonitor subMonitor = SubMonitor.convert(progressMonitor, Messages.InstallProcessor_installerProgressInfo,
				IProgressMonitor.UNKNOWN);
		try
		{
			subMonitor
					.beginTask(NLS.bind(Messages.InstallProcessor_installingTaskName, RUBY), IProgressMonitor.UNKNOWN);
			final String[] installDir = new String[1];
			Job installRubyDialog = new UIJob("Ruby installer options") //$NON-NLS-1$
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					RubyInstallerOptionsDialog dialog = new RubyInstallerOptionsDialog();
					if (dialog.open() == Window.OK)
					{
						installDir[0] = dialog.getInstallDir();
						return Status.OK_STATUS;
					}
					else
					{
						return Status.CANCEL_STATUS;
					}
				}
			};
			installRubyDialog.schedule();
			try
			{
				installRubyDialog.join();
			}
			catch (InterruptedException e)
			{
			}
			IStatus result = installRubyDialog.getResult();
			if (!result.isOK())
			{
				return result;
			}

			IStatus status = installRuby(installDir[0]);
			if (!status.isOK())
			{
				return status;
			}
			IdeLog.logInfo(PortalUIPlugin.getDefault(), "Successfully installed Ruby into " + installDir[0]); //$NON-NLS-1$
			// Ruby was installed successfully. Now we need to extract DevKit into the Ruby dir and change its
			// configurations to match the installation location.

			// TODO - We need to fix the DevKit installation. The DevKit team changed their installation way recently...

			// status = installDevKit(installDir[0]);
			// if (!status.isOK())
			// {
			// displayMessageInUIThread(MessageDialog.ERROR, Messages.InstallProcessor_installationErrorTitle, status
			// .getMessage());
			// return status;
			// }
			finalizeInstallation(installDir[0]);
			// PortalUIPlugin.logInfo(
			//					"Successfully installed DevKit into " + installDir[0] + ". Ruby installation completed.", null); //$NON-NLS-1$ //$NON-NLS-2$
			return Status.OK_STATUS;
		}
		catch (Exception e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), "Error while installing Ruby", e); //$NON-NLS-1$
			return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
					Messages.InstallProcessor_errorWhileInstalling, RUBY));
		}
		finally
		{
			subMonitor.done();
		}
	}

	/**
	 * Run the Ruby installer and install Ruby into the given directory.
	 * 
	 * @param installDir
	 * @return The status of this installation
	 */
	protected IStatus installRuby(final String installDir)
	{
		Job job = new Job(NLS.bind(Messages.InstallProcessor_installerJobName, RUBY + ' '
				+ Messages.InstallProcessor_installerGroupTitle))
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					SubMonitor subMonitor = SubMonitor.convert(monitor, IProgressMonitor.UNKNOWN);
					subMonitor.beginTask(NLS.bind(Messages.InstallProcessor_installingTaskName, RUBY),
							IProgressMonitor.UNKNOWN);
					IdeLog.logInfo(PortalUIPlugin.getDefault(), "Installing Ruby into " + installDir); //$NON-NLS-1$

					// Try to get a file lock first, before running the process. This file was just downloaded, so there
					// is a chance it's still being held by the OS or by the downloader.
					IStatus fileLockStatus = LockUtils.waitForLockRelease(downloadedPaths[0], 10000L);
					if (!fileLockStatus.isOK())
					{
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
								Messages.InstallProcessor_failedToInstallSeeLog, RUBY));
					}

					ProcessBuilder processBuilder = new ProcessBuilder(downloadedPaths[0],
							"/silent", "/dir=\"" + installDir + "\"", "/tasks=\"modpath\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					Process process = processBuilder.start();
					int res = process.waitFor();
					if (res == RUBY_INSTALLER_PROCESS_CANCEL)
					{
						IdeLog.logInfo(PortalUIPlugin.getDefault(), "Ruby installation cancelled"); //$NON-NLS-1$
						return Status.CANCEL_STATUS;
					}
					if (res != 0)
					{
						// We had an error while installing
						IdeLog.logError(
								PortalUIPlugin.getDefault(),
								"Failed to install Ruby. The ruby installer process returned a termination code of " + res); //$NON-NLS-1$
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, res, NLS.bind(
								Messages.InstallProcessor_installationErrorMessage, RUBY, RUBY), null);
					}
					else if (!new File(installDir).exists())
					{
						// Just to be sure that we got everything in place
						IdeLog.logError(PortalUIPlugin.getDefault(),
								"Failed to install Ruby. The " + installDir + " directory was not created"); //$NON-NLS-1$ //$NON-NLS-2$
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, res, NLS.bind(
								Messages.InstallProcessor_installationError_installDirMissing, RUBY), null);
					}
					return Status.OK_STATUS;
				}
				catch (Exception e)
				{
					IdeLog.logError(PortalUIPlugin.getDefault(), e);
					return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
							Messages.InstallProcessor_failedToInstallSeeLog, RUBY), e);
				}
				finally
				{
					monitor.done();
				}
			}
		};
		// Give it a little delay, just in case the downloader still holds on to the rubyinstaller file.
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
	 * Extract the downloaded DevKit into the install dir and configure it to work.<br>
	 * At this stage, we assume that the install dir and the DevKit package have been verified and valid!
	 * 
	 * @param dir
	 * @return The result status of the installation
	 * @throws Exception
	 */
	protected IStatus installDevKit(String dir)
	{
		final String installDir = dir + File.separatorChar + "DevKit"; //$NON-NLS-1$
		Job job = new Job(NLS.bind(Messages.InstallProcessor_installingTaskName, DEVKIT))
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					SubMonitor subMonitor = SubMonitor.convert(monitor, 1000);
					subMonitor.beginTask(
							NLS.bind(Messages.InstallProcessor_extractingPackageTaskName, DEVKIT, installDir), 900);
					// We get a folder status first, before unzipping into the folder. This folder was just created,
					// so there is a chance it's still being held by the OS or by the Ruby installer.
					IStatus folderStatus = LockUtils.waitForFolderAccess(installDir, 10000);
					if (!folderStatus.isOK())
					{
						PortalUIPlugin.getDefault().getLog().log(folderStatus);
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
								Messages.InstallProcessor_failedToInstallSeeLog, DEVKIT));
					}
					// DevKit arrives as a 7zip package, so we use a specific Windows decoder to extract it.
					// This extraction process follows the instructions at:
					// http://wiki.github.com/oneclick/rubyinstaller/development-kit
					extractWin(downloadedPaths[1], installDir);
					subMonitor.worked(900);

					subMonitor.beginTask(NLS.bind(Messages.InstallProcessor_updatingTaskName, DEVKIT), 100);
					// Update the /devkit/msys/1.0.11/etc/fstab with the Ruby installation path
					File fstab = new File(installDir, DEVKIT_FSTAB_PATH);
					StringBuilder builder = new StringBuilder();
					// read the content of the original file and update the Ruby location to the selected one. Then,
					// save the new content and override the file.
					String pathReplacement = installDir.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
					if (!pathReplacement.endsWith("/")) //$NON-NLS-1$
					{
						pathReplacement = pathReplacement + '/';
					}
					String newLine = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
					BufferedReader reader = new BufferedReader(new FileReader(fstab));
					String line = null;
					while ((line = reader.readLine()) != null)
					{
						line = line.replaceAll(DEVKIT_FSTAB_LOCATION_PREFIX, pathReplacement);
						builder.append(line);
						builder.append(newLine);
					}
					reader.close();
					// Now save the modified content into the same file
					FileWriter writer = new FileWriter(fstab);
					writer.write(builder.toString());
					writer.flush();
					writer.close();
					subMonitor.worked(100);
				}
				catch (Throwable t)
				{
					IdeLog.logError(PortalUIPlugin.getDefault(), "Failed to install DevKit", t); //$NON-NLS-1$
					return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
							Messages.InstallProcessor_failedToInstallSeeLog, DEVKIT), t);
				}
				finally
				{
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule(500);
		try
		{
			job.join();
		}
		catch (InterruptedException e)
		{
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
		properties.put("ruby_install", urls[0]); //$NON-NLS-1$
		// TODO - Uncomment this once we have DevKit back
		// properties.put("devkit_install", urls[1]); //$NON-NLS-1$
		FileOutputStream fileOutputStream = null;
		try
		{
			fileOutputStream = new FileOutputStream(propertiesFile);
			properties.store(fileOutputStream,
					NLS.bind(Messages.InstallProcessor_aptanaInstallationComment, "Ruby & DevKit")); //$NON-NLS-1$
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

	/**
	 * Ruby installer dialog.<br>
	 * This dialog only asks for the install directory.
	 * 
	 * @author Shalom Gibly <sgibly@aptana.com>
	 */
	private class RubyInstallerOptionsDialog extends InstallerOptionsDialog
	{
		public RubyInstallerOptionsDialog()
		{
			super(Display.getDefault().getActiveShell(), RUBY);
			setTitleImage(PortalUIPlugin.getDefault().getImageRegistry().get(PortalUIPlugin.RUBY_IMAGE));
		}

		/**
		 * Returns the installation dir selected in the text field.
		 * 
		 * @return the installation directory
		 */
		public String getInstallDir()
		{
			return attributes.get(INSTALL_DIR_ATTR).toString();
		}

		@Override
		protected void setAttributes()
		{
			attributes.put(INSTALL_DIR_ATTR, installDir);
		}
	}
}
