package com.aptana.git.ui.internal.actions;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
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

import com.aptana.core.util.IOUtil;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;

@SuppressWarnings({ "restriction", "rawtypes" })
public class GithubNetworkHandler extends AbstractGitHandler
{

	private static final String GITHUB_COM = "github.com"; //$NON-NLS-1$
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
				GitUIPlugin.logError(e.getMessage(), e);
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
				GitUIPlugin.logError(e.getMessage(), e);
			}
		}
		catch (CoreException e)
		{
			GitUIPlugin.logError(e);
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
		// Check the remote urls for github and use that to determine URL we need!
		Set<String> remoteURLs = repo.remoteURLs();
		for (String remoteURL : remoteURLs)
		{
			if (!remoteURL.contains(GITHUB_COM))
			{
				continue;
			}
			String remaining = remoteURL.substring(remoteURL.indexOf(GITHUB_COM) + 10);
			if (remaining.startsWith("/") || remaining.startsWith(":")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				remaining = remaining.substring(1);
			}
			if (remaining.endsWith(GitRepository.GIT_DIR))
			{
				remaining = remaining.substring(0, remaining.length() - 4);
			}
			int split = remaining.indexOf("/"); //$NON-NLS-1$
			String userName = remaining.substring(0, split);
			String repoName = remaining.substring(split + 1);
			return MessageFormat.format("http://github.com/{0}/{1}/network", userName, repoName); //$NON-NLS-1$
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
				GitUIPlugin.logError(e.getMessage(), e);
			}
		}
		return fgGithubJS;
	}

}
