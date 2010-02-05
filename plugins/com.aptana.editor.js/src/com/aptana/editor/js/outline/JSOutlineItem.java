package com.aptana.editor.js.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.parsing.ast.ILanguageNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.ILexeme;

public class JSOutlineItem implements ILexeme, ILanguageNode
{

	public static enum Type
	{
		PROPERTY, ARRAY, BOOLEAN, FUNCTION, NULL, NUMBER, OBJECT_LITERAL, REGEX, STRING
	}

	private String fLabel;
	private Type fType;
	private ILexeme fSourceRange;
	private IParseNode fReferenceNode;
	private int fChildrenCount;

	private List<IParseNode> fVirtualChildren;

	public JSOutlineItem(String label, Type type, ILexeme sourceRange, IParseNode referenceNode)
	{
		this(label, type, sourceRange, referenceNode, 0);
	}

	public JSOutlineItem(String label, Type type, ILexeme sourceRange, IParseNode referenceNode, int childrenCount)
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
	public String getText()
	{
		return fSourceRange.getText();
	}

	@Override
	public String getLanguage()
	{
		return fReferenceNode.getLanguage();
	}
}
