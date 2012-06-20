/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.engineyard.ui.wizard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.deploy.engineyard.EngineYardAPI;
import com.aptana.deploy.engineyard.EngineYardDeployProvider;
import com.aptana.deploy.engineyard.EngineYardPlugin;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.ui.wizard.AbstractDeployWizard;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleEntry;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.usage.PingStartup;

@SuppressWarnings("restriction")
public class EngineYardDeployWizard extends AbstractDeployWizard
{

	private static final String EY_IMG_PATH = "icons/ey_small_wizard.png"; //$NON-NLS-1$
	private static final String BUNDLE_ENGINEYARD = "Engine Yard"; //$NON-NLS-1$

	@Override
	public void addPages()
	{
		super.addPages();

		EngineYardAPI api = new EngineYardAPI();
		File credentials = EngineYardAPI.getCredentialsFile();
		// if credentials are valid, go to EngineYardDeployWizardPage
		if (credentials.exists() && api.authenticateFromCredentials().isOK())
		{
			addPage(new EngineYardDeployWizardPage());
		}
		else
		{
			addPage(new EngineYardLoginWizardPage());
		}
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		super.init(workbench, selection);
		setDefaultPageImageDescriptor(EngineYardPlugin.getImageDescriptor(EY_IMG_PATH));
	}

	@Override
	public boolean performFinish()
	{
		IWizardPage currentPage = getContainer().getCurrentPage();
		String pageName = currentPage.getName();

		IRunnableWithProgress runnable = null;
		if (EngineYardSignupPage.NAME.equals(pageName))
		{
			EngineYardSignupPage page = (EngineYardSignupPage) currentPage;
			runnable = createEngineYardSignupRunnable(page);
		}
		else if (EngineYardDeployWizardPage.NAME.equals(pageName))
		{
			EngineYardDeployWizardPage page = (EngineYardDeployWizardPage) currentPage;
			runnable = createEngineYardDeployRunnable(page);
			DeployPreferenceUtil.setDeployType(getProject(), EngineYardDeployProvider.ID);
		}

		if (runnable != null)
		{
			try
			{
				getContainer().run(true, false, runnable);
			}
			catch (Exception e)
			{
				IdeLog.logError(EngineYardPlugin.getDefault(), e);
			}
		}
		return true;
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
					builder.append(URLEncoder.encode(PingStartup.getApplicationId(), IOUtil.UTF_8));
					builder.append("&email="); //$NON-NLS-1$
					builder.append(URLEncoder.encode(userID, IOUtil.UTF_8));
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
						IdeLog.logError(
								EngineYardPlugin.getDefault(),
								MessageFormat.format(Messages.DeployWizard_FailureToGrabHerokuSignupJSError,
										builder.toString()));
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
			IdeLog.logError(EngineYardPlugin.getDefault(), e);
		}
	}
}
