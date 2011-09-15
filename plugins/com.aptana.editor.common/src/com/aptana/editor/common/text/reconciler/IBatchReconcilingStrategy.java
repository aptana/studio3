package com.aptana.editor.common.text.reconciler;

/**
 * Marking interface to denote that this is a reconciler that always assumes reconcile(IRegion) forces a reconcile of
 * the full document.
 * 
 * @author cwilliams
 */
public interface IBatchReconcilingStrategy
{
	public void fullReconcile();
}
