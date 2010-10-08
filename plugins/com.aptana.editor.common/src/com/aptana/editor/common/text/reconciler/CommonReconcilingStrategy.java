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

public class CommonReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension
{

	private AbstractThemeableEditor fEditor;

	/**
	 * Code Folding.
	 */
	private List<Position> fPositions = new ArrayList<Position>();

	private IProgressMonitor fMonitor;

	private RubyRegexpFolder folder;

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
		// TODO Only recalculate the folding diff in the dirty region? Requires us to set this as an "incremental reconciler" to get just dirty region
		reconcile(false);
	}

	public void setDocument(IDocument document)
	{
		folder = new RubyRegexpFolder(document);
		fEditor.getFileService().setDocument(document);
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
			return;
		// doing a full parse at the moment
		fEditor.getFileService().parse();
		if (monitor != null && monitor.isCanceled())
			return;
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
