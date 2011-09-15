/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexFilesOfProjectJob;
import com.aptana.index.core.IndexManager;

public class CommonReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension,
		IBatchReconcilingStrategy
{

	private AbstractThemeableEditor fEditor;
	private boolean fInitialReconcileDone;

	/**
	 * Code Folding.
	 */
	private Map<ProjectionAnnotation, Position> fPositions = new HashMap<ProjectionAnnotation, Position>();

	private IProgressMonitor fMonitor;

	private IFoldingComputer folder;
	private IDocument fDocument;

	public CommonReconcilingStrategy(AbstractThemeableEditor editor)
	{
		fEditor = editor;
		fEditor.addPropertyListener(new IPropertyListener()
		{

			public void propertyChanged(Object source, int propId)
			{
				if (propId == IEditorPart.PROP_INPUT)
				{
					reconcile(false, true);
				}
			}
		});
	}

	public AbstractThemeableEditor getEditor()
	{
		return fEditor;
	}

	public void reconcile(IRegion partition)
	{
		// we can't do incremental yet
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{
		// we can't do incremental yet
	}

	public void setDocument(IDocument document)
	{
		folder = createFoldingComputer(document);
		fEditor.getFileService().setDocument(document);
		fDocument = document;
	}

	protected IFoldingComputer createFoldingComputer(IDocument document)
	{
		return fEditor.createFoldingComputer(document);
	}

	public void initialReconcile()
	{
		if (fInitialReconcileDone)
		{
			return;
		}
		reconcile(true);
		fInitialReconcileDone = true;
	}

	public void setProgressMonitor(IProgressMonitor monitor)
	{
		fMonitor = monitor;
	}

	public void aboutToBeReconciled()
	{
	}

	public void notifyListeners(boolean notify)
	{
	}

	public void reconciled()
	{
	}

	protected void calculatePositions(boolean initialReconcile, IProgressMonitor monitor)
	{
		if (monitor != null && monitor.isCanceled())
		{
			return;
		}

		// Folding...

		try
		{
			synchronized (fPositions)
			{
				fPositions.clear();
				fPositions = folder.emitFoldingRegions(initialReconcile, monitor);
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		// If we had all positions we shouldn't probably listen to cancel, but we may have exited emitFoldingRegions
		// early because of cancel...
		if (monitor != null && monitor.isCanceled())
		{
			return;
		}

		updatePositions();
	}

	// Delete all the positions in the document
	protected void clearPositions(IProgressMonitor monitor)
	{
		if (monitor != null && monitor.isCanceled())
		{
			return;
		}
		// clear folding positions
		synchronized (fPositions)
		{
			fPositions.clear();
		}
	}

	/**
	 * Update the folding positions in the document
	 */
	protected void updatePositions()
	{
		fEditor.updateFoldingStructure(fPositions);
	}

	private void reconcile(boolean initialReconcile)
	{
		reconcile(initialReconcile, false);
	}

	private void reconcile(boolean initialReconcile, boolean force)
	{
		FileService fileService = fEditor.getFileService();
		// doing a full parse at the moment
		if (force || fileService.parse(fMonitor))
		{
			// only do folding and validation when the source was changed
			if (fEditor.isFoldingEnabled())
			{
				calculatePositions(initialReconcile, fMonitor);
			}
			else
			{
				synchronized (fPositions)
				{
					fPositions.clear();
				}
				updatePositions();
			}
			if (fMonitor != null && fMonitor.isCanceled())
			{
				return;
			}
			fEditor.getFileService().validate();
			if (fEditor.getFileService().hasValidParseResult())
			{
				if (fMonitor != null && fMonitor.isCanceled())
				{
					return;
				}
				updateIndexWithWorkingCopy();
			}
		}
	}

	/**
	 * Take the working copy (editor buffer/document) and replace the index's entries for the underlying file with the
	 * results of indexing the buffer. This allows us to pretend that the current contents (unsaved) are reflected for
	 * the file in the index. This fixes APSTUD-2944. Eventually the user has to save or not - if they save, we'll end
	 * up indexing and update. if they don't we'll need to re-index the file manually since the unsaved contenst will
	 * still be in there!
	 */
	private void updateIndexWithWorkingCopy()
	{
		// Update the index with working copy!
		final IFile file = getFile();
		if (file == null)
		{
			return;
		}
		IProject project = file.getProject();
		Index index = IndexManager.getInstance().getIndex(project.getLocationURI());
		final URI fileURI = file.getLocationURI();
		index.remove(fileURI);
		Set<IFile> files = new HashSet<IFile>();
		files.add(file);
		IndexFilesOfProjectJob job = new IndexFilesOfProjectJob(project, files)
		{
			@Override
			protected Set<IFileStore> toFileStores(IProgressMonitor monitor)
			{
				Set<IFileStore> fileStores = new HashSet<IFileStore>();
				IFileStore fileStore = new FileStore()
				{

					public URI toURI()
					{
						return fileURI;
					}

					public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
					{
						try
						{
							return new ByteArrayInputStream(fDocument.get().getBytes(file.getCharset()));
						}
						catch (UnsupportedEncodingException e)
						{
							IdeLog.logError(CommonEditorPlugin.getDefault(), e);
						}
						return null;
					}

					@Override
					public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
					{
						return null;
					}

					@Override
					public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
					{
						return null;
					}

					@Override
					public IFileStore getChild(String name)
					{
						return null;
					}

					@Override
					public String getName()
					{
						return file.getName();
					}

					@Override
					public IFileStore getParent()
					{
						return null;
					}
				};
				fileStores.add(fileStore);
				return fileStores;
			}
		};
		job.setPriority(Job.SHORT);
		job.setSystem(true);
		job.schedule();
	}

	private IFile getFile()
	{
		if (fEditor != null)
		{
			IEditorInput editorInput = fEditor.getEditorInput();

			if (editorInput instanceof IFileEditorInput)
			{
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				return fileEditorInput.getFile();
			}
		}

		return null;
	}

	public void fullReconcile()
	{
		reconcile(false);
	}
}
