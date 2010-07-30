package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.ide.core.io.LockUtils;
import com.aptana.portal.ui.PortalUIPlugin;

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
	private static final String XAMPP = "XAMPP"; //$NON-NLS-1$
	protected static final String XAMPP_DEFAULT_INSTALL_PATH = "C:\\XAMPP"; //$NON-NLS-1$
	private static boolean installationInProgress;

	/**
	 * Install XAMPP on the machine.<br>
	 * The configuration will grab the installer from the given attributes. We expects a single attribute which contains
	 * the XAMPP installer link (.exe file)
	 * 
	 * @param attributes
	 *            An string array of size 1, which contains the URL for the XAMPP installer (.exe).
	 * @see com.aptana.configurations.processor.AbstractConfigurationProcessor#configure(org.eclipse.core.runtime.IProgressMonitor,
	 *      java.lang.Object)
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
			PortalUIPlugin.logError(err, new Exception());
			applyErrorAttributes(err);
			return configurationStatus;
		}
		try
		{
			configurationStatus.removeAttribute(CONFIG_ATTR);
			clearErrorAttributes();
			if (attributes == null || !(attributes instanceof Object[]))
			{
				String err = NLS.bind(Messages.InstallProcessor_missingInstallURLs, XAMPP);
				applyErrorAttributes(err);
				PortalUIPlugin.logError(new Exception(err));
				return configurationStatus;
			}
			Object[] attrArray = (Object[]) attributes;
			if (attrArray.length != 2)
			{
				// structure error
				String err = NLS.bind(Messages.InstallProcessor_wrongNumberOfInstallLinks, new Object[] { XAMPP, 1,
						attrArray.length });
				applyErrorAttributes(err);
				PortalUIPlugin.logError(new Exception(err));
				return configurationStatus;
			}

			// Start the installation...
			configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
			IStatus status = download(attrArray, progressMonitor);
			if (status.isOK())
			{
				status = install(progressMonitor);
			}
			switch (status.getCode())
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

	protected IStatus install(IProgressMonitor progressMonitor)
	{
		if (downloadedPaths == null || downloadedPaths[0] == null || downloadedPaths[1] == null)
		{
			String failureMessge = Messages.InstallProcessor_couldNotLocateInstaller;
			String err = NLS.bind(Messages.InstallProcessor_failedToInstall, XAMPP);
			displayMessageInUIThread(MessageDialog.ERROR, Messages.InstallProcessor_installationErrorTitle, err + ' '
					+ failureMessge);
			return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, err + ' ' + failureMessge);
		}
		SubMonitor subMonitor = SubMonitor.convert(progressMonitor, Messages.InstallProcessor_installerProgressInfo,
				IProgressMonitor.UNKNOWN);
		try
		{
			subMonitor.beginTask(NLS.bind(Messages.InstallProcessor_installingTaskName, XAMPP),
					IProgressMonitor.UNKNOWN);
			final String[] installDir = new String[1];
			Job installRubyDialog = new UIJob("Ruby installer options") //$NON-NLS-1$
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					XAMPPInstallerOptionsDialog dialog = new XAMPPInstallerOptionsDialog();
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

			IStatus status = installXAMPP(installDir[0]);
			if (!status.isOK())
			{
				if (status.getSeverity() != IStatus.CANCEL)
				{
					displayMessageInUIThread(MessageDialog.ERROR, Messages.InstallProcessor_installationErrorTitle,
							status.getMessage());
				}
				return status;
			}
			finalizeInstallation(installDir[0]);
			PortalUIPlugin.logInfo(
					"Successfully installed XAMPP into " + installDir[0] + ". XAMPP installation completed.", null); //$NON-NLS-1$ //$NON-NLS-2$
			return Status.OK_STATUS;
		}
		catch (Exception e)
		{
			PortalUIPlugin.logError("Error while installing XAMPP", e); //$NON-NLS-1$
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
	 * @param installDir
	 * @return The status of this installation
	 */
	protected IStatus installXAMPP(final String installDir)
	{
		Job job = new Job(NLS.bind(Messages.InstallProcessor_installerJobName, XAMPP + ' '
				+ Messages.InstallProcessor_installerGroupTitle))
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					SubMonitor subMonitor = SubMonitor.convert(monitor, IProgressMonitor.UNKNOWN);
					subMonitor.beginTask(NLS.bind(Messages.InstallProcessor_installingTaskName, XAMPP),
							IProgressMonitor.UNKNOWN);
					PortalUIPlugin.logInfo("Installing XAMPP into " + installDir, null); //$NON-NLS-1$

					// Try to get a file lock first, before running the process. This file was just downloaded, so there
					// is a chance it's still being held by the OS or by the downloader.
					IStatus fileLockStatus = LockUtils.waitForLockRelease(downloadedPaths[0], 10000L);
					if (!fileLockStatus.isOK())
					{
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
								Messages.InstallProcessor_failedToInstallSeeLog, XAMPP));
					}
					// TODO - Replace this with the instructions specified here:
					// http://www.apachefriends.org/en/xampp-windows.html#522
					ProcessBuilder processBuilder = new ProcessBuilder(downloadedPaths[0],
							"/silent", "/dir=\"" + installDir + "\"", "/tasks=\"modpath\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					Process process = processBuilder.start();
					int res = process.waitFor();
					if (res == 5)
					{
						PortalUIPlugin.logInfo("XAMPP installation cancelled", null); //$NON-NLS-1$
						return Status.CANCEL_STATUS;
					}
					if (res != 0)
					{
						// We had an error while installing
						PortalUIPlugin
								.logError(
										"Failed to install XAMPP. The XAMPP installer process returned a termination code of " + res, null); //$NON-NLS-1$
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, res, NLS.bind(
								Messages.InstallProcessor_installationErrorMessage, XAMPP, XAMPP), null);
					}
					else if (!new File(installDir).exists())
					{
						// Just to be sure that we got everything in place
						PortalUIPlugin.logError(
								"Failed to install XAMPP. The " + installDir + " directory was not created", null); //$NON-NLS-1$ //$NON-NLS-2$
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, res, NLS.bind(
								Messages.InstallProcessor_installationError_installDirMissing, XAMPP), null);
					}
					return Status.OK_STATUS;
				}
				catch (Exception e)
				{
					PortalUIPlugin.logError(e);
					return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, NLS.bind(
							Messages.InstallProcessor_failedToInstallSeeLog, XAMPP), e);
				}
				finally
				{
					monitor.done();
				}
			}
		};
		// Give it a little delay, just in case the downloader still holds on to the rubyinstaller file.
		job.schedule(3000);
		try
		{
			job.join();
		}
		catch (InterruptedException e)
		{
			PortalUIPlugin.logError(e);
			return Status.CANCEL_STATUS;
		}
		return job.getResult();
	}

	private class XAMPPInstallerOptionsDialog extends TitleAreaDialog
	{
		private Text path;
		private String installDir;

		public XAMPPInstallerOptionsDialog()
		{
			super(Display.getDefault().getActiveShell());
			setTitleImage(PortalUIPlugin.getDefault().getImageRegistry().get(PortalUIPlugin.RUBY));
			setBlockOnOpen(true);
			setHelpAvailable(false);
			installDir = XAMPP_DEFAULT_INSTALL_PATH;
		}

		@Override
		protected void configureShell(Shell newShell)
		{
			super.configureShell(newShell);
			newShell.setText(Messages.InstallProcessor_installerShellTitle);
		}

		/**
		 * Returns the installation dir selected in the text field.
		 * 
		 * @return the installation directory
		 */
		public String getInstallDir()
		{
			return installDir;
		}

		@Override
		protected Control createDialogArea(Composite parent)
		{
			Composite composite = (Composite) super.createDialogArea(parent);
			// Create a inner composite so we can control the margins
			Composite inner = new Composite(composite, SWT.NONE);
			inner.setLayoutData(new GridData(GridData.FILL_BOTH));
			GridLayout layout = new GridLayout();
			layout.marginLeft = 4;
			layout.marginRight = 4;
			layout.marginTop = 4;
			layout.marginBottom = 4;
			inner.setLayout(layout);

			Group group = new Group(inner, SWT.NONE);
			group.setText(Messages.InstallProcessor_installerGroupTitle);
			group.setLayout(new GridLayout());
			GridData layoutData = new GridData(GridData.FILL_BOTH);
			group.setLayoutData(layoutData);

			Label l = new Label(group, SWT.WRAP);
			l.setText(NLS.bind(Messages.InstallProcessor_installerMessage, XAMPP));
			Composite installLocation = new Composite(group, SWT.NONE);
			installLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			installLocation.setLayout(new GridLayout(2, false));
			path = new Text(installLocation, SWT.SINGLE | SWT.BORDER);
			path.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			path.setText(installDir);
			path.addKeyListener(new KeyListener()
			{
				@Override
				public void keyReleased(org.eclipse.swt.events.KeyEvent e)
				{
					installDir = path.getText();
				}

				@Override
				public void keyPressed(org.eclipse.swt.events.KeyEvent e)
				{
					installDir = path.getText();
				}
			});
			Button browse = new Button(installLocation, SWT.PUSH);
			browse.setText(Messages.InstallProcessor_browse);
			browse.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					DirectoryDialog dirDialog = new DirectoryDialog(getParentShell());
					String dir = dirDialog.open();
					if (dir != null)
					{
						path.setText(dir);
						installDir = dir;
					}
				}
			});
			setTitle(NLS.bind(Messages.InstallProcessor_installerTitle, XAMPP));
			return composite;
		}
	}
}
