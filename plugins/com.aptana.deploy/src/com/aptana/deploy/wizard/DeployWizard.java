/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.wizard;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.CorePlugin;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.deploy.Activator;
import com.aptana.deploy.internal.wizard.CapifyProjectPage;
import com.aptana.deploy.internal.wizard.DeployWizardPage;
import com.aptana.deploy.internal.wizard.EngineYardDeployWizardPage;
import com.aptana.deploy.internal.wizard.EngineYardSignupPage;
import com.aptana.deploy.internal.wizard.FTPDeployWizardPage;
import com.aptana.deploy.internal.wizard.HerokuDeployWizardPage;
import com.aptana.deploy.internal.wizard.HerokuSignupPage;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.preferences.IPreferenceConstants;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.syncing.ui.actions.BaseSyncAction;
import com.aptana.ide.syncing.ui.actions.DownloadAction;
import com.aptana.ide.syncing.ui.actions.SynchronizeProjectAction;
import com.aptana.ide.syncing.ui.actions.UploadAction;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SyncDirection;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleEntry;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.usage.PingStartup;

@SuppressWarnings("restriction")
public class DeployWizard extends Wizard implements IWorkbenchWizard
{

	private static final String BUNDLE_HEROKU = "Heroku"; //$NON-NLS-1$
	private static final String BUNDLE_ENGINEYARD = "Engine Yard"; //$NON-NLS-1$

	private IProject project;

	@Override
	public boolean performFinish()
	{
		IRunnableWithProgress runnable = null;
		// check what the user chose, then do the heavy lifting, or tell the page to finish...
		IWizardPage currentPage = getContainer().getCurrentPage();
		String pageName = currentPage.getName();
		DeployType type = null;
		String deployEndpointName = null;
		if (HerokuDeployWizardPage.NAME.equals(pageName))
		{
			HerokuDeployWizardPage page = (HerokuDeployWizardPage) currentPage;
			runnable = createHerokuDeployRunnable(page);
			type = DeployType.HEROKU;
			deployEndpointName = page.getAppName();
		}
		else if (FTPDeployWizardPage.NAME.equals(pageName))
		{
			FTPDeployWizardPage page = (FTPDeployWizardPage) currentPage;
			runnable = createFTPDeployRunnable(page);
			type = DeployType.FTP;
			deployEndpointName = page.getConnectionPoint().getName();
		}
		else if (HerokuSignupPage.NAME.equals(pageName))
		{
			HerokuSignupPage page = (HerokuSignupPage) currentPage;
			runnable = createHerokuSignupRunnable(page);
		}
		else if (CapifyProjectPage.NAME.equals(pageName))
		{
			CapifyProjectPage page = (CapifyProjectPage) currentPage;
			runnable = createCapifyRunnable(page);
			type = DeployType.CAPISTRANO;
		}
		else if (EngineYardSignupPage.NAME.equals(pageName))
		{
			EngineYardSignupPage page = (EngineYardSignupPage) currentPage;
			runnable = createEngineYardSignupRunnable(page);
		}
		else if (EngineYardDeployWizardPage.NAME.equals(pageName))
		{
			EngineYardDeployWizardPage page = (EngineYardDeployWizardPage) currentPage;
			runnable = createEngineYardDeployRunnable(page);
			type = DeployType.ENGINEYARD;
		}

		// stores the deploy type and what application or FTP connection it's deploying to
		if (type != null)
		{
			DeployPreferenceUtil.setDeployType(project, type);
			if (deployEndpointName != null)
			{
				DeployPreferenceUtil.setDeployEndpoint(project, deployEndpointName);
			}
		}

		if (runnable != null)
		{
			try
			{
				getContainer().run(true, false, runnable);
			}
			catch (Exception e)
			{
				Activator.logError(e);
			}
		}
		return true;
	}

	protected IRunnableWithProgress createFTPDeployRunnable(FTPDeployWizardPage page)
	{
		if (!page.completePage())
		{
			return null;
		}
		final IConnectionPoint destinationConnectionPoint = page.getConnectionPoint();
		final boolean isAutoSyncSelected = page.isAutoSyncSelected();
		final SyncDirection direction = page.getSyncDirection();
		final IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActivePart();

		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				SubMonitor sub = SubMonitor.convert(monitor, 100);
				try
				{
					ISiteConnection site = null;
					ISiteConnection[] sites = SiteConnectionUtils.findSites(project, destinationConnectionPoint);
					if (sites.length == 0)
					{
						// creates the site to link the project with the FTP connection
						IConnectionPoint sourceConnectionPoint = SyncUtils.findOrCreateConnectionPointFor(project);
						CoreIOPlugin.getConnectionPointManager().addConnectionPoint(sourceConnectionPoint);
						site = SiteConnectionUtils.createSite(MessageFormat.format("{0} <-> {1}", project.getName(), //$NON-NLS-1$
								destinationConnectionPoint.getName()),
								sourceConnectionPoint, destinationConnectionPoint);
						SyncingPlugin.getSiteConnectionManager().addSiteConnection(site);
					}
					else if (sites.length == 1)
					{
						// the site to link the project with the FTP connection already exists
						site = sites[0];
					}
					else
					{
						// multiple FTP connections are associated with the project; finds the last one
						// try for last remembered site first
						String lastConnection = DeployPreferenceUtil.getDeployEndpoint(project);
						if (lastConnection != null)
						{
							site = SiteConnectionUtils.getSiteWithDestination(lastConnection, sites);
						}
					}

					if (isAutoSyncSelected)
					{
						BaseSyncAction action = null;
						switch (direction)
						{
							case UPLOAD:
								action = new UploadAction();
								break;
							case DOWNLOAD:
								action = new DownloadAction();
								break;
							case BOTH:
								action = new SynchronizeProjectAction();
						}
						action.setActivePart(null, activePart);
						action.setSelection(new StructuredSelection(project));
						action.setSelectedSite(site);
						action.run(null);
					}
				}
				finally
				{
					sub.done();
				}
			}
		};
		return runnable;
	}

	protected IRunnableWithProgress createCapifyRunnable(CapifyProjectPage page)
	{
		IRunnableWithProgress runnable;
		runnable = new IRunnableWithProgress()
		{

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				SubMonitor sub = SubMonitor.convert(monitor, 100);
				try
				{
					// Just open the config/deploy.rb file in an editor
					PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
					{

						public void run()
						{
							try
							{
								IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
										.getActivePage();
								IFile file = getProject().getFile(new Path("config").append("deploy.rb")); //$NON-NLS-1$ //$NON-NLS-2$
								IDE.openEditor(page, file);
							}
							catch (PartInitException e)
							{
								throw new RuntimeException(e);
							}
						}
					});
				}
				catch (Exception e)
				{
					throw new InvocationTargetException(e);
				}
				finally
				{
					sub.done();
				}
			}
		};
		return runnable;
	}

	protected IRunnableWithProgress createHerokuDeployRunnable(HerokuDeployWizardPage page)
	{
		IRunnableWithProgress runnable;
		final String appName = page.getAppName();
		final boolean publishImmediately = page.publishImmediately();

		// persists the auto-publish setting
		IEclipsePreferences prefs = (new InstanceScope()).getNode(Activator.getPluginIdentifier());
		prefs.putBoolean(IPreferenceConstants.HEROKU_AUTO_PUBLISH, publishImmediately);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}

		runnable = new IRunnableWithProgress()
		{

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				SubMonitor sub = SubMonitor.convert(monitor, 100);
				try
				{
					// Initialize git repo for project if necessary
					IGitRepositoryManager manager = GitPlugin.getDefault().getGitRepositoryManager();
					GitRepository repo = manager.createOrAttach(project, sub.newChild(20));
					// TODO What if we didn't create the repo right now, but it is "dirty"?
					// Now do an initial commit
					repo.index().refresh(sub.newChild(15));
					repo.index().stageFiles(repo.index().changedFiles());
					repo.index().commit(Messages.DeployWizard_AutomaticGitCommitMessage);
					sub.worked(10);

					// Run commands to create/deploy
					PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
					{

						public void run()
						{
							CommandElement command;
							if (publishImmediately)
							{
								command = getCommand(BUNDLE_HEROKU, "Create and Deploy App"); //$NON-NLS-1$
							}
							else
							{
								command = getCommand(BUNDLE_HEROKU, "Create App"); //$NON-NLS-1$
							}
							// TODO What if command is null!?
							if (command != null)
							{
								// Send along the app name
								CommandContext context = command.createCommandContext();
								context.put("HEROKU_APP_NAME", appName); //$NON-NLS-1$
								command.execute(context);
							}
						}
					});
				}
				catch (CoreException ce)
				{
					throw new InvocationTargetException(ce);
				}
				finally
				{
					sub.done();
				}
			}

		};
		return runnable;
	}

	protected IRunnableWithProgress createHerokuSignupRunnable(HerokuSignupPage page)
	{
		IRunnableWithProgress runnable;
		final String userID = page.getUserID();
		runnable = new IRunnableWithProgress()
		{

			/**
			 * Send a ping to aptana.com with email address for referral tracking
			 * 
			 * @throws IOException
			 */
			private String sendPing(IProgressMonitor monitor) throws IOException
			{
				HttpURLConnection connection = null;
				try
				{
					final String HOST = "http://toolbox.aptana.com"; //$NON-NLS-1$
					StringBuilder builder = new StringBuilder(HOST);
					builder.append("/webhook/heroku?request_id="); //$NON-NLS-1$
					builder.append(URLEncoder.encode(PingStartup.getApplicationId(), "UTF-8")); //$NON-NLS-1$
					builder.append("&email="); //$NON-NLS-1$
					builder.append(URLEncoder.encode(userID, "UTF-8")); //$NON-NLS-1$
					builder.append("&type=signuphook"); //$NON-NLS-1$

					URL url = new URL(builder.toString());
					connection = (HttpURLConnection) url.openConnection();
					connection.setUseCaches(false);
					connection.setAllowUserInteraction(false);
					int responseCode = connection.getResponseCode();
					if (responseCode != HttpURLConnection.HTTP_OK)
					{
						// Log an error
						Activator.logError(
								MessageFormat.format(Messages.DeployWizard_FailureToGrabHerokuSignupJSError,
										builder.toString()), null);
					}
					else
					{
						return IOUtil.read(connection.getInputStream());
					}
				}
				finally
				{
					if (connection != null)
					{
						connection.disconnect();
					}
				}
				return ""; //$NON-NLS-1$
			}

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				SubMonitor sub = SubMonitor.convert(monitor, 100);
				try
				{
					String javascriptToInject = sendPing(sub.newChild(40));
					openSignup(javascriptToInject, sub.newChild(60));
				}
				catch (Exception e)
				{
					throw new InvocationTargetException(e);
				}
				finally
				{
					sub.done();
				}
			}

			/**
			 * Open the Heroku signup page.
			 * 
			 * @param monitor
			 * @throws Exception
			 */
			private void openSignup(final String javascript, IProgressMonitor monitor) throws Exception
			{
				final String BROWSER_ID = "heroku-signup"; //$NON-NLS-1$
				final URL url = new URL("https://api.heroku.com/signup/aptana3"); //$NON-NLS-1$

				final int style = IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.LOCATION_BAR
						| IWorkbenchBrowserSupport.STATUS;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						openSignupURLinEclipseBrowser(url, style, BROWSER_ID, javascript);
					}
				});
			}
		};
		return runnable;
	}
	
	protected IRunnableWithProgress createEngineYardSignupRunnable(EngineYardSignupPage page)
	{
		IRunnableWithProgress runnable;
		final String userID = page.getUserID();
		runnable = new IRunnableWithProgress()
		{

			/**
			 * Send a ping to aptana.com with email address for referral tracking
			 * 
			 * @throws IOException
			 */
			private String sendPing(IProgressMonitor monitor) throws IOException
			{
				HttpURLConnection connection = null;
				try
				{
					final String HOST = "http://toolbox.aptana.com"; //$NON-NLS-1$
					StringBuilder builder = new StringBuilder(HOST);
					builder.append("/webhook/engineyard?request_id="); //$NON-NLS-1$
					builder.append(URLEncoder.encode(PingStartup.getApplicationId(), "UTF-8")); //$NON-NLS-1$
					builder.append("&email="); //$NON-NLS-1$
					builder.append(URLEncoder.encode(userID, "UTF-8")); //$NON-NLS-1$
					builder.append("&type=signuphook"); //$NON-NLS-1$
					builder.append("&version="); //$NON-NLS-1$
					builder.append(EclipseUtil.getPluginVersion(CorePlugin.PLUGIN_ID));

					URL url = new URL(builder.toString());
					connection = (HttpURLConnection) url.openConnection();
					connection.setUseCaches(false);
					connection.setAllowUserInteraction(false);
					int responseCode = connection.getResponseCode();
					if (responseCode != HttpURLConnection.HTTP_OK)
					{
						// Log an error
						Activator.logError(
								MessageFormat.format(Messages.DeployWizard_FailureToGrabHerokuSignupJSError,
										builder.toString()), null);
					}
					else
					{
						return IOUtil.read(connection.getInputStream());
					}
				}
				finally
				{
					if (connection != null)
					{
						connection.disconnect();
					}
				}
				return ""; //$NON-NLS-1$
			}

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				SubMonitor sub = SubMonitor.convert(monitor, 100);
				try
				{
					String javascriptToInject = sendPing(sub.newChild(40));
					openSignup(javascriptToInject, sub.newChild(60));
				}
				catch (Exception e)
				{
					throw new InvocationTargetException(e);
				}
				finally
				{
					sub.done();
				}
			}

			/**
			 * Open the Engine Yard signup page.
			 * 
			 * @param monitor
			 * @throws Exception
			 */
			private void openSignup(final String javascript, IProgressMonitor monitor) throws Exception
			{
				final String BROWSER_ID = "Engine-Yard-signup"; //$NON-NLS-1$
				final URL url = new URL("http://cloud.engineyard.com/ev?code=APTANA_REFERRAL"); //$NON-NLS-1$

				final int style = IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.LOCATION_BAR
						| IWorkbenchBrowserSupport.STATUS;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						openSignupURLinEclipseBrowser(url, style, BROWSER_ID, javascript);
					}
				});
			}
		};
		return runnable;
	}
	
	protected IRunnableWithProgress createEngineYardDeployRunnable(EngineYardDeployWizardPage page)
	{
		IRunnableWithProgress runnable;

		runnable = new IRunnableWithProgress()
		{

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
				{

					public void run()
					{
						CommandElement command;
						command = getCommand(BUNDLE_ENGINEYARD, "Deploy App"); //$NON-NLS-1$
						command.execute();
					}
				});
			}

		};
		return runnable;
	}

	@Override
	public void addPages()
	{
		// Add the first basic page where they choose the deployment option
		addPage(new DeployWizardPage(project));
		setForcePreviousAndNextButtons(true); // we only add one page here, but we calculate the next page
												// dynamically...
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page)
	{
		// delegate to page because we modify the page list dynamically and don't statically add them via addPage
		return page.getNextPage();
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page)
	{
		// delegate to page because we modify the page list dynamically and don't statically add them via addPage
		return page.getPreviousPage();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		Object element = selection.getFirstElement();
		if (element instanceof IResource)
		{
			IResource resource = (IResource) element;
			this.project = resource.getProject();
		}
	}

	public IProject getProject()
	{
		return project;
	}

	@Override
	public boolean canFinish()
	{
		IWizardPage page = getContainer().getCurrentPage();
		// We don't want getNextPage() getting invoked so early on first page, because it does auth check on Heroku
		// credentials...
		if (page.getName().equals(DeployWizardPage.NAME))
		{
			return false;
		}
		return page.isPageComplete() && page.getNextPage() == null;
	}

	/*
	 * Because we're dynamic and not adding pages the normal way, the pages aren't getting disposed individually. We
	 * need to track what pages are open and dispose them! (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#dispose()
	 */
	@Override
	public void dispose()
	{
		try
		{
			// Find current page and traverse backwards through all the pages to collect them.
			Set<IWizardPage> pages = new HashSet<IWizardPage>();
			IWizardPage page = getContainer().getCurrentPage();
			while (page != null)
			{
				pages.add(page);
				page = page.getPreviousPage();
			}
			// traverse forward
			page = getContainer().getCurrentPage();
			while (page != null)
			{
				pages.add(page);
				page = page.getNextPage();
			}
			for (IWizardPage aPage : pages)
			{
				aPage.dispose();
			}
			pages = null;
		}
		finally
		{
			super.dispose();
		}
	}
	
	private CommandElement getCommand(String bundleName, String commandName)
	{
		BundleEntry entry = BundleManager.getInstance().getBundleEntry(bundleName);
		if (entry == null)
		{
			return null;
		}
		for (BundleElement bundle : entry.getContributingBundles())
		{
			CommandElement command = bundle.getCommandByName(commandName);
			if (command != null)
			{
				return command;
			}
		}
		return null;
	}
	
	private void openSignupURLinEclipseBrowser(URL url, int style, String browserId, final String javascript)
	{
		try
		{
			WebBrowserEditorInput input = new WebBrowserEditorInput(url, style, browserId);
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart editorPart = page.openEditor(input, WebBrowserEditor.WEB_BROWSER_EDITOR_ID);
			WebBrowserEditor webBrowserEditor = (WebBrowserEditor) editorPart;
			Field f = WebBrowserEditor.class.getDeclaredField("webBrowser"); //$NON-NLS-1$
			f.setAccessible(true);
			BrowserViewer viewer = (BrowserViewer) f.get(webBrowserEditor);
			final Browser browser = viewer.getBrowser();
			browser.addProgressListener(new ProgressListener()
			{

				public void completed(ProgressEvent event)
				{
					browser.removeProgressListener(this);
					browser.execute(javascript);
				}

				public void changed(ProgressEvent event)
				{
					// ignore
				}
			});
		}
		catch (Exception e)
		{
			Activator.logError(e);
		}
	}

}
