package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;

import com.aptana.configurations.processor.AbstractConfigurationProcessor;
import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.ide.core.io.downloader.DownloadManager;
import com.aptana.portal.ui.PortalUIPlugin;

/**
 * A Ruby install processor.<br>
 * This class is in charge of downloading and installing Ruby and DevKit for Windows operating systems.<br>
 * Note: In case we decide to support something similar for MacOSX and Linux, this processor would probably need
 * delegators set up.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class RubyInstallProcessor extends AbstractConfigurationProcessor
{
	protected static String RUBY_DEFAULT_INSTALL_PATH = "C:\\Ruby"; //$NON-NLS-1$
	private static boolean installationInProgress;
	private String[] downloadedPaths;

	/**
	 * Configure Ruby on the machine.<br>
	 * The configuration will grab the installer and the DevKit from the given attributes. We expects the first
	 * attribute to contain the Ruby installer link, and the second attribute to contain the DevKit 7z package.
	 * 
	 * @param attributes
	 *            An array of strings holding the rubyinstaller and the devkit URLs.
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
		try
		{
			configurationStatus.removeAttribute(CONFIG_ATTR);
			clearErrorAttributes();
			if (attributes == null || !(attributes instanceof Object[]))
			{
				applyErrorAttributes(Messages.RubyInstallProcessor_missingRubyInstallURLs);
				PortalUIPlugin.logError(new Exception(Messages.RubyInstallProcessor_missingRubyInstallURLs));
				return configurationStatus;
			}
			Object[] attrArray = (Object[]) attributes;
			if (attrArray.length != 2)
			{
				// structure error
				applyErrorAttributes(Messages.RubyInstallProcessor_wrongNumberOfRubyInstallLinks + attrArray.length);
				PortalUIPlugin.logError(new Exception(Messages.RubyInstallProcessor_wrongNumberOfRubyInstallLinks
						+ attrArray.length));
				return configurationStatus;
			}

			// Start the installation...
			configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
			// TODO - Attache: download(attrArray, progressMonitor);
			install(progressMonitor);
			configurationStatus.setStatus(ConfigurationStatus.OK);
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
	 * Download the remote content and store it the temp directory.
	 * 
	 * @param URLs
	 * @param progressMonitor
	 */
	private void download(Object[] URLs, IProgressMonitor progressMonitor)
	{
		downloadedPaths = null;
		DownloadManager downloadManager = new DownloadManager();
		List<URL> urlsList = new ArrayList<URL>(URLs.length);
		for (Object o : URLs)
		{
			try
			{
				urlsList.add(new URL(o.toString()));
			}
			catch (MalformedURLException mue)
			{
				PortalUIPlugin.logError(mue);
			}
		}
		try
		{
			downloadManager.addURLs(urlsList);
			IStatus status = downloadManager.start(progressMonitor);
			if (status.isOK())
			{
				downloadedPaths = downloadManager.getContentsLocations();
			}
		}
		catch (Exception e)
		{
			PortalUIPlugin.logError(e);
		}
	}

	/**
	 * Install Ruby and DevKit.
	 * 
	 * @param progressMonitor
	 */
	private void install(IProgressMonitor progressMonitor)
	{
		SubMonitor subMonitor = SubMonitor.convert(progressMonitor, "Installing...", IProgressMonitor.UNKNOWN);
		try
		{
			subMonitor.beginTask("Installing Ruby...", IProgressMonitor.UNKNOWN);
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
				return;
			}
			System.out.println("Installing to :" + installDir[0]);
			// ProcessBuilder processBuilder = new ProcessBuilder();
		}
		finally
		{
			subMonitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.AbstractConfigurationProcessor#computeStatus(org.eclipse.core.runtime.
	 * IProgressMonitor, java.lang.Object)
	 */
	@Override
	public ConfigurationStatus computeStatus(IProgressMonitor progressMonitor, Object attributes)
	{
		// This one does nothing. We compute the Ruby status in the generic VersionsConfigurationProcessor
		return configurationStatus;
	}

	private class RubyInstallerOptionsDialog extends TitleAreaDialog
	{
		private Text path;
		private String installDir;

		public RubyInstallerOptionsDialog()
		{
			super(Display.getDefault().getActiveShell());
			setBlockOnOpen(true);
			setHelpAvailable(false);
			installDir = RUBY_DEFAULT_INSTALL_PATH;
		}

		@Override
		protected void configureShell(Shell newShell)
		{
			super.configureShell(newShell);
			newShell.setText("Installer");
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
			group.setText("Installer");
			group.setLayout(new GridLayout());
			GridData layoutData = new GridData(GridData.FILL_BOTH);
			group.setLayoutData(layoutData);
			
			Label l = new Label(group, SWT.WRAP);
			l.setText("Ruby will be installed into the following folder. \nClick ok to install, or select a different folder before you continue.");
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
			browse.setText("Browse");
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
			setTitle("Ruby Installer");
			return composite;
		}
	}
}
