package com.aptana.editor.common.text.reconciler;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

public interface IFoldingComputer
{

	public abstract Map<ProjectionAnnotation, Position> emitFoldingRegions(boolean initialReconcile,
			IProgressMonitor monitor)
			throws BadLocationException;

}