/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;

import com.aptana.editor.common.AbstractThemeableEditor;

public class CommonCompositeReconcilingStrategy extends CompositeReconcilingStrategy
{

	private CommonReconcilingStrategy fStrategy;

	/**
	 * Creates a new common reconciling strategy.
	 * 
	 * @param editor
	 *            the editor of the strategy's reconciler
	 * @param documentPartitioning
	 *            the document partitioning this strategy uses for configuration
	 */
	public CommonCompositeReconcilingStrategy(AbstractThemeableEditor editor, String documentPartitioning)
	{
		fStrategy = new CommonReconcilingStrategy(editor);
		// TODO: adds spell-checking reconciling strategy
		setReconcilingStrategies(new IReconcilingStrategy[] { fStrategy });
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{
		try
		{
			super.reconcile(dirtyRegion, subRegion);
		}
		finally
		{
			reconciled();
		}
	}

	public void reconcile(IRegion partition)
	{
		try
		{
			super.reconcile(partition);				
		}
		finally
		{
			reconciled();
		}
	}

	/**
	 * Tells this strategy whether to inform its listeners.
	 * 
	 * @param notify
	 *            <code>true</code> if listeners should be notified
	 */
	public void notifyListeners(boolean notify)
	{
		fStrategy.notifyListeners(notify);
	}

	public void initialReconcile()
	{
		try
		{
			super.initialReconcile();
		}
		finally
		{
			reconciled();
		}
	}

	/**
	 * Called before reconciling is started.
	 */
	public void aboutToBeReconciled()
	{
		fStrategy.aboutToBeReconciled();
	}

	/**
	 * Called when reconcile has finished.
	 */
	private void reconciled()
	{
		fStrategy.reconciled();
	}
}
