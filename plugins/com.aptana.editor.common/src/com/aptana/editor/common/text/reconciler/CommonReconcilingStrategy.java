/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.parsing.FileService;

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
		}
	}

	public void fullReconcile()
	{
		reconcile(false);
	}
}
