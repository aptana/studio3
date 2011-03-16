/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Display;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.parsing.FileService;

public class CommonReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension
{

	private AbstractThemeableEditor fEditor;

	/**
	 * Code Folding.
	 */
	private List<Position> fPositions = new ArrayList<Position>();

	private IProgressMonitor fMonitor;

	private IFoldingComputer folder;

	public CommonReconcilingStrategy(AbstractThemeableEditor editor)
	{
		fEditor = editor;
	}

	public AbstractThemeableEditor getEditor()
	{
		return fEditor;
	}

	public void reconcile(IRegion partition)
	{
		// TODO Only recalculate the folding diff in the dirty region?
		reconcile(false);
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{
		// TODO Only recalculate the folding diff in the dirty region? Requires us to set this as an
		// "incremental reconciler" to get just dirty region
		reconcile(false);
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
		reconcile(true);
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

	protected void calculatePositions(IProgressMonitor monitor)
	{
		if (monitor != null && monitor.isCanceled())
		{
			return;
		}

		FileService fileService = fEditor.getFileService();
		// doing a full parse at the moment
		fileService.parse();
		// abort if parse failed
		if (!fileService.hasValidParseResult())
		{
			return;
		}
		if (monitor != null && monitor.isCanceled())
		{
			return;
		}
		// Folding...
		fPositions.clear();
		try
		{
			fPositions = folder.emitFoldingRegions(monitor);
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
		}
		// If we had all positions we shouldn't probably listen to cancel, but we may have exited emitFoldingRegions
		// early because of cancel...
		if (monitor != null && monitor.isCanceled())
			return;

		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				fEditor.updateFoldingStructure(fPositions);
			}
		});

	}

	private void reconcile(boolean initialReconcile)
	{
		calculatePositions(fMonitor);
	}
}
