/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.ast;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.NoSuchElementException;

import beaver.spec.ast.Node;
import beaver.spec.ast.TreeWalker;

import com.aptana.parsing.ParsingPlugin;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class ParseNode extends Node implements IParseNode
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

		public String getName()
		{
			return fName;
		}

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
	public ParseNode(String language)
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
		if (child instanceof ParseNode)
		{
			((ParseNode) child).setParent(this);
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
	public boolean contains(int offset)
	{
		return this.getStartingOffset() <= offset && offset <= this.getEndingOffset();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		// Must be a parse node
		if (!(obj instanceof IParseNode))
			return false;

		IParseNode other = (IParseNode) obj;
		// Must be same language
		if (!getLanguage().equals(other.getLanguage()))
			return false;

		// Same type
		if (getNodeType() != other.getNodeType())
			return false;

		// Must have same parent
		if (getParent() == null)
		{
			if (other.getParent() != null)
				return false;
		}
		else if (!getParent().equals(other.getParent()))
			return false;

		// That's about the best we can check from here, since offsets can change a lot. Should really also check
		// identifier/name
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getAttributes()
	 */
	public IParseNodeAttribute[] getAttributes()
	{
		return NO_ATTRIBUTES;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getChild(int)
	 */
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
	 * @see com.aptana.parsing.ast.IParseNode#getChildIndex(com.aptana.parsing.ast.IParseNode)
	 */
	public int getChildIndex(IParseNode child)
	{
		int result = -1;

		for (int i = 0; i < fChildrenCount; i++)
		{
			if (fChildren[i] == child)
			{
				result = i;
				break;
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getIndex()
	 */
	public int getIndex()
	{
		int result = -1;

		// grab parent
		IParseNode parent = getParent();

		// get child index of this node, if parent exists
		if (parent != null)
		{
			result = parent.getChildIndex(this);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getChildren()
	 */
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
	public int getChildCount()
	{
		return fChildrenCount;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getElementName()
	 */
	public String getElementName()
	{
		return this.getClass().getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getEndingOffset()
	 */
	public int getEndingOffset()
	{
		return getEnd();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getFollowingNode()
	 */
	public IParseNode getNextNode()
	{
		IParseNode result = this.getFirstChild();

		if (result == null)
		{
			result = this.getNextSibling();
		}

		if (result == null)
		{
			IParseNode parent = this.getParent();

			while (parent != null)
			{
				IParseNode candidate = parent.getNextSibling();

				if (candidate != null)
				{
					result = candidate;
					break;
				}
				else
				{
					parent = parent.getParent();
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getFirstChild()
	 */
	public IParseNode getFirstChild()
	{
		IParseNode result = null;

		if (this.hasChildren())
		{
			result = this.getChild(0);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getFollowingSibling()
	 */
	public IParseNode getNextSibling()
	{
		IParseNode parent = this.getParent();
		IParseNode result = null;

		if (parent != null)
		{
			// get index of potential sibling
			int index = this.getIndex() + 1;

			if (index < parent.getChildCount())
			{
				result = parent.getChild(index);
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getLanguage()
	 */
	public String getLanguage()
	{
		return fLanguage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getLength()
	 */
	public int getLength()
	{
		return getEnd() - getStart() + 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getLastChild()
	 */
	public IParseNode getLastChild()
	{
		IParseNode result = null;

		if (this.hasChildren())
		{
			result = this.getChild(this.getChildCount() - 1);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getNameNode()
	 */
	public INameNode getNameNode()
	{
		return new NameNode(getText(), getStartingOffset(), getEndingOffset());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getNodeAt(int)
	 */
	public IParseNode getNodeAtOffset(int offset)
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
					IParseNode node = child.getNodeAtOffset(offset);

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
	public IParseNode getParent()
	{
		return fParent;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getPrecedingNode()
	 */
	public IParseNode getPreviousNode()
	{
		IParseNode result = this.getPreviousSibling();

		if (result != null)
		{
			IParseNode candidate = result.getLastChild();

			while (candidate != null)
			{
				result = candidate;
				candidate = candidate.getLastChild();
			}
		}

		if (result == null)
		{
			result = this.getParent();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getPrecedingSibling()
	 */
	public IParseNode getPreviousSibling()
	{
		IParseNode parent = this.getParent();
		IParseNode result = null;

		if (parent != null)
		{
			// get index of potential sibling
			int index = this.getIndex() - 1;

			if (index >= 0)
			{
				result = parent.getChild(index);
			}
		}

		return result;
	}

	/**
	 * getRootNode
	 * 
	 * @return
	 */
	public IParseNode getRootNode()
	{
		IParseNode root = this;

		while (true)
		{
			IParseNode parent = root.getParent();

			if (parent == null)
			{
				break;
			}
			else
			{
				root = parent;
			}
		}

		return root;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		return getStart();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ILexeme#getText()
	 */
	public String getText()
	{
		return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getType()
	 */
	public short getNodeType()
	{
		return getId();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		return this.getChildCount() > 0;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		int hash = 31 + getLanguage().hashCode();
		hash = hash * 31 + getNodeType();
		// TODO Can we do something other than recursively go up the stack asking for hashcodes?
		// hash = hash * 31 + (getParent() == null ? 0 : getParent().hashCode());
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#isEmpty()
	 */
	public boolean isEmpty()
	{
		return end < start;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<IParseNode> iterator()
	{
		return new Iterator<IParseNode>()
		{
			private int index = 0;

			public boolean hasNext()
			{
				return fChildren != null && index < fChildrenCount;
			}

			public IParseNode next()
			{
				if (hasNext() == false)
				{
					throw new NoSuchElementException();
				}

				return fChildren[index++];
			}

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
	 * @see #getChildCount()
	 */
	public void replaceChild(int index, IParseNode child) throws IndexOutOfBoundsException
	{
		if (index >= fChildrenCount)
		{
			throw new IndexOutOfBoundsException(index + " >= " + fChildrenCount); //$NON-NLS-1$
		}
		fChildren[index] = child;
		if (child instanceof ParseNode)
		{
			((ParseNode) child).setParent(this);
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
			((ParseNode) child).setParent(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see beaver.spec.ast.Node#setLocation(int, int)
	 */
	@Override
	public void setLocation(int start, int end)
	{
		int diff = end - start;

		// NOTE: a diff of -1 is used to indicate that the node is empty. If the diff is larger than that (in negative
		// magnitude), then we consider the ending offset to be in error
		if (diff < -1)
		{
			String source;

			try
			{
				source = this.toString();
			}
			catch (Throwable t)
			{
				source = ""; //$NON-NLS-1$
			}

			String message = MessageFormat.format( //
				Messages.ParseNode_Bad_Ending_Offset, //
				start, //
				end, //
				this.getLanguage(), //
				this.getNodeType(), //
				source //
				);

			ParsingPlugin.logError(message, null);

			end = start - 1;
		}

		super.setLocation(start, end);
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
