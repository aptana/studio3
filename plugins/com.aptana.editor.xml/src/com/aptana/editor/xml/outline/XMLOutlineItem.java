package com.aptana.editor.xml.outline;

import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

class XMLOutlineItem implements IRange
{

	private IRange fSourceRange;
	private IParseNode fReferenceNode;

	public XMLOutlineItem(IRange sourceRange, IParseNode referenceNode)
	{
		fSourceRange = sourceRange;
		fReferenceNode = referenceNode;
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
		if (!(obj instanceof XMLOutlineItem))
		{
			return false;
		}
		return fReferenceNode.equals(((XMLOutlineItem) obj).fReferenceNode);
	}

	@Override
	public int hashCode()
	{
		return fReferenceNode.hashCode();
	}
}
