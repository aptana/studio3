/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.quickdiff.IQuickDiffReferenceProvider;

import com.aptana.core.util.IOUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;

public class QuickDiffReferenceProvider implements IQuickDiffReferenceProvider
{

	public static final String ID = "com.aptana.git.ui.quickdiff"; //$NON-NLS-1$

	private String fId;

	/**
	 * The active editor.
	 */
	private ITextEditor editor;

	/**
	 * The reference document to compare against. This is the source of the file as of the comparison rev/SHA. By
	 * default that is HEAD.
	 */
	private Document fReference;

	/**
	 * Lock used to synchronize access to fReference.
	 */
	private Object fReferenceLock = new Object();

	public IDocument getReference(IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			synchronized (fReferenceLock)
			{
				if (fReference == null)
				{
					IEditorInput input = getEditorInput();
					if (!(input instanceof IFileEditorInput))
					{
						return null;
					}
					// TODO Handle other editor input types, like IURIEditorInput!
					IFile file = ((IFileEditorInput) input).getFile();
					if (file == null)
					{
						return null;
					}
					IGitRepositoryManager manager = getGitRepositoryManager();
					if (manager == null)
					{
						return null;
					}
					GitRepository repo = manager.getAttached(file.getProject());
					if (repo == null)
					{
						return null;
					}
					IPath fileName = repo.relativePath(file);
					// TODO allow user to specify the revision/SHA/branch to compare against
					IFileRevision revision = GitPlugin.revisionForCommit(new GitCommit(repo, GitRepository.HEAD), fileName);
					IStorage storage = revision.getStorage(monitor);
					if (storage == null)
					{
						return null;
					}
					String src = IOUtil.read(storage.getContents(), IOUtil.UTF_8);
					fReference = new Document(src);
				}
				return fReference;
			}
		}
		finally
		{
			sub.done();
		}
	}

	private IEditorInput getEditorInput()
	{
		if (this.editor == null)
		{
			return null;
		}
		return this.editor.getEditorInput();
	}

	private IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	public void dispose()
	{
		this.editor = null;
		synchronized (fReferenceLock)
		{
			this.fReference = null;
		}
	}

	public String getId()
	{
		return fId;
	}

	public void setActiveEditor(ITextEditor editor)
	{
		this.editor = editor;
	}

	public boolean isEnabled()
	{
		// TODO Auto-generated method stub
		return true;
	}

	public void setId(String id)
	{
		fId = id;
	}

}
