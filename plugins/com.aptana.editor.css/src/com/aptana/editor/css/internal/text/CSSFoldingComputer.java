package com.aptana.editor.css.internal.text;

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
import com.aptana.editor.css.parsing.ast.CSSCommentNode;
import com.aptana.editor.css.parsing.ast.CSSFontFaceNode;
import com.aptana.editor.css.parsing.ast.CSSMediaNode;
import com.aptana.editor.css.parsing.ast.CSSPageNode;
import com.aptana.editor.css.parsing.ast.CSSRuleNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class CSSFoldingComputer implements IFoldingComputer
{

	private IDocument fDocument;
	private AbstractThemeableEditor fEditor;

	public CSSFoldingComputer(AbstractThemeableEditor editor, IDocument document)
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
			if ((child instanceof CSSCommentNode) || (child instanceof CSSRuleNode) || (child instanceof CSSMediaNode)
					|| (child instanceof CSSPageNode) || (child instanceof CSSFontFaceNode))
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
					// If start + length + 1 goes past end of document, it fails!
					int toAdd = 1;
					if (start + child.getLength() + 1 >= fDocument.getLength())
					{
						toAdd = 0;
					}
					newPositions.add(new Position(start, child.getLength() + toAdd));
				}
			}
			if (((child instanceof ParseRootNode) || (child instanceof CSSMediaNode) || (child instanceof CSSPageNode))
					&& child.hasChildren())
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
