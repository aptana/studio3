package com.aptana.editor.common;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;

public class CommonReconcilingStrategy implements IReconcilingStrategy,
        IReconcilingStrategyExtension {

    @Override
    public void reconcile(IRegion partition) {
    }

    @Override
    public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
    }

    @Override
    public void setDocument(IDocument document) {
    }

    @Override
    public void initialReconcile() {
    }

    @Override
    public void setProgressMonitor(IProgressMonitor monitor) {
    }
}
