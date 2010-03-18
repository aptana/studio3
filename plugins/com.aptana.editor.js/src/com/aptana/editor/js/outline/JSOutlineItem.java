package com.aptana.editor.js.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.parsing.ast.ILanguageNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class JSOutlineItem implements IRange, ILanguageNode, Comparable<IRange>
{

	public static enum Type
	{
		PROPERTY, ARRAY, BOOLEAN, FUNCTION, NULL, NUMBER, OBJECT_LITERAL, REGEX, STRING
	}

	private String fLabel;
	private Type fType;
	private IRange fSourceRange;
	private IParseNode fReferenceNode;
	private int fChildrenCount;

	private List<IParseNode> fVirtualChildren;

	public JSOutlineItem(String label, Type type, IRange sourceRange, IParseNode referenceNode)
	{
		this(label, type, sourceRange, referenceNode, 0);
	}

	public JSOutlineItem(String label, Type type, IRange sourceRange, IParseNode referenceNode, int childrenCount)
	{
		fLabel = label;
		fType = type;
		fSourceRange = sourceRange;
		fReferenceNode = referenceNode;
		fChildrenCount = childrenCount;
	}

	public void addVirtualChild(IParseNode target)
	{
		if (fVirtualChildren == null)
		{
			fVirtualChildren = new ArrayList<IParseNode>();
		}
		fVirtualChildren.add(target);
	}

	public int getChildrenCount()
	{
		if (hasVirtualChildren())
		{
			return fChildrenCount + fVirtualChildren.size();
		}
		return fChildrenCount;
	}

	public String getLabel()
	{
		return fLabel;
	}

	public IParseNode getReferenceNode()
	{
		return fReferenceNode;
	}

	public IParseNode[] getAllReferenceNodes()
	{
		if (hasVirtualChildren())
		{
			IParseNode[] result = new IParseNode[fVirtualChildren.size() + 1];
			result = fVirtualChildren.toArray(result);
			result[result.length - 1] = getReferenceNode();
			return result;
		}
		return new IParseNode[] { getReferenceNode() };
	}

	public Type getType()
	{
		return fType;
	}

	private boolean hasVirtualChildren()
	{
		return fVirtualChildren != null && fVirtualChildren.size() > 0;
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
	public String getLanguage()
	{
		return fReferenceNode.getLanguage();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof JSOutlineItem))
		{
			return false;
		}
		JSOutlineItem other = (JSOutlineItem) obj;
		return fLabel.equals(other.fLabel) && fSourceRange.equals(other.fSourceRange);
	}

	@Override
	public int hashCode()
	{
		return 31 * fLabel.hashCode() + fSourceRange.hashCode();
	}

	@Override
	public int compareTo(IRange o)
	{
		return getStartingOffset() - o.getStartingOffset();
	}
}
