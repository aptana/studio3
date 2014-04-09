/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.reconciler.Reconciler;

public class CommonReconciler extends Reconciler
{

	private IReconcilingStrategy defaultStrategy;
	private final Set<IReconcilingStrategy> reconcilingStrategies = new HashSet<IReconcilingStrategy>();
	private BundleChangeReconcileTrigger bundleChangeReconcileTrigger;

	/**
	 * Used for performance testing purposes so we can see if we've finished our first reconcile!
	 */
	@SuppressWarnings("unused")
	private boolean fInitialProcessDone = false;

	/**
	 * 
	 */
	public CommonReconciler(IReconcilingStrategy defaultStrategy)
	{
		super();
		this.defaultStrategy = defaultStrategy;
		setReconcilingStrategy(defaultStrategy, String.valueOf(System.currentTimeMillis()));
	}

	public void dispose()
	{
		if (defaultStrategy instanceof IDisposableReconcilingStrategy)
		{
			((IDisposableReconcilingStrategy) defaultStrategy).dispose();
		}
		defaultStrategy = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.Reconciler#setReconcilingStrategy(org.eclipse.jface.text.reconciler.
	 * IReconcilingStrategy, java.lang.String)
	 */
	@Override
	public void setReconcilingStrategy(IReconcilingStrategy strategy, String contentType)
	{
		super.setReconcilingStrategy(strategy, contentType);
		reconcilingStrategies.add(strategy);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.Reconciler#getReconcilingStrategy(java.lang.String)
	 */
	@Override
	public IReconcilingStrategy getReconcilingStrategy(String contentType)
	{
		IReconcilingStrategy strategy = super.getReconcilingStrategy(contentType);
		if (strategy != null)
		{
			return strategy;
		}
		return defaultStrategy;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#install(org.eclipse.jface.text.ITextViewer)
	 */
	@Override
	public void install(ITextViewer textViewer)
	{
		super.install(textViewer);
		bundleChangeReconcileTrigger = new BundleChangeReconcileTrigger(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#uninstall()
	 */
	@Override
	public void uninstall()
	{
		if (bundleChangeReconcileTrigger != null)
		{
			bundleChangeReconcileTrigger.dispose();
			bundleChangeReconcileTrigger = null;
		}
		super.uninstall();
	}

	@Override
	protected void process(DirtyRegion dirtyRegion)
	{
		IRegion region = dirtyRegion;

		if (region == null)
		{
			// TODO Can we cheat for strategies that do batch and incremental and just do the batch once?
			region = new Region(0, getDocument().getLength());
		}

		Set<IBatchReconcilingStrategy> batch = new HashSet<IBatchReconcilingStrategy>();
		ITypedRegion[] regions = computePartitioning(region.getOffset(), region.getLength());
		for (int i = 0; i < regions.length; i++)
		{
			ITypedRegion r = regions[i];
			IReconcilingStrategy s = getReconcilingStrategy(r.getType());
			if (s == null)
			{
				continue;
			}

			if (s instanceof IBatchReconcilingStrategy)
			{
				batch.add((IBatchReconcilingStrategy) s);
			}

			if (dirtyRegion != null)
			{
				s.reconcile(dirtyRegion, r);
			}
			else
			{
				s.reconcile(r);
			}
		}
		// Now run
		for (IBatchReconcilingStrategy batcher : batch)
		{
			batcher.fullReconcile();
		}
	}

	protected ITypedRegion[] computePartitioning(int offset, int length)
	{
		ITypedRegion[] regions = null;
		try
		{
			regions = TextUtilities
					.computePartitioning(getDocument(), getDocumentPartitioning(), offset, length, false);
		}
		catch (BadLocationException x)
		{
			regions = new TypedRegion[0];
		}
		return regions;
	}

	@Override
	protected void initialProcess()
	{
		for (IReconcilingStrategy s : reconcilingStrategies)
		{
			if (s instanceof IReconcilingStrategyExtension)
			{
				((IReconcilingStrategyExtension) s).initialReconcile();
			}
		}
		fInitialProcessDone = true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#forceReconciling()
	 */
	@Override
	public void forceReconciling()
	{
		super.forceReconciling();
	}
}
