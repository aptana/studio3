package com.aptana.editor.common.text.reconciler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.jruby.RubyFixnum;
import org.jruby.RubyMatchData;
import org.jruby.RubyNumeric;
import org.jruby.RubyRegexp;
import org.jruby.RubyString;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.scripting.model.BundleManager;

public class RubyRegexpFolder
{

	private IDocument fDocument;

	RubyRegexpFolder(IDocument document)
	{
		this.fDocument = document;
	}

	public List<Position> emitFoldingRegions(List<Position> positions, IProgressMonitor monitor) throws BadLocationException
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

			String scope = CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(fDocument, offset);

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
