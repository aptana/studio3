package com.aptana.parsing.ast;

import beaver.spec.ast.Node;
import beaver.spec.ast.TreeWalker;

public class ParseBaseNode extends Node implements IParseNode
{

	private IParseNode[] fChildren;
	private IParseNode fParent;

	public ParseBaseNode()
	{
		fChildren = new IParseNode[0];
	}

	public void addChild(IParseNode child)
	{
		// could use a more efficient way (e.g. doubling the array size when needed each time), but addChild() is not
		// called often while setChildren() is the more preferred method
		IParseNode[] newList = new IParseNode[fChildren.length + 1];
		System.arraycopy(fChildren, 0, newList, 0, fChildren.length);
		fChildren = newList;
		fChildren[fChildren.length - 1] = child;
	}

	@Override
	public void accept(TreeWalker walker)
	{
	}

	@Override
	public IParseNode[] getChildren()
	{
		return fChildren;
	}

	@Override
	public int getEndingOffset()
	{
		return getEnd();
	}

	@Override
	public int getLength()
	{
		return getEnd() - getStart() + 1;
	}

	@Override
	public IParseNode getParent()
	{
		return fParent;
	}

	@Override
	public int getStartingOffset()
	{
		return getStart();
	}

	@Override
	public String getText()
	{
		return toString();
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		int count = fChildren.length;
		for (int i = 0; i < count; ++i)
		{
			text.append(fChildren[i]);
			if (i < count - 1)
			{
				text.append(" "); //$NON-NLS-1$
			}
		}
		return text.toString();
	}

	public void setParent(IParseNode parent)
	{
		fParent = parent;
	}

	protected void setChildren(IParseNode[] children)
	{
		fChildren = children;
		for (IParseNode child : children)
		{
			((ParseBaseNode) child).setParent(this);
		}
	}
}
