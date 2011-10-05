/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserView;
import org.eclipse.ui.part.WorkbenchPart;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;

@SuppressWarnings({ "restriction", "rawtypes" })
public class GithubNetworkHandler extends AbstractGitHandler
{
	private static final String GITHUB_JS = "templates/github.js"; //$NON-NLS-1$
	private String fgGithubJS;

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final String networkURL = getGithubURL();
		if (networkURL == null)
		{
			return null;
		}
		try
		{
			// Use View
			final String browserViewerFieldName = "viewer"; //$NON-NLS-1$
			final Class klass = WebBrowserView.class;
			final IViewPart obj = getActivePage().showView(WebBrowserView.WEB_BROWSER_VIEW_ID,
					"-0", IWorkbenchPage.VIEW_VISIBLE); //$NON-NLS-1$

			try
			{
				Method m = WorkbenchPart.class.getDeclaredMethod("setPartName", String.class); //$NON-NLS-1$
				m.setAccessible(true);
				m.invoke(obj, Messages.GithubNetworkHandler_ViewName);
			}
			catch (Exception e)
			{
				IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}

			try
			{
				Field f = klass.getDeclaredField(browserViewerFieldName);
				f.setAccessible(true);
				final BrowserViewer viewer = (BrowserViewer) f.get(obj);
				viewer.getBrowser().addProgressListener(new ProgressListener()
				{

					public void completed(ProgressEvent event)
					{
						String js = getGithubJS();
						viewer.getBrowser().execute(js);
					}

					public void changed(ProgressEvent event)
					{
					}
				});
				viewer.setURL(networkURL);
			}
			catch (Exception e)
			{
				IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}
		return null;
	}

	private String getGithubURL()
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			return null;
		}

		Set<String> urls = repo.getGithubURLs();
		if (!urls.isEmpty())
		{
			return urls.iterator().next() + "/network"; //$NON-NLS-1$
		}
		return null;
	}

	@Override
	protected boolean calculateEnabled()
	{
		return getGithubURL() != null;
	}

	protected synchronized String getGithubJS()
	{
		if (fgGithubJS == null)
		{
			try
			{
				InputStream stream = FileLocator.openStream(GitUIPlugin.getDefault().getBundle(),
						Path.fromPortableString(GITHUB_JS), false);
				fgGithubJS = IOUtil.read(stream);
			}
			catch (IOException e)
			{
				IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
		}
		return fgGithubJS;
	}

}
