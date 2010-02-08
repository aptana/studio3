/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Display;
import org.jruby.RubyFixnum;
import org.jruby.RubyMatchData;
import org.jruby.RubyNumeric;
import org.jruby.RubyRegexp;
import org.jruby.RubyString;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.scripting.model.BundleManager;

public class CommonReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension
{

	private AbstractThemeableEditor fEditor;

	/**
	 * Code Folding.
	 */
	final List<Position> fPositions = new ArrayList<Position>();

	private IDocument fDocument;
	private IProgressMonitor fMonitor;

	public CommonReconcilingStrategy(AbstractThemeableEditor editor)
	{
		fEditor = editor;
	}

	public AbstractThemeableEditor getEditor()
	{
		return fEditor;
	}

	@Override
	public void reconcile(IRegion partition)
	{
		// TODO Only recalculate the folding diff in the dirty region?
		reconcile(false);
	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{
		// TODO Only recalculate the folding diff in the dirty region?
		reconcile(false);
	}

	@Override
	public void setDocument(IDocument document)
	{
		fDocument = document;
		fEditor.getFileService().setDocument(document);
	}

	@Override
	public void initialReconcile()
	{
		reconcile(true);
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor)
	{
		fMonitor = monitor;
	}

	public void aboutToBeReconciled()
	{
	}

	public void notifyListeners(boolean notify)
	{
	}

	public void reconciled()
	{
	}

	protected void calculatePositions(IProgressMonitor monitor)
	{
		// doing a full parse at the moment
		fEditor.getFileService().parse();

		fPositions.clear();
		try
		{
			emitFoldingRegions(monitor);
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
		}

		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				fEditor.updateFoldingStructure(fPositions);
			}
		});

	}

	private void emitFoldingRegions(IProgressMonitor monitor) throws BadLocationException
	{
		Map<Integer, Integer> starts = new HashMap<Integer, Integer>();
		if (monitor != null)
		{
			monitor.beginTask(Messages.CommonReconcilingStrategy_FoldingTaskName, -1);
		}

		// Go through the partitions, in each partition go through each line and match regexps for that scope
		// against the line. Regexps are contributed by the bundles for given scopes
		ITypedRegion[] partitions = fDocument.getDocumentPartitioner().computePartitioning(0, fDocument.getLength());
		for (ITypedRegion region : partitions)
		{
			int offset = region.getOffset();
			int length = region.getLength();

			String scope = CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(fDocument, offset);

			RubyRegexp startRegexp = getStartFoldRegexp(scope);
			if (startRegexp == null)
				continue;
			RubyRegexp endRegexp = getEndFoldRegexp(scope);
			if (endRegexp == null)
				continue;

			String partitionText = fDocument.get(offset, length);
			String[] lines = partitionText.split("\r|\n|\r\n"); //$NON-NLS-1$
			for (String line : lines)
			{
				// Look for an open...
				RubyString rLine = startRegexp.getRuntime().newString(line);
				IRubyObject startMatcher = startRegexp.match_m(startRegexp.getRuntime().getCurrentContext(), rLine);
				if (!startMatcher.isNil())
				{
					int start = 0;
					IRubyObject posStart = ((RubyMatchData) startMatcher).begin(startRegexp.getRuntime()
							.getCurrentContext(), startRegexp.getRuntime().newFixnum(0));
					if (posStart instanceof RubyFixnum)
					{
						start = RubyNumeric.num2int(posStart);
					}
					starts.put(findIndent(line), start + offset); // need to push the indent level too...
				}
				// Don't look for an end if there's no open yet!
				if (starts.size() > 0)
				{
					// check to see if we have an open folding region at this indent level...
					int indent = findIndent(line);
					if (starts.containsKey(indent))
					{
						IRubyObject endMatcher = endRegexp.match_m(endRegexp.getRuntime().getCurrentContext(), rLine);
						if (!endMatcher.isNil())
						{
							int startingOffset = starts.remove(indent);

							int end = 0;
							IRubyObject posStart = ((RubyMatchData) endMatcher).end(endRegexp.getRuntime()
									.getCurrentContext(), endRegexp.getRuntime().newFixnum(0));
							if (posStart instanceof RubyFixnum)
							{
								end = RubyNumeric.num2int(posStart);
							}

							int posLength = (end + offset) - startingOffset;
							if (posLength > 0)
							{
								// FIXME Don't add if the start and end are on the same line
								Position position = new Position(startingOffset, posLength);
								fPositions.add(position);
							}
						}
					}
				}
				offset += line.length() + 1; // FIXME This assumes line delimiter is 1 char!
			}
		}

		if (monitor != null)
		{
			monitor.done();
		}
	}

	private int findIndent(String text)
	{
		// TODO Handle tab characters and expanding them out to their tab width?
		int indent = 0;
		while (indent < text.length())
		{
			if (Character.isWhitespace(text.charAt(indent)))
			{
				indent++;
			}
			else
			{
				break;
			}
		}

		return indent;
	}

	private RubyRegexp getEndFoldRegexp(String scope)
	{
		return BundleManager.getInstance().getFoldingStopRegexp(scope);
	}

	private RubyRegexp getStartFoldRegexp(String scope)
	{
		return BundleManager.getInstance().getFoldingStartRegexp(scope);
	}

	private void reconcile(boolean initialReconcile)
	{
		calculatePositions(fMonitor);
	}
}
