/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.history;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.ui.IWorkbenchPage;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.model.Diff;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.ui.GitUIPlugin;

@SuppressWarnings("restriction")
class OpenRevisionAction extends Action
{

	private IWorkbenchPage page;
	private Table table;

	OpenRevisionAction(IWorkbenchPage page, Table table)
	{
		this.page = page;
		this.table = table;
	}

	@Override
	public String getText()
	{
		return Messages.OpenRevisionAction_Text;
	}

	@Override
	public void run()
	{
		TableItem[] selected = table.getSelection();
		final Diff d = (Diff) selected[0].getData();
		final GitCommit c = d.commit();
		final IFileRevision nextFile = GitPlugin.revisionForCommit(c, Path.fromPortableString(d.newName()));
		try
		{
			Utils.openEditor(page, nextFile, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}
	}
}
