/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.ui.DiffFormatter;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.actions.Messages;
import com.aptana.ui.util.SafeMessageDialogRunnable;
import com.aptana.ui.util.UIUtils;

public class DiffHandler extends AbstractGitHandler
{

	@Override
	protected boolean calculateEnabled()
	{
		return !getSelectedChangedFiles().isEmpty();
	}

	private List<ChangedFile> getSelectedChangedFiles()
	{
		List<ChangedFile> changedFiles = new ArrayList<ChangedFile>();
		IGitRepositoryManager manager = getGitRepositoryManager();
		if (manager == null)
		{
			return changedFiles;
		}

		GitRepository repo = null;
		for (IResource resource : getSelectedResources())
		{
			if (resource == null)
			{
				continue;
			}

			if (repo == null)
			{
				repo = manager.getAttached(resource.getProject());
			}
			changedFiles.addAll(getChangedFilesForResource(repo, resource));
		}
		return changedFiles;
	}

	private List<ChangedFile> getChangedFilesForResource(GitRepository repo, IResource resource)
	{
		List<ChangedFile> files = new ArrayList<ChangedFile>();
		if (repo == null)
		{
			return files;
		}
		if (resource instanceof IContainer)
		{
			files.addAll(repo.getChangedFilesForContainer((IContainer) resource));
		}
		else
		{
			ChangedFile changedFile = repo.getChangedFileForResource(resource);
			if (changedFile != null)
			{
				files.add(changedFile);
			}
		}
		return files;
	}

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		Map<IPath, String> diffs = new HashMap<IPath, String>();
		List<ChangedFile> changedFiles = getSelectedChangedFiles();
		if (changedFiles == null || changedFiles.isEmpty())
		{
			return null;
		}

		GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			return null;
		}
		for (ChangedFile file : changedFiles)
		{
			if (file == null)
			{
				continue;
			}

			if (diffs.containsKey(file.getRelativePath()))
			{
				continue; // already calculated diff...
			}
			String diff = repo.index().diffForFile(file, file.hasStagedChanges(), 3);
			diffs.put(file.getRelativePath(), diff);
		}
		if (diffs.isEmpty())
		{
			return null;
		}

		String diff = ""; //$NON-NLS-1$
		try
		{
			diff = DiffFormatter.toHTML(diffs);
		}
		catch (Throwable t)
		{
			IdeLog.logError(GitUIPlugin.getDefault(), "Failed to turn diff into HTML", t, IDebugScopes.DEBUG); //$NON-NLS-1$
		}

		final String finalDiff = diff;
		UIUtils.showMessageDialogFromBgThread(new SafeMessageDialogRunnable()
		{
			public int openMessageDialog()
			{
				MessageDialog dialog = new MessageDialog(getShell(), Messages.GitProjectView_GitDiffDialogTitle, null,
						"", 0, new String[] { IDialogConstants.OK_LABEL }, 0) //$NON-NLS-1$
				{
					@Override
					protected Control createCustomArea(Composite parent)
					{
						Browser diffArea = new Browser(parent, SWT.BORDER);
						GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
						data.heightHint = 300;
						diffArea.setLayoutData(data);
						diffArea.setText(finalDiff);
						return diffArea;
					}

					@Override
					protected boolean isResizable()
					{
						return true;
					}
				};
				return dialog.open();
			}
		});

		return null;
	}

}
