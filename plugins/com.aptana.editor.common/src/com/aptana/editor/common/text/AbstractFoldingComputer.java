package com.aptana.editor.common.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.common.text.reconciler.Messages;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * An abstract base class to use to quickly implement an {@link IFoldingComputer} based on traversing an AST.
 * 
 * @author cwilliams
 */
public abstract class AbstractFoldingComputer implements IFoldingComputer
{

	private IDocument fDocument;
	private AbstractThemeableEditor fEditor;
	private ArrayList<Integer> fLines;

	public AbstractFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		super();
		this.fEditor = editor;
		this.fDocument = document;
	}

	protected IDocument getDocument()
	{
		return fDocument;
	}

	protected AbstractThemeableEditor getEditor()
	{
		return fEditor;
	}

	protected IParseNode getAST()
	{
		return getEditor().getFileService().getParseResult();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.reconciler.IFoldingComputer#emitFoldingRegions(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public synchronized List<Position> emitFoldingRegions(IProgressMonitor monitor) throws BadLocationException
	{
		fLines = new ArrayList<Integer>();
		int lineCount = getDocument().getNumberOfLines();
		if (lineCount <= 1) // Quick hack fix for minified files. We need at least two lines to have folding!
		{
			return Collections.emptyList();
		}
		SubMonitor sub = null;
		try
		{
			IParseNode parseNode = getAST();
			if (parseNode == null)
			{
				return Collections.emptyList();
			}
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
			sub = SubMonitor.convert(monitor, Messages.CommonReconcilingStrategy_FoldingTaskName, length);
			return getPositions(sub.newChild(length), parseNode);
		}
		finally
		{
			fLines = null;
			if (sub != null)
			{
				sub.done();
			}
		}
	}

	protected IParseNode[] getChildren(IParseNode parseNode)
	{
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
		return children;
	}

	private List<Position> getPositions(IProgressMonitor monitor, IParseNode parseNode)
	{
		List<Position> newPositions = new ArrayList<Position>();
		IParseNode[] children = getChildren(parseNode);
		SubMonitor sub = SubMonitor.convert(monitor, 2 * children.length);
		for (IParseNode child : children)
		{
			if (sub.isCanceled())
			{
				return newPositions;
			}
			if (isFoldable(child))
			{
				// FIXME We had hacks for the length for each language before. Do we need hooks to override that here?
				int start = child.getStartingOffset();
				boolean add = true;
				int end = child.getEndingOffset() + 1;
				try
				{
					int line = getDocument().getLineOfOffset(start);
					// Don't bother adding multiple positions for the same starting line
					if (fLines.contains(line))
					{
						add = false;
					}
					else
					{
						// Don't set up folding for stuff starting and ending on same line
						int endLine = getDocument().getLineOfOffset(child.getEndingOffset());
						if (endLine == line)
						{
							add = false;
						}
						else
						{
							// When we can, use the end of the end line as the end offset, so it looks nicer in the
							// editor.
							IRegion endLineRegion = getDocument().getLineInformation(endLine);
							end = endLineRegion.getOffset() + endLineRegion.getLength() + 1;
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
					end = Math.min(getDocument().getLength(), end);
					newPositions.add(new Position(start, end - start));
				}
			}
			if (traverseInto(child))
			{
				// Recurse into AST!
				newPositions.addAll(getPositions(sub.newChild(1), child));
			}
			sub.worked(1);
		}
		sub.done();
		return newPositions;
	}

	/**
	 * Is this a node type we want folding for?
	 * 
	 * @param child
	 * @return
	 */
	protected abstract boolean isFoldable(IParseNode child);

	/**
	 * Should we try traversing into this node and it's children? Base implementation is yes if it has children.
	 * Subclasses may want to override to avoid traversing into node types we know have children but nothing of interest
	 * for folding.
	 * 
	 * @param child
	 * @return
	 */
	protected boolean traverseInto(IParseNode child)
	{
		return child.hasChildren();
	}

}