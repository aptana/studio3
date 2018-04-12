/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.internal.text.revisions.Colors;
import org.eclipse.jface.text.revisions.Revision;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.history.IHistoryPage;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.team.ui.history.RevisionAnnotationController;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.QuickDiffReferenceProvider;
import com.aptana.git.ui.internal.history.GitHistoryPage;

@SuppressWarnings("restriction")
public class BlameHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		Collection<IResource> resources = getSelectedResources();
		for (IResource resource : resources)
		{
			if (resource.getType() != IResource.FILE)
			{
				continue;
			}
			final IFile file = (IFile) resource;
			final GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(file.getProject());
			RevisionInformation info = createRevisionInformation(repo, repo.relativePath(file));

			IEditorPart editorPart = getEditor();
			AbstractDecoratedTextEditor fEditor = null;
			if (editorPart instanceof AbstractDecoratedTextEditor)
			{
				fEditor = (AbstractDecoratedTextEditor) editorPart;
			}
			if (fEditor == null)
			{
				IWorkbenchPage page = getActivePage();
				if (page != null)
				{
					try
					{
						fEditor = (AbstractDecoratedTextEditor) IDE.openEditor(page, file);
					}
					catch (PartInitException e)
					{
						IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
					}
				}
			}

			if (fEditor != null)
			{
				fEditor.showRevisionInformation(info, QuickDiffReferenceProvider.ID);
				IWorkbenchPage page = getActivePage();
				if (page != null)
				{
					attachHistorySyncher(file, repo, page);
				}
			}
		}
		return null;
	}

	protected IHistoryView attachHistorySyncher(final IFile file, final GitRepository repo, IWorkbenchPage page)
	{
		IHistoryView historyView = TeamUI.getHistoryView();
		if (historyView != null)
		{
			IHistoryPage historyPage = historyView.showHistoryFor(file);
			if (historyPage instanceof GitHistoryPage)
			{
				new RevisionAnnotationController(page, file, ((GitHistoryPage) historyPage).getSelectionProvider())
				{

					@Override
					protected Object getHistoryEntry(Revision selected)
					{
						String sha = selected.getId();
						return new GitCommit(repo, sha);
					}
				};
			}
		}
		return historyView;
	}

	private RevisionInformation createRevisionInformation(GitRepository repo, IPath relativePath)
	{
		// Run git blame on the file, parse out the output and turn it into revisions!
		IStatus result = repo.execute(GitRepository.ReadWrite.READ, "blame", "-p", relativePath.toOSString()); //$NON-NLS-1$ //$NON-NLS-2$
		if (result == null || !result.isOK())
		{
			return new RevisionInformation();
		}

		String output = result.getMessage();
		Map<String, GitRevision> revisions = new HashMap<String, GitRevision>();

		String[] lines = output.split("\r?\n|\r"); //$NON-NLS-1$
		GitRevision revision = null;
		String sha = null;
		String author = null;
		String committer = null;
		int finalLine = 1;
		int numberOfLines = 1;
		Calendar timestamp = null;
		String summary = null;
		for (String line : lines)
		{
			if (sha == null)
			{
				// first line, grab the sha, line numbers
				String[] parts = line.split("\\s"); //$NON-NLS-1$
				sha = new String(parts[0]);
				finalLine = Integer.parseInt(parts[2]);
				if (parts.length > 3)
				{
					numberOfLines = Integer.parseInt(parts[3]);
				}
			}
			else if (line.startsWith("author ")) //$NON-NLS-1$
			{
				author = new String(line.substring(7));
			}
			else if (line.startsWith("summary ")) //$NON-NLS-1$
			{
				summary = new String(line.substring(8));
			}
			else if (line.startsWith("committer-time ")) //$NON-NLS-1$
			{
				String time = line.substring(15);
				long timeValue = Long.parseLong(time) * 1000;
				timestamp = Calendar.getInstance();
				timestamp.setTimeInMillis(timeValue);
			}
			else if (line.startsWith("committer ")) //$NON-NLS-1$
			{
				committer = new String(line.substring(10));
			}
			else if (line.charAt(0) == '\t')
			{
				// it's the actual source, we're done with the revision!
				revision = revisions.get(sha);
				if (revision == null)
				{
					revision = new GitRevision(sha, author, committer, summary, timestamp.getTime());
				}
				// Need to shift line numbers down one to match properly
				revision.addRange(new LineRange(finalLine - 1, numberOfLines));
				revisions.put(sha, revision);
				sha = null;
				revision = null;
				author = null;
				committer = null;
				timestamp = null;
				summary = null;
				numberOfLines = 1;
				finalLine = 1;
			}
		}

		RevisionInformation info = new RevisionInformation();
		if (!revisions.isEmpty())
		{
			List<String> uniqueAuthors = new ArrayList<String>();
			for (GitRevision rev : revisions.values())
			{
				if (uniqueAuthors.contains(rev.getAuthor()))
				{
					continue;
				}
				uniqueAuthors.add(rev.getAuthor());
			}
			// Assign unique colors!
			RGB[] colors;
			if (uniqueAuthors.size() < 2)
			{
				colors = new RGB[] { new RGB(255, 0, 0) };
			}
			else
			{
				// FIXME Shouldn't be accessing this class, so we'll need to create our own version eventually
				colors = Colors.rainbow(uniqueAuthors.size());
			}
			for (GitRevision rev : revisions.values())
			{
				rev.setColor(colors[uniqueAuthors.indexOf(rev.getAuthor())]);
				info.addRevision(rev);
			}
		}
		return info;
	}
}
