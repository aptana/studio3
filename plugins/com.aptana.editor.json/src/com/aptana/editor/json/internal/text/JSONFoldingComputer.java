package com.aptana.editor.json.internal.text;

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
import com.aptana.editor.json.parsing.ast.JSONArrayNode;
import com.aptana.editor.json.parsing.ast.JSONObjectNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class JSONFoldingComputer implements IFoldingComputer
{

	private IDocument fDocument;
	private AbstractThemeableEditor fEditor;
	private ArrayList<Integer> fLines;

	public JSONFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		this.fDocument = document;
		this.fEditor = editor;
	}

	protected IDocument getDocument()
	{
		return fDocument;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.reconciler.IFoldingComputer#emitFoldingRegions(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public List<Position> emitFoldingRegions(IProgressMonitor monitor) throws BadLocationException
	{
		fLines = new ArrayList<Integer>();
		int lineCount = fDocument.getNumberOfLines();
		if (lineCount <= 1) // Quick hack fix for minified files. We need at least two lines to have folding!
		{
			return Collections.emptyList();
		}

		IParseNode parseNode = getAST();
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
		fLines = null;
		return newPositions;
	}

	protected IParseNode getAST()
	{
		return fEditor.getFileService().getParseResult();
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
			if ((child instanceof JSONObjectNode) || (child instanceof JSONArrayNode))
			{
				int start = child.getStartingOffset();
				boolean add = true;
				try
				{
					int line = fDocument.getLineOfOffset(start);
					// Don't bother adding multiple positions for the same starting line
					if (fLines.contains(line))
					{
						add = false;
					}
					else
					{
						// Don't set up folding for stuff starting and ending on same line
						int endLine = fDocument.getLineOfOffset(child.getEndingOffset());
						if (endLine == line)
						{
							add = false;
						}
						else
						{
							fLines.add(line);
						}
					}
				}
				catch (BadLocationException e)
				{
					// ignore
				}
				if (add)
				{
					int length = child.getLength() + 1;
					int end = Math.min(getDocument().getLength(), start + length);
					newPositions.add(new Position(start, end - start));
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
