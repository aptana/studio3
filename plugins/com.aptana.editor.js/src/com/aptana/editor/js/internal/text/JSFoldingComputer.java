package com.aptana.editor.js.internal.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.common.text.reconciler.Messages;
import com.aptana.editor.js.parsing.ast.JSArgumentsNode;
import com.aptana.editor.js.parsing.ast.JSArrayNode;
import com.aptana.editor.js.parsing.ast.JSCommentNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGroupNode;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.parsing.ast.JSParametersNode;
import com.aptana.editor.js.parsing.ast.JSStatementsNode;
import com.aptana.editor.js.parsing.ast.JSSwitchNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class JSFoldingComputer implements IFoldingComputer
{

	private IDocument fDocument;
	private AbstractThemeableEditor fEditor;

	public JSFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		this.fDocument = document;
		this.fEditor = editor;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.reconciler.IFoldingComputer#emitFoldingRegions(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public List<Position> emitFoldingRegions(IProgressMonitor monitor) throws BadLocationException
	{
		int lineCount = fDocument.getNumberOfLines();
		if (lineCount <= 1) // Quick hack fix for minified files. We need at least two lines to have folding!
		{
			return Collections.emptyList();
		}

		IParseNode parseNode = fEditor.getFileService().getParseResult();
		int length = parseNode.getChildCount();
		if (parseNode instanceof ParseRootNode)
		{
			ParseRootNode prn = (ParseRootNode) parseNode;
			IParseNode[] comments = prn.getCommentNodes();
			if (comments != null && comments.length > 0)
			{
				length += comments.length;
			}
		}
		SubMonitor sub = SubMonitor.convert(monitor, Messages.CommonReconcilingStrategy_FoldingTaskName, length);

		List<Position> newPositions = getPositions(sub.newChild(length), parseNode);
		sub.done();
		return newPositions;
	}

	private List<Position> getPositions(IProgressMonitor monitor, IParseNode parseNode)
	{
		List<Position> newPositions = new ArrayList<Position>();
		IParseNode[] children = parseNode.getChildren();
		if (parseNode instanceof ParseRootNode)
		{
			ParseRootNode prn = (ParseRootNode) parseNode;
			IParseNode[] comments = prn.getCommentNodes();
			if (comments != null && comments.length > 0)
			{
				IParseNode[] combined = new IParseNode[children.length + comments.length];
				System.arraycopy(children, 0, combined, 0, children.length);
				System.arraycopy(comments, 0, combined, children.length, comments.length);
				children = combined;
			}
		}
		SubMonitor sub = SubMonitor.convert(monitor, 2 * children.length);

		for (IParseNode child : children)
		{
			if ((child instanceof JSCommentNode) || (child instanceof JSFunctionNode)
					|| (child instanceof JSObjectNode) || (child instanceof JSSwitchNode)
					|| (child instanceof JSStatementsNode) || (child instanceof JSArrayNode)
					|| (child instanceof JSGroupNode) || (child instanceof JSArgumentsNode)
					|| (child instanceof JSParametersNode))
			{
				int start = child.getStartingOffset();
				boolean add = true;
				try
				{
					// Don't set up folding for stuff starting and ending on same line
					int line = fDocument.getLineOfOffset(start);
					int endLine = fDocument.getLineOfOffset(child.getEndingOffset());
					if (endLine == line)
					{
						add = false;
					}
				}
				catch (BadLocationException e)
				{
					// ignore
				}
				if (add)
				{
					int toAdd = 1;
					if (child instanceof JSArgumentsNode || child instanceof JSFunctionNode)
					{
						toAdd = 2;
					}
					// FIXME Use start of line as start position? Don't add another position on same line!
					newPositions.add(new Position(start, child.getLength() + toAdd));
				}
			}
			if (child.hasChildren())
			{
				// Recurse into AST!
				newPositions.addAll(getPositions(sub.newChild(1), child));
			}
			sub.worked(1);
		}
		sub.done();
		return newPositions;
	}
}
