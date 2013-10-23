/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.history;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.team.ui.history.HistoryPage;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.ui.util.UIUtils;

public class GitHistoryPage extends HistoryPage
{

	private static final String POPUP_MENU_ID = "com.aptana.git.ui.git_history"; //$NON-NLS-1$

	private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(Messages.GitHistoryPage_DateFormat);

	private Composite ourControl;
	private SashForm graphDetailSplit;
	private SashForm revInfoSplit;
	private CommitGraphTable graph;
	private Browser commentViewer;
	private CommitFileDiffViewer fileViewer;
	private String currentRef;

	@Override
	public boolean inputSet()
	{
		if (graph == null)
		{
			return false;
		}

		Object input = super.getInput();

		IResource resource = null;
		if (input instanceof IResource[])
		{
			IResource[] resources = (IResource[]) input;
			if (resources.length == 0)
			{
				return false;
			}
			resource = resources[0];
		}
		else if (input instanceof IResource)
		{
			resource = (IResource) input;
		}
		if (resource == null)
		{
			return false;
		}

		GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		loadHistory(resource, currentRef = repo.currentBranch());
		return true;
	}

	private void loadHistory(final IResource resource, final String ref)
	{
		currentRef = ref;
		Job job = new Job(Messages.GitHistoryPage_GeneratingHistoryJob_title)
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
				// Generate the commit list and set the components up with it!
				GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
				if (repo == null)
				{
					return Status.OK_STATUS;
				}
				GitRevList revList = new GitRevList(repo);
				// Need the repo relative path
				IPath resourcePath = repo.relativePath(resource);
				if (subMonitor.isCanceled())
				{
					return Status.CANCEL_STATUS;
				}
				repo.lazyReload();
				subMonitor.worked(5);
				GitRevSpecifier rev;
				if (resourcePath.isEmpty())
				{
					rev = new GitRevSpecifier(ref);
				}
				else
				{
					rev = new GitRevSpecifier(ref, "--", resourcePath.toOSString()); //$NON-NLS-1$
				}
				IStatus result = revList.walkRevisionListWithSpecifier(rev, subMonitor.newChild(95));
				if (result != null && result.isOK())
				{
					final List<GitCommit> commits = revList.getCommits();
					Display.getDefault().asyncExec(new Runnable()
					{

						public void run()
						{
							graph.setCommits(commits);
							if (getControl() != null && !getControl().isDisposed())
							{
								getSite().getPage().activate((IWorkbenchPart) getHistoryView());
								((IViewPart) getHistoryView()).getViewSite().getActionBars().updateActionBars();
							}
						}
					});
				}
				subMonitor.done();
				return result;
			}
		};
		job.setUser(true);
		job.setPriority(Job.SHORT);
		schedule(job);
	}

	private IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	private IWorkbenchPartSite getWorkbenchSite()
	{
		final IWorkbenchPart part = getHistoryPageSite().getPart();
		return (part != null) ? part.getSite() : null;
	}

	private void schedule(final Job j)
	{
		final IWorkbenchPartSite site = getWorkbenchSite();
		if (site == null)
		{
			j.schedule();
			return;
		}
		final IWorkbenchSiteProgressService p = (IWorkbenchSiteProgressService) site
				.getAdapter(IWorkbenchSiteProgressService.class);
		if (p != null)
		{
			p.schedule(j, 0, true /* use half-busy cursor */);
			return;
		}
		j.schedule();
	}

	@Override
	public void createControl(Composite parent)
	{
		ourControl = createMainPanel(parent);

		GridData gd = GridDataFactory.fillDefaults().grab(true, true).create();
		ourControl.setLayoutData(gd);

		graphDetailSplit = new SashForm(ourControl, SWT.VERTICAL);
		graphDetailSplit.setLayoutData(gd);

		graph = createCommitTable(graphDetailSplit);
		revInfoSplit = new SashForm(graphDetailSplit, SWT.HORIZONTAL);
		commentViewer = new Browser(revInfoSplit, SWT.READ_ONLY);
		fileViewer = new CommitFileDiffViewer(revInfoSplit, this);

		graphDetailSplit.setWeights(new int[] { 500, 500 });
		revInfoSplit.setWeights(new int[] { 700, 300 });

		attachCommitSelectionChanged();
		hookContextMenu(commentViewer);
		layout();

		commentViewer.setText(MessageFormat.format(
				"<html><head></head><body style=\"background-color: {0};\"></body></html>", toHex(getBackground()))); //$NON-NLS-1$

		getHistoryPageSite().setSelectionProvider(getSelectionProvider());

		hookContextMenu(graph);
	}

	/**
	 * hookContextMenu
	 */
	private void hookContextMenu(TableViewer treeViewer)
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);

		// Create menu.
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(POPUP_MENU_ID, menuMgr, treeViewer);
	}

	protected RGB getBackground()
	{
		return UIUtils.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND).getRGB();
	}

	public ISelectionProvider getSelectionProvider()
	{
		return graph;
	}

	/**
	 * hookContextMenu
	 */
	private void hookContextMenu(Control browserControl)
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				// Other plug-ins can contribute there actions here
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});

		Menu menu = menuMgr.createContextMenu(browserControl);
		browserControl.setMenu(menu);
	}

	private CommitGraphTable createCommitTable(Composite parent)
	{
		return new CommitGraphTable(parent);
	}

	private void attachCommitSelectionChanged()
	{
		graph.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(final SelectionChangedEvent event)
			{
				final ISelection s = event.getSelection();
				if (s.isEmpty() || !(s instanceof IStructuredSelection))
				{
					commentViewer.setText(""); //$NON-NLS-1$
					fileViewer.setInput(null);
					return;
				}

				final IStructuredSelection sel = ((IStructuredSelection) s);
				GitCommit commit = (GitCommit) sel.getFirstElement();
				// TODO If we know the user's github project URL, we can point them to the GitHub URL for this instead
				// of generating our own!
				commentViewer.setText(commitToHTML(commit));
				fileViewer.setInput(commit);
			}
		});
	}

	private Composite createMainPanel(final Composite parent)
	{
		final Composite c = new Composite(parent, SWT.NULL);
		final GridLayout parentLayout = new GridLayout();
		parentLayout.marginHeight = 0;
		parentLayout.marginWidth = 0;
		parentLayout.verticalSpacing = 0;
		c.setLayout(parentLayout);
		return c;
	}

	private void layout()
	{
		graphDetailSplit.setMaximizedControl(null);
		revInfoSplit.setMaximizedControl(null);
		ourControl.layout();
	}

	@Override
	public Control getControl()
	{
		return ourControl;
	}

	@Override
	public void setFocus()
	{
		graph.getControl().setFocus();
	}

	public String getDescription()
	{
		return getName();
	}

	public String getName()
	{
		Object input = super.getInput();

		IResource resource = null;
		if (input instanceof IResource[])
		{
			IResource[] resources = (IResource[]) input;
			if (resources.length == 0)
			{
				return StringUtil.EMPTY;
			}
			resource = resources[0];
		}
		else if (input instanceof IResource)
		{
			resource = (IResource) input;
		}
		if (resource == null)
		{
			return StringUtil.EMPTY;
		}

		return resource.getProject().getName();
	}

	public boolean isValidInput(final Object object)
	{
		return canShowHistoryFor(object);
	}

	public void refresh()
	{
		// TODO Force a reload of the index and the refs and set input.
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	private String commitToHTML(GitCommit commit)
	{
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("\\{sha\\}", commit.sha()); //$NON-NLS-1$
		variables.put("\\{themeBG\\}", toHex(getBackground())); //$NON-NLS-1$
		variables.put("\\{date\\}", TIMESTAMP_FORMAT.format(commit.date())); //$NON-NLS-1$
		variables.put("\\{author\\}", commit.getAuthor()); //$NON-NLS-1$
		variables.put("\\{subject\\}", commit.getSubject()); //$NON-NLS-1$
		String comment = commit.getComment();
		// Auto convert references to URLs into links
		comment = comment.replaceAll("http://(.+)", "<a href=\"$0\" target=\"_blank\">http://$1</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		comment = comment.replaceAll(StringUtil.LINE_SPLITTER_REGEX, "<br />"); // Convert newlines into breakreads //$NON-NLS-1$
		variables.put("\\{comment\\}", comment); //$NON-NLS-1$

		String avatar = StringUtil.EMPTY;
		if (commit.getAuthorEmail() != null)
		{
			avatar = StringUtil.md5(commit.getAuthorEmail().toLowerCase());
		}
		variables.put("\\{avatar\\}", avatar); //$NON-NLS-1$

		StringBuilder parents = new StringBuilder();
		if (commit.parents() != null && !commit.parents().isEmpty())
		{
			for (String parentSha : commit.parents())
			{
				parents.append(parentSha).append("<br />"); //$NON-NLS-1$
			}
		}
		variables.put("\\{parent\\}", parents.toString()); //$NON-NLS-1$
		return StringUtil.replaceAll(loadTemplate(), variables);
	}

	private String loadTemplate()
	{
		try
		{
			InputStream stream = FileLocator.openStream(GitUIPlugin.getDefault().getBundle(),
					new Path("templates").append("commit_details.html"), false); //$NON-NLS-1$ //$NON-NLS-2$
			return IOUtil.read(stream);
		}
		catch (IOException e)
		{
			IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
			return StringUtil.EMPTY;
		}
	}

	/**
	 * Determine if the input can be shown in this viewer.
	 * 
	 * @param object
	 *            an object that is hopefully of type ResourceList or IResource, but may be anything (including null).
	 * @return true if the input is a ResourceList or an IResource of type FILE, FOLDER or PROJECT and we can show it;
	 *         false otherwise.
	 */
	protected static boolean canShowHistoryFor(final Object object)
	{
		if (object instanceof IResource[])
		{
			final IResource[] array = (IResource[]) object;
			if (array.length == 0)
			{
				return false;
			}
			for (final IResource r : array)
			{
				if (!typeOk(r))
				{
					return false;
				}
			}
			return true;

		}

		if (object instanceof IResource)
		{
			return typeOk((IResource) object);
		}

		return false;
	}

	private static boolean typeOk(final IResource object)
	{
		switch (object.getType())
		{
			case IResource.FILE:
			case IResource.FOLDER:
			case IResource.PROJECT:
				return true;
		}
		return false;
	}

	// FIXME Copy-pasted from Theme
	private String toHex(RGB rgb)
	{
		return MessageFormat.format("#{0}{1}{2}", pad(Integer.toHexString(rgb.red), 2, '0'), pad(Integer //$NON-NLS-1$
				.toHexString(rgb.green), 2, '0'), pad(Integer.toHexString(rgb.blue), 2, '0'));
	}

	private String pad(String string, int desiredLength, char padChar)
	{
		StringBuilder builder = new StringBuilder(string);
		while (builder.length() < desiredLength)
		{
			builder.insert(0, padChar);
		}
		return builder.toString();
	}

	public void setRef(String branchName)
	{
		Object input = super.getInput();

		IResource resource = null;
		if (input instanceof IResource[])
		{
			IResource[] resources = (IResource[]) input;
			if (resources.length > 0)
			{
				resource = resources[0];
			}
		}
		else if (input instanceof IResource)
		{
			resource = (IResource) input;
		}

		loadHistory(resource, branchName);
	}

	public String getCurrentRef()
	{
		return currentRef;
	}
}
