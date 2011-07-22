/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.Reconciler;

public class CommonReconciler extends Reconciler {

	private final IReconcilingStrategy defaultStrategy;
	private BundleChangeReconcileTrigger bundleChangeReconcileTrigger;

	/**
	 * Used for performance testing purposes so we can see if we've finished our
	 * first reconcile!
	 */
	@SuppressWarnings("unused")
	private boolean fIninitalProcessDone = false;


	/**
	 * 
	 */
	public CommonReconciler(IReconcilingStrategy defaultStrategy) {
		super();
		this.defaultStrategy = defaultStrategy;
		setReconcilingStrategy(defaultStrategy, String.valueOf(System.currentTimeMillis()));
	}

	/**
	 * 
	 * @param strategy
	 * @param contentTypes
	 */
	public void setReconcilingStrategy(IReconcilingStrategy strategy, String[] contentTypes) {
		for (String contentType : contentTypes) {
			setReconcilingStrategy(strategy, contentType);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.Reconciler#getReconcilingStrategy(java.lang.String)
	 */
	@Override
	public IReconcilingStrategy getReconcilingStrategy(String contentType) {
		IReconcilingStrategy strategy = super.getReconcilingStrategy(contentType);
		if (strategy != null) {
			return strategy;
		}
		return defaultStrategy;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#install(org.eclipse.jface.text.ITextViewer)
	 */
	@Override
	public void install(ITextViewer textViewer) {
		super.install(textViewer);
		bundleChangeReconcileTrigger = new BundleChangeReconcileTrigger(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#uninstall()
	 */
	@Override
	public void uninstall() {
		if (bundleChangeReconcileTrigger != null) {
			bundleChangeReconcileTrigger.dispose();
			bundleChangeReconcileTrigger = null;
		}
		super.uninstall();
	}

	@Override
	protected void initialProcess() {
		for (ITypedRegion region : computePartitioning(0, getDocument().getLength())) {
			IReconcilingStrategy strategy = getReconcilingStrategy(region.getType());
			strategy.reconcile(region);
		}
		fIninitalProcessDone = true;
	}

	/**
	 * Computes and returns the partitioning for the given region of the input document
	 * of the reconciler's connected text viewer.
	 *
	 * @param offset the region offset
	 * @param length the region length
	 * @return the computed partitioning
	 * @since 3.0
	 */
	private ITypedRegion[] computePartitioning(int offset, int length) {
		ITypedRegion[] regions= null;
		try {
			regions= TextUtilities.computePartitioning(getDocument(), getDocumentPartitioning(), offset, length, false);
		} catch (BadLocationException x) {
			regions= new TypedRegion[0];
		}
		return regions;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#forceReconciling()
	 */
	@Override
	public void forceReconciling() {
		super.forceReconciling();
	}
}
