package com.aptana.parsing.ast;

import beaver.spec.ast.Node;
import beaver.spec.ast.TreeWalker;

import com.aptana.parsing.lexer.IRange;

public class ParseBaseNode extends Node implements IParseNode
{

	private IParseNode[] fChildren;
	private IParseNode fParent;
	private int fChildrenCount;

	private String fLanguage;

	public ParseBaseNode(String language)
	{
		fLanguage = language;
		fChildren = new IParseNode[0];
	}

	public void addChild(IParseNode child)
	{
		// makes sure our private buffer is large enough
		int currentLength = fChildren.length;
		int size = fChildrenCount + 1;
		if (size > currentLength)
		{
			// it's not, so adds about 50% to our current buffer size
			int newLength = (currentLength * 3) / 2 + 1;
			// creates a new empty list
			IParseNode[] newList = new IParseNode[newLength];
			// moves the current contents to our new list
			System.arraycopy(fChildren, 0, newList, 0, fChildrenCount);
			// sets our current list to the new list
			fChildren = newList;
		}
		fChildren[fChildrenCount++] = child;
		if (child instanceof ParseBaseNode)
		{
			((ParseBaseNode) child).setParent(this);
		}
	}

	public void addOffset(int offset)
	{
		setLocation(getStart() + offset, getEnd() + offset);
	}

	@Override
	public void accept(TreeWalker walker)
	{
	}

	@Override
	public IParseNode getChild(int index)
	{
		if (index >= 0 && index < fChildrenCount)
		{
			return fChildren[index];
		}
		return null;
	}

	@Override
	public IParseNode[] getChildren()
	{
		IParseNode[] result = new IParseNode[fChildrenCount];
		if (fChildrenCount > 0)
		{
			System.arraycopy(fChildren, 0, result, 0, fChildrenCount);
		}
		return result;
	}

	@Override
	public int getChildrenCount()
	{
		return fChildrenCount;
	}

	@Override
	public IParseNode getElementAt(int offset)
	{
		if (offset < getStartingOffset() || offset > getEndingOffset())
		{
			// not in this node
			return null;
		}
		IParseNode[] children = getChildren();
		for (IParseNode child : children)
		{
			if (child.getStartingOffset() <= offset && offset <= child.getEndingOffset())
			{
				return child.getElementAt(offset);
			}
		}
		return this;
	}

	@Override
	public int getEndingOffset()
	{
		return getEnd();
	}

	@Override
	public int getIndex(IParseNode child)
	{
		for (int i = 0; i < fChildrenCount; ++i)
		{
			if (fChildren[i] == child)
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	public String getLanguage()
	{
		return fLanguage;
	}

	@Override
	public int getLength()
	{
		return getEnd() - getStart() + 1;
	}

	@Override
	public INameNode getNameNode()
	{
		return new INameNode()
		{

			@Override
			public String getName()
			{
				return getText();
			}

			@Override
			public IRange getNameRange()
			{
				return ParseBaseNode.this;
			}
		};
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
		return ""; //$NON-NLS-1$
	}

	@Override
	public short getType()
	{
		return getId();
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < fChildrenCount; ++i)
		{
			text.append(fChildren[i]);
			if (i < fChildrenCount - 1)
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

	public void setChildren(IParseNode[] children)
	{
		fChildren = children;
		fChildrenCount = children.length;
		for (IParseNode child : children)
		{
			((ParseBaseNode) child).setParent(this);
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		// Must be a parse node
		if (!(obj instanceof IParseNode))
			return false;

		IParseNode other = (IParseNode) obj;
		// Must be same language
		if (!getLanguage().equals(other.getLanguage()))
			return false;

		// Must have same parent
		if (getParent() == null)
		{
			if (other.getParent() != null)
				return false;
		}
		else if (!getParent().equals(other.getParent()))
			return false;

		// Same type
		if (getType() != other.getType())
			return false;

		// That's about the best we can check from here, since offsets can change a lot. Should really also check
		// identifier/name
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 31 + getLanguage().hashCode();
		hash = hash * 31 + getType();
		hash = hash * 31 + (getParent() == null ? 0 : getParent().hashCode());
		return hash;
	}
}
