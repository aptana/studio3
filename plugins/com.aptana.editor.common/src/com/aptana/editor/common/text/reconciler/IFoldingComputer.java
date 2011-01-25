package com.aptana.editor.common.text.reconciler;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;

public interface IFoldingComputer
{

	public abstract List<Position> emitFoldingRegions(IProgressMonitor monitor) throws BadLocationException;

}