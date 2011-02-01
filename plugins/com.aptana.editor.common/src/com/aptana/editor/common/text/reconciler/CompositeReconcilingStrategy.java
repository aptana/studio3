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
public class CompositeReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension
{

	/** The list of internal reconciling strategies. */
	private IReconcilingStrategy[] fStrategies;

	/**
	 * Creates a new, empty composite reconciling strategy.
	 */
	public CompositeReconcilingStrategy()
	{
	}

	/**
	 * Sets the reconciling strategies for this composite strategy.
	 * 
	 * @param strategies
	 *            the strategies to be set or <code>null</code>
	 */
	public void setReconcilingStrategies(IReconcilingStrategy[] strategies)
	{
		fStrategies = strategies;
	}

	/**
	 * Returns the previously set stratgies or <code>null</code>.
	 * 
	 * @return the contained strategies or <code>null</code>
	 */
	public IReconcilingStrategy[] getReconcilingStrategies()
	{
		return fStrategies;
	}

	public void setDocument(IDocument document)
	{
		if (fStrategies == null)
		{
			return;
		}

		for (int i = 0; i < fStrategies.length; i++)
		{
			fStrategies[i].setDocument(document);
		}
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{
		if (fStrategies == null)
		{
			return;
		}

		for (int i = 0; i < fStrategies.length; i++)
		{
			fStrategies[i].reconcile(dirtyRegion, subRegion);
		}
	}

	public void reconcile(IRegion partition)
	{
		if (fStrategies == null)
		{
			return;
		}

		for (int i = 0; i < fStrategies.length; i++)
		{
			fStrategies[i].reconcile(partition);
		}
	}

	public void setProgressMonitor(IProgressMonitor monitor)
	{
		if (fStrategies == null)
		{
			return;
		}

		for (int i = 0; i < fStrategies.length; i++)
		{
			if (fStrategies[i] instanceof IReconcilingStrategyExtension)
			{
				IReconcilingStrategyExtension extension = (IReconcilingStrategyExtension) fStrategies[i];
				extension.setProgressMonitor(monitor);
			}
		}
	}

	public void initialReconcile()
	{
		if (fStrategies == null)
		{
			return;
		}

		for (int i = 0; i < fStrategies.length; i++)
		{
			if (fStrategies[i] instanceof IReconcilingStrategyExtension)
			{
				IReconcilingStrategyExtension extension = (IReconcilingStrategyExtension) fStrategies[i];
				extension.initialReconcile();
			}
		}
	}
}
