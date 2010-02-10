package com.aptana.editor.common.text.reconciler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.jruby.RubyRegexp;
import org.jruby.RubyString;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.scripting.model.BundleManager;

class RubyRegexpFolder
{

	private IDocument fDocument;

	RubyRegexpFolder(IDocument document)
	{
		this.fDocument = document;
	}

	public List<Position> emitFoldingRegions(List<Position> positions, IProgressMonitor monitor)
			throws BadLocationException
	{
		int lineCount = fDocument.getNumberOfLines();
		Map<Integer, Integer> starts = new HashMap<Integer, Integer>();
		if (monitor != null)
		{
			monitor.beginTask(Messages.CommonReconcilingStrategy_FoldingTaskName, lineCount);
		}
		for (int i = 0; i < lineCount; i++)
		{
			IRegion lineRegion = fDocument.getLineInformation(i);
			int offset = lineRegion.getOffset();
			String line = fDocument.get(offset, lineRegion.getLength());

			String scope = getScopeAtOffset(offset);

			RubyRegexp startRegexp = getStartFoldRegexp(scope);
			if (startRegexp == null)
			{
				if (monitor != null)
					monitor.worked(1);
				continue;
			}
			RubyRegexp endRegexp = getEndFoldRegexp(scope);
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
				if (starts.containsKey(indent))
				{
					IRubyObject endMatcher = endRegexp.match_m(endRegexp.getRuntime().getCurrentContext(), rLine);
					if (!endMatcher.isNil())
					{
						int startingOffset = starts.remove(indent);
						int end = lineRegion.getOffset() + lineRegion.getLength() + 1; // cheat and just use end of line
						if (end > fDocument.getLength())
						{
							end = fDocument.getLength();
						}
						int posLength = end - startingOffset;
						if (posLength > 0)
						{							
							Position position = new Position(startingOffset, posLength);
							positions.add(position);
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
		return positions;
	}

	protected String getScopeAtOffset(int offset) throws BadLocationException
	{
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
