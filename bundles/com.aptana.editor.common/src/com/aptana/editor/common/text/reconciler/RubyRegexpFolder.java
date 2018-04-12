/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.jruby.RubyRegexp;
import org.jruby.RubyString;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.scripting.model.BundleManager;

// FIXME Move this to an internal package!
public class RubyRegexpFolder implements IFoldingComputer
{

	private IDocument fDocument;
	private AbstractThemeableEditor fEditor;

	public RubyRegexpFolder(AbstractThemeableEditor editor, IDocument document)
	{
		this.fDocument = document;
		this.fEditor = editor;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.reconciler.IFoldingComputer#emitFoldingRegions(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public Map<ProjectionAnnotation, Position> emitFoldingRegions(boolean initialReconcile, IProgressMonitor monitor,
			IParseRootNode ast) throws BadLocationException
	{
		int lineCount = fDocument.getNumberOfLines();
		if (lineCount <= 1) // Quick hack fix for minified files. We need at least two lines to have folding!
		{
			return Collections.emptyMap();
		}
		Map<ProjectionAnnotation, Position> newPositions = new HashMap<ProjectionAnnotation, Position>(lineCount >> 2);
		Map<Integer, Integer> starts = new HashMap<Integer, Integer>(3);
		if (monitor != null)
		{
			monitor.beginTask(Messages.CommonReconcilingStrategy_FoldingTaskName, lineCount);
		}
		for (int currentLine = 0; currentLine < lineCount; currentLine++)
		{
			// Check for cancellation
			if (monitor != null && monitor.isCanceled())
				return newPositions;

			IRegion lineRegion = fDocument.getLineInformation(currentLine);
			int offset = lineRegion.getOffset();
			String line = fDocument.get(offset, lineRegion.getLength());

			// Use scope at beginning of line for start regexp
			RubyRegexp startRegexp = getStartFoldRegexp(getScopeAtOffset(offset));
			if (startRegexp == null)
			{
				if (monitor != null)
					monitor.worked(1);
				continue;
			}
			// Use scope at end of line for end regexp
			RubyRegexp endRegexp = getEndFoldRegexp(getScopeAtOffset(offset + lineRegion.getLength()));
			if (endRegexp == null)
			{
				if (monitor != null)
					monitor.worked(1);
				continue;
			}
			// Look for an open...
			RubyString rLine = startRegexp.getRuntime().newString(line);
			IRubyObject startMatcher = startRegexp.match_m(startRegexp.getRuntime().getCurrentContext(), rLine);
			if (!startMatcher.isNil())
			{
				starts.put(findIndent(line), offset); // cheat and just give offset of line since line resolution is all
														// that matters
			}
			// Don't look for an end if there's no open yet!
			if (starts.size() > 0)
			{
				// check to see if we have an open folding region at this indent level...
				int indent = findIndent(line);
				// Subtract one if we're handling /* */ folding!
				if (line.trim().startsWith("*")) //$NON-NLS-1$
				{
					indent--;
				}
				if (starts.containsKey(indent))
				{
					IRubyObject endMatcher = endRegexp.match_m(endRegexp.getRuntime().getCurrentContext(), rLine);
					if (!endMatcher.isNil())
					{
						int startingOffset = starts.remove(indent);
						int startLine = fDocument.getLineOfOffset(startingOffset);
						if (startLine != currentLine)
						{
							int end = lineRegion.getOffset() + lineRegion.getLength() + 1; // cheat and just use end of
																							// line
							if (end > fDocument.getLength())
							{
								end = fDocument.getLength();
							}
							int posLength = end - startingOffset;
							if (posLength > 0)
							{
								Position position = new Position(startingOffset, posLength);
								newPositions.put(new ProjectionAnnotation(), position);
							}
						}
					}
				}
			}
			if (monitor != null)
				monitor.worked(1);
		}

		if (monitor != null)
		{
			monitor.done();
		}
		return newPositions;
	}

	protected String getScopeAtOffset(int offset) throws BadLocationException
	{
		if (fEditor != null)
		{
			ISourceViewer sv = fEditor.getISourceViewer();
			if (sv != null)
			{
				return getDocumentScopeManager().getScopeAtOffset(sv, offset);
			}
		}
		return getDocumentScopeManager().getScopeAtOffset(fDocument, offset);
	}

	protected IDocumentScopeManager getDocumentScopeManager()
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager();
	}

	private int findIndent(String text)
	{
		// TODO Handle tab characters and expanding them out to their tab width?
		int indent = 0;
		while (indent < text.length())
		{
			if (!Character.isWhitespace(text.charAt(indent)))
				break;
			indent++;
		}

		return indent;
	}

	protected RubyRegexp getEndFoldRegexp(String scope)
	{
		return BundleManager.getInstance().getFoldingStopRegexp(scope);
	}

	protected RubyRegexp getStartFoldRegexp(String scope)
	{
		return BundleManager.getInstance().getFoldingStartRegexp(scope);
	}

}
