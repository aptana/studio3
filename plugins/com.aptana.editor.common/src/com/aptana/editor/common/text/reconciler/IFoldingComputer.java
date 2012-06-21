/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

import com.aptana.parsing.ast.IParseRootNode;

public interface IFoldingComputer
{

	public abstract Map<ProjectionAnnotation, Position> emitFoldingRegions(boolean initialReconcile,
			IProgressMonitor monitor, IParseRootNode ast) throws BadLocationException;

}