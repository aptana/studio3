package com.aptana.git.ui.internal.history;

import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.team.ui.history.HistoryPage;

import com.aptana.git.core.model.Diff;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;

public class GitHistoryPage extends HistoryPage
{

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
		}
		// What if we have some trailing slash or something?
		if (resourcePath.length() == 0)
		{
			resourcePath = repo.currentBranch();
		}
		revList.walkRevisionListWithSpecifier(new GitRevSpecifier(resourcePath), -1);
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
		Table table = new Table(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		final TableLayout layout = new TableLayout();
		table.setLayout(layout);

		final TableColumn graph = new TableColumn(table, SWT.NONE);
		graph.setResizable(true);
		graph.setText(""); //$NON-NLS-1$
		graph.setWidth(250);
		layout.addColumnData(new ColumnWeightData(20, true));

		final TableColumn author = new TableColumn(table, SWT.NONE);
		author.setResizable(true);
		author.setText("Author");
		author.setWidth(250);
		layout.addColumnData(new ColumnWeightData(10, true));

		final TableColumn date = new TableColumn(table, SWT.NONE);
		date.setResizable(true);
		date.setText("Date");
		date.setWidth(250);
		layout.addColumnData(new ColumnWeightData(5, true));

		TableViewer viewer = new TableViewer(table);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new CommitLabelProvider());
		return viewer;
	}

	static class CommitLabelProvider extends BaseLabelProvider implements ITableLabelProvider
	{

		private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

		@Override
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex)
		{
			GitCommit commit = (GitCommit) element;
			switch (columnIndex)
			{
				case 0:
					return commit.getSubject();
				case 1:
					return commit.getAuthor();
				case 2:
					return fmt.format(commit.date());
				default:
					return "";
			}
		}

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
				return "";
			resource = resources[0];
		}
		else if (input instanceof IResource)
		{
			resource = (IResource) input;
		}
		if (resource == null)
			return "";

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
		// TODO Auto-generated method stub

	}

	@Override
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	protected Document commitToDocument(GitCommit commit)
	{
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z");
		StringBuilder builder = new StringBuilder();
		builder.append("SHA: ").append(commit.sha()).append("\n");
		builder.append("Author: ").append(commit.getAuthor()).append("\n");
		builder.append("Date: ").append(format.format(commit.date())).append("\n");
		builder.append("Subject: ").append(commit.getSubject()).append("\n");
		if (commit.parents() != null && !commit.parents().isEmpty())
		{
			for (String parentSha : commit.parents())
			{
				builder.append("Parent: ");
				builder.append(parentSha).append("\n");
			}
		}
		builder.append("\n").append(commit.getComment()).append("\n\n");
		
		List<Diff> diffs = commit.getDiff();
		for (Diff diff : diffs)
		{
			if (diff.fileCreated())
			{
				builder.append("Created: ");
			}
			else if (diff.fileDeleted())
			{
				builder.append("Deleted: ");
			}
			else
			{
				builder.append("Modified: ");
			}
			builder.append(diff.fileName()).append("\n");
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
