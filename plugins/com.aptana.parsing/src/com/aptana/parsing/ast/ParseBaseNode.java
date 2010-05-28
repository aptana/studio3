package com.aptana.parsing.ast;

import java.util.Iterator;
import java.util.NoSuchElementException;

import beaver.spec.ast.Node;
import beaver.spec.ast.TreeWalker;

import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class ParseBaseNode extends Node implements IParseNode
{
	protected static final class NameNode implements INameNode
	{
		private final String fName;
		private final IRange fRange;

		public NameNode(String name, int start, int end)
		{
			fName = name;
			fRange = new Range(start, end);
		}

		@Override
		public String getName()
		{
			return fName;
		}

		@Override
		public IRange getNameRange()
		{
			return fRange;
		}
	}

	protected static final IParseNode[] NO_CHILDREN = new IParseNode[0];
	protected static final IParseNodeAttribute[] NO_ATTRIBUTES = new IParseNodeAttribute[0];

	private IParseNode[] fChildren;
	private IParseNode fParent;
	private int fChildrenCount;
	private String fLanguage;

	/**
	 * ParseBaseNode
	 * 
	 * @param language
	 */
	public ParseBaseNode(String language)
	{
		fLanguage = language;
		fChildren = new IParseNode[0];
	}

	/*
	 * (non-Javadoc)
	 * @see beaver.spec.ast.Node#accept(beaver.spec.ast.TreeWalker)
	 */
	@Override
	public void accept(TreeWalker walker)
	{
	}

	/**
	 * addChild
	 */
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

	/**
	 * addOffset
	 * 
	 * @param offset
	 */
	public void addOffset(int offset)
	{
		setLocation(getStart() + offset, getEnd() + offset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#contains(int)
	 */
	@Override
	public boolean contains(int offset)
	{
		return this.getStartingOffset() <= offset && offset <= this.getEndingOffset();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getAttributes()
	 */
	@Override
	public IParseNodeAttribute[] getAttributes()
	{
		return NO_ATTRIBUTES;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getChild(int)
	 */
	@Override
	public IParseNode getChild(int index)
	{
		if (index >= 0 && index < fChildrenCount)
		{
			return fChildren[index];
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getChildIndex()
	 */
	@Override
	public int getChildIndex()
	{
		IParseNode parent = getParent();
		int result = 0;

		if (parent != null)
		{
			for (int i = 0; i < parent.getChildrenCount(); i++)
			{
				if (parent.getChild(i) == this)
				{
					result = i;
					break;
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getChildren()
	 */
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getChildrenCount()
	 */
	@Override
	public int getChildrenCount()
	{
		return fChildrenCount;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getElementName()
	 */
	@Override
	public String getElementName()
	{
		return this.getClass().getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getEndingOffset()
	 */
	@Override
	public int getEndingOffset()
	{
		return getEnd();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getIndex(com.aptana.parsing.ast.IParseNode)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getLanguage()
	 */
	@Override
	public String getLanguage()
	{
		return fLanguage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getLength()
	 */
	@Override
	public int getLength()
	{
		return getEnd() - getStart() + 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getNameNode()
	 */
	@Override
	public INameNode getNameNode()
	{
		return new NameNode(getText(), getStartingOffset(), getEndingOffset());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getNodeAt(int)
	 */
	@Override
	public IParseNode getNodeAt(int offset)
	{
		IParseNode result = null;

		if (contains(offset))
		{
			// default to this node being the match
			result = this;

			// but check the children in case one of them contains the offset
			for (IParseNode child : getChildren())
			{
				if (child.contains(offset))
				{
					IParseNode node = child.getNodeAt(offset);

					if (node != null)
					{
						result = node;
						break;
					}
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getParent()
	 */
	@Override
	public IParseNode getParent()
	{
		return fParent;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getStartingOffset()
	 */
	@Override
	public int getStartingOffset()
	{
		return getStart();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ILexeme#getText()
	 */
	@Override
	public String getText()
	{
		return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getType()
	 */
	@Override
	public short getType()
	{
		return getId();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 31 + getLanguage().hashCode();
		hash = hash * 31 + getType();
		hash = hash * 31 + (getParent() == null ? 0 : getParent().hashCode());
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return end < start;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<IParseNode> iterator()
	{
		return new Iterator<IParseNode>()
		{
			private int index = 0;

			@Override
			public boolean hasNext()
			{
				return fChildren != null && index < fChildrenCount;
			}

			@Override
			public IParseNode next()
			{
				if (hasNext() == false)
				{
					throw new NoSuchElementException();
				}

				return fChildren[index++];
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Set a child node at a specific index, <b>replacing</b> any other child that exists in that index.
	 * 
	 * @param index
	 * @param child
	 * @throws IndexOutOfBoundsException
	 *             in case the given index is negative, bigger / equal to the children count.
	 * @see #getChildrenCount()
	 */
	public void setChildAt(int index, IParseNode child) throws IndexOutOfBoundsException
	{
		if (index >= fChildrenCount)
		{
			throw new IndexOutOfBoundsException(index + " >= " + fChildrenCount); //$NON-NLS-1$
		}
		fChildren[index] = child;
		if (child instanceof ParseBaseNode)
		{
			((ParseBaseNode) child).setParent(this);
		}
	}

	/**
	 * setChildren
	 * 
	 * @param children
	 */
	public void setChildren(IParseNode[] children)
	{
		fChildren = children;
		fChildrenCount = children.length;
		for (IParseNode child : children)
		{
			((ParseBaseNode) child).setParent(this);
		}
	}

	/**
	 * setParent
	 * 
	 * @param parent
	 */
	public void setParent(IParseNode parent)
	{
		fParent = parent;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
}
