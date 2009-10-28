package com.aptana.git.ui.internal.history;

import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.team.ui.history.HistoryPage;

import com.aptana.git.core.model.Diff;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;

public class GitHistoryPage extends HistoryPage
{
	
	private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(Messages.GitHistoryPage_DateFormat);

	private Composite ourControl;
	private SashForm graphDetailSplit;
	private SashForm revInfoSplit;
	private TableViewer graph;
	private TextViewer commentViewer;
	private CommitFileDiffViewer fileViewer;

	@Override
	public boolean inputSet()
	{
		if (graph == null)
			return false;

		Object input = super.getInput();

		IResource resource = null;
		if (input instanceof IResource[])
		{
			IResource[] resources = (IResource[]) input;
			if (resources == null || resources.length == 0)
				return false;
			resource = resources[0];
		}
		else if (input instanceof IResource)
		{
			resource = (IResource) input;
		}
		if (resource == null)
			return false;

		// Generate the commit list and set the components up with it!
		GitRepository repo = GitRepository.getAttached(resource.getProject());
		GitRevList revList = new GitRevList(repo);
		// Need the repo relative path
		String workingDirectory = repo.workingDirectory();
		String resourcePath = resource.getLocationURI().getPath();
		if (resourcePath.startsWith(workingDirectory))
		{
			resourcePath = resourcePath.substring(workingDirectory.length());
			if (resourcePath.startsWith("/") || resourcePath.startsWith("\\")) //$NON-NLS-1$ //$NON-NLS-2$
				resourcePath = resourcePath.substring(1);
		}
		// What if we have some trailing slash or something?
		if (resourcePath.length() == 0)
		{
			resourcePath = repo.currentBranch();
		}
		repo.lazyReload();
		revList.walkRevisionListWithSpecifier(new GitRevSpecifier(resourcePath));
		List<GitCommit> commits = revList.getCommits();
		graph.setInput(commits);
		return true;
	}

	@Override
	public void createControl(Composite parent)
	{
		ourControl = createMainPanel(parent);
		GridData gd = new GridData();
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		ourControl.setLayoutData(gd);

		gd = new GridData();
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		graphDetailSplit = new SashForm(ourControl, SWT.VERTICAL);
		graphDetailSplit.setLayoutData(gd);

		graph = createCommitTable(graphDetailSplit);
		revInfoSplit = new SashForm(graphDetailSplit, SWT.HORIZONTAL);
		commentViewer = new TextViewer(revInfoSplit, SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
		fileViewer = new CommitFileDiffViewer(revInfoSplit);

		graphDetailSplit.setWeights(new int[] { 500, 500 });
		revInfoSplit.setWeights(new int[] { 700, 300 });

		// revObjectSelectionProvider = new RevObjectSelectionProvider();
		// popupMgr = new MenuManager(null, POPUP_ID);
		attachCommitSelectionChanged();
		// createLocalToolbarActions();
		// createResourceFilterActions();
		// createStandardActions();
		// createViewMenu();

		// finishContextMenu();
		// attachContextMenu(graph.getControl());
		// attachContextMenu(commentViewer.getControl());
		// attachContextMenu(fileViewer.getControl());
		layout();
	}

	private TableViewer createCommitTable(Composite parent)
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
					commentViewer.setInput(null);
					fileViewer.setInput(null);
					return;
				}

				final IStructuredSelection sel = ((IStructuredSelection) s);
				GitCommit commit = (GitCommit) sel.getFirstElement();
				commentViewer.setInput(commitToDocument(commit));
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

	@Override
	public String getDescription()
	{
		return getName();
	}

	@Override
	public String getName()
	{
		Object input = super.getInput();

		IResource resource = null;
		if (input instanceof IResource[])
		{
			IResource[] resources = (IResource[]) input;
			if (resources == null || resources.length == 0)
				return ""; //$NON-NLS-1$
			resource = resources[0];
		}
		else if (input instanceof IResource)
		{
			resource = (IResource) input;
		}
		if (resource == null)
			return ""; //$NON-NLS-1$

		return resource.getProject().getName();
	}

	@Override
	public boolean isValidInput(final Object object)
	{
		return canShowHistoryFor(object);
	}

	@Override
	public void refresh()
	{
		// TODO Force a reload of the index and the refs and set input.
	}

	@Override
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	protected Document commitToDocument(GitCommit commit)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(Messages.GitHistoryPage_SHA).append(commit.sha()).append("\n"); //$NON-NLS-1$
		builder.append(Messages.GitHistoryPage_AuthorHeader).append(commit.getAuthor()).append("\n"); //$NON-NLS-1$
		builder.append(Messages.GitHistoryPage_DateHeader).append(TIMESTAMP_FORMAT.format(commit.date())).append("\n"); //$NON-NLS-1$
		builder.append(Messages.GitHistoryPage_SubjectHeader).append(commit.getSubject()).append("\n"); //$NON-NLS-1$
		if (commit.parents() != null && !commit.parents().isEmpty())
		{
			for (String parentSha : commit.parents())
			{
				builder.append(Messages.GitHistoryPage_ParentHeader);
				builder.append(parentSha).append("\n"); //$NON-NLS-1$
			}
		}
		builder.append("\n").append(commit.getComment()).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$

		List<Diff> diffs = commit.getDiff();
		for (Diff diff : diffs)
		{
			if (diff.renamed())
			{
				builder.append(Messages.GitHistoryPage_RenamedHeader);
				builder.append(diff.oldName()).append(" -> "); //$NON-NLS-1$
				builder.append(diff.newName()).append("\n"); //$NON-NLS-1$
			}
			else
			{
				if (diff.fileCreated())
				{
					builder.append(Messages.GitHistoryPage_CreatedHeader);
				}
				else if (diff.fileDeleted())
				{
					builder.append(Messages.GitHistoryPage_DeletedHeader);
				}
				else
				{
					builder.append(Messages.GitHistoryPage_ModifiedHeader);
				}
				builder.append(diff.fileName()).append("\n"); //$NON-NLS-1$
			}
		}

		return new Document(builder.toString());
	}

	/**
	 * Determine if the input can be shown in this viewer.
	 * 
	 * @param object
	 *            an object that is hopefully of type ResourceList or IResource, but may be anything (including null).
	 * @return true if the input is a ResourceList or an IResource of type FILE, FOLDER or PROJECT and we can show it;
	 *         false otherwise.
	 */
	public static boolean canShowHistoryFor(final Object object)
	{
		if (object instanceof IResource[])
		{
			final IResource[] array = (IResource[]) object;
			if (array.length == 0)
				return false;
			for (final IResource r : array)
			{
				if (!typeOk(r))
					return false;
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

}
