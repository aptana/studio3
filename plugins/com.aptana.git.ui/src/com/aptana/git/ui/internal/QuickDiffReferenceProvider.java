/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
					IFileRevision revision = GitPlugin.revisionForCommit(new GitCommit(repo, "HEAD"), fileName); //$NON-NLS-1$
					IStorage storage = revision.getStorage(monitor);
					if (storage == null)
					{
						return null;
					}
					String src = IOUtil.read(storage.getContents());
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
