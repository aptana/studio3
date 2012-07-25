/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;

/**
 * A reconciling strategy consisting of a sequence of internal reconciling strategies. By default, all requests are
 * passed on to the contained strategies.
 */
public class CompositeReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension,
		IBatchReconcilingStrategy, IDisposableReconcilingStrategy
{

	/** The list of internal reconciling strategies. */
	private final IReconcilingStrategy[] fStrategies;

	/**
	 * Creates composite reconciling strategy.
	 */
	public CompositeReconcilingStrategy(IReconcilingStrategy... strategies)
	{
		fStrategies = (strategies != null) ? strategies : new IReconcilingStrategy[0];
	}

	public void dispose()
	{
		for (IReconcilingStrategy strategy : fStrategies)
		{
			if (strategy instanceof IDisposableReconcilingStrategy)
			{
				((IDisposableReconcilingStrategy) strategy).dispose();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document)
	{
		for (IReconcilingStrategy strategy : fStrategies)
		{
			strategy.setDocument(document);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion,
	 * org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{
		for (IReconcilingStrategy strategy : fStrategies)
		{
			strategy.reconcile(dirtyRegion, subRegion);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(IRegion partition)
	{
		for (IReconcilingStrategy strategy : fStrategies)
		{
			strategy.reconcile(partition);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#setProgressMonitor(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public void setProgressMonitor(IProgressMonitor monitor)
	{
		for (IReconcilingStrategy strategy : fStrategies)
		{
			if (strategy instanceof IReconcilingStrategyExtension)
			{
				IReconcilingStrategyExtension extension = (IReconcilingStrategyExtension) strategy;
				extension.setProgressMonitor(monitor);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#initialReconcile()
	 */
	public void initialReconcile()
	{
		for (IReconcilingStrategy strategy : fStrategies)
		{
			if (strategy instanceof IReconcilingStrategyExtension)
			{
				IReconcilingStrategyExtension extension = (IReconcilingStrategyExtension) strategy;
				extension.initialReconcile();
			}
		}
	}

	public void fullReconcile()
	{
		for (IReconcilingStrategy strategy : fStrategies)
		{
			if (strategy instanceof IBatchReconcilingStrategy)
			{
				IBatchReconcilingStrategy extension = (IBatchReconcilingStrategy) strategy;
				extension.fullReconcile();
			}
		}
	}
}
