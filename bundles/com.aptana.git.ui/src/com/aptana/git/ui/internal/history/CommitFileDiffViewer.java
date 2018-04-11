/*******************************************************************************
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.aptana.git.ui.internal.history;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ui.history.FileRevisionTypedElement;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.aptana.core.util.ArrayUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.Diff;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.ui.GitUIPlugin;

@SuppressWarnings("restriction")
class CommitFileDiffViewer extends TableViewer
{

	CommitFileDiffViewer(final Composite parent, final GitHistoryPage historyPage)
	{
		super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);

		final Table rawTable = getTable();
		rawTable.setHeaderVisible(true);
		rawTable.setLinesVisible(false);

		final TableLayout layout = new TableLayout();
		rawTable.setLayout(layout);
		createColumns(rawTable, layout);

		setContentProvider(new CommitDiffContentProvider());
		setLabelProvider(new SingleCommitLabelProvider());

		addOpenListener(new IOpenListener()
		{
			public void open(final OpenEvent event)
			{
				final ISelection s = event.getSelection();
				if (s.isEmpty() || !(s instanceof IStructuredSelection))
					return;
				final IStructuredSelection iss = (IStructuredSelection) s;
				final Diff d = (Diff) iss.getFirstElement();
				showTwoWayFileDiff(d);
			}
		});

		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				manager.add(new OpenRevisionAction(historyPage.getSite().getPage(), getTable()));
				// Other plug-ins can contribute there actions here
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});

		Menu menu = menuMgr.createContextMenu(getControl());
		getControl().setMenu(menu);
	}

	private void showTwoWayFileDiff(final Diff d)
	{
		if (d == null || d.isBinary())
			return;
		// TODO What about files that are really big? When a file is created/deleted?
		final GitCommit c = d.commit();
		final IFileRevision baseFile = GitPlugin.revisionForCommit(c.getFirstParent(), Path.fromPortableString(d.oldName()));
		final IFileRevision nextFile = GitPlugin.revisionForCommit(c, Path.fromPortableString(d.newName()));
		final ITypedElement base = new FileRevisionTypedElement(baseFile);
		final ITypedElement next = new FileRevisionTypedElement(nextFile);
		final GitCompareFileRevisionEditorInput in = new GitCompareFileRevisionEditorInput(base, next, null);
		CompareUI.openCompareEditor(in);
	}

	private void createColumns(final Table rawTable, final TableLayout layout)
	{
		final TableColumn mode = new TableColumn(rawTable, SWT.NONE);
		mode.setResizable(true);
		mode.setText(""); //$NON-NLS-1$
		mode.setWidth(5);
		layout.addColumnData(new ColumnWeightData(1, true));

		final TableColumn path = new TableColumn(rawTable, SWT.NONE);
		path.setResizable(true);
		path.setText(Messages.CommitFileDiffViewer_PathColumnLabel);
		path.setWidth(250);
		layout.addColumnData(new ColumnWeightData(20, true));
	}

	private static class CommitDiffContentProvider implements IStructuredContentProvider
	{

		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof GitCommit)
			{
				GitCommit commit = (GitCommit) inputElement;
				return commit.getDiff().toArray();
			}
			return ArrayUtil.NO_OBJECTS;
		}

		public void dispose()
		{
			// do nothing
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			// do nothing
		}

	}

	private static class SingleCommitLabelProvider extends BaseLabelProvider implements ITableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
		{
			Diff diff = (Diff) element;
			switch (columnIndex)
			{
				case 0:
					if (diff.fileCreated())
						return GitUIPlugin.getImage("icons/obj16/empty_file.png"); //$NON-NLS-1$
					if (diff.fileDeleted())
						return GitUIPlugin.getImage("icons/obj16/deleted_file.png"); //$NON-NLS-1$
					return GitUIPlugin.getImage("icons/obj16/new_file.png"); //$NON-NLS-1$
				default:
					return null;
			}
		}

		public String getColumnText(Object element, int columnIndex)
		{
			Diff diff = (Diff) element;
			switch (columnIndex)
			{
				case 0:
					if (diff.renamed())
						return Messages.CommitFileDiffViewer_Renamed;
					if (diff.fileCreated())
						return Messages.CommitFileDiffViewer_Created;
					if (diff.fileDeleted())
						return Messages.CommitFileDiffViewer_Deleted;
					return Messages.CommitFileDiffViewer_Modified;
				case 1:
					if (diff.renamed())
						return diff.oldName() + " -> " + diff.newName(); //$NON-NLS-1$
					return diff.fileName();
				default:
					return ""; //$NON-NLS-1$
			}
		}

	}
}
