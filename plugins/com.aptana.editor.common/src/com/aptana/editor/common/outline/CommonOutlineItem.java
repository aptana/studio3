package com.aptana.editor.common.outline;

import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class CommonOutlineItem implements IRange, Comparable<IRange>
{

	private IRange fSourceRange;
	private IParseNode fReferenceNode;

	public CommonOutlineItem(IRange sourceRange, IParseNode referenceNode)
	{
		fSourceRange = sourceRange;
		fReferenceNode = referenceNode;
	}

	public IRange getSourceRange()
	{
		return fSourceRange;
	}

	public IParseNode getReferenceNode()
	{
		return fReferenceNode;
	}

	@Override
	public int getEndingOffset()
	{
		return fSourceRange.getEndingOffset();
	}

	@Override
	public int getLength()
	{
		return fSourceRange.getLength();
	}

	@Override
	public int getStartingOffset()
	{
		return fSourceRange.getStartingOffset();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CommonOutlineItem))
		{
			return false;
		}
		return fReferenceNode.equals(((CommonOutlineItem) obj).fReferenceNode);
	}

	@Override
	public int hashCode()
	{
		return fReferenceNode.hashCode();
	}

	@Override
	public int compareTo(IRange o)
	{
		return getStartingOffset() - o.getStartingOffset();
	}
}
