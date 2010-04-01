/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Lindsey
 */
public class NodeBase implements INode, Comparable<NodeBase>
{
	/**
	 * empty string
	 */
	protected static final String EMPTY_STRING = ""; //$NON-NLS-1$
	
	private List<Class<?>> childTypes;
	
	private INode _parent;
	private List<INode> _children;
	private int _childCount;
	private StringBuffer _text;
	private int _columnNumber;
	private int _lineNumber;

	/**
	 * NodeBase
	 */
	public NodeBase()
	{
	}
	
	/**
	 * addAllowableChildType
	 *
	 * @param type
	 */
	public void addChildType(Class<?> type)
	{
		if (childTypes == null)
		{
			childTypes = new ArrayList<Class<?>>();
		}
		
		if (childTypes.contains(type) == false)
		{
			childTypes.add(type);
		}
	}
	
	/**
	 * @see com.aptana.xml.INode#appendChild(INode)
	 */
	public void appendChild(INode child)
	{
		boolean allowed = false;
		
		if (childTypes != null)
		{
			for (int i = 0; i < childTypes.size(); i++)
			{
				Class<?> type = childTypes.get(i);
				
				if (type.isInstance(child))
				{
					allowed = true;
					break;
				}
			}
		}
		
		if (allowed)
		{
			if (this._children == null)
			{
				this._children = new ArrayList<INode>();
			}
	
			this._children.add(child);
			this._childCount++;
			
			if (child instanceof NodeBase)
			{
				((NodeBase) child).setParent(this);
			}
		}
		else
		{
			String thisName = this.getClass().getName();
			String childName = child.getClass().getName();
			String message = MessageFormat.format(
				Messages.NodeBase_Invalid_Child_Type,
				new Object[] { thisName, childName }
			);
			
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * @see com.aptana.xml.INode#appendText(java.lang.String)
	 */
	public void appendText(String text)
	{
		if (text != null && text.length() > 0)
		{
			if (this._text == null)
			{
				this._text = new StringBuffer();
			}
			
			this._text.append(text);
		}
	}

	/**
	 * @see com.aptana.xml.INode#getChild(int)
	 */
	public INode getChild(int index)
	{
		INode result = null;
		
		if (this._children != null)
		{
			if (0 <= index && index < this._childCount)
			{
				result = this._children.get(index);
			}
		}
		
		return result;
	}

	/**
	 * @see com.aptana.xml.INode#getChildCount()
	 */
	public int getChildCount()
	{
		return this._childCount;
	}

	/**
	 * @see com.aptana.xml.INode#getDocument()
	 */
	public DocumentNode getDocument()
	{
		INode result = this.getParent();
		
		while (result != null && (result instanceof DocumentNode) == false)
		{
			result = result.getParent();
		}
		
		// TODO: [KEL] DocumentNode should create all nodes so it can auto-associated itself.
		// We will not this hack once that is in place.
		if (result == null)
		{
			result = new DocumentNode();
		}
		
		return (DocumentNode) result;
	}
	
	/**
	 * @see com.aptana.xml.INode#getParent()
	 */
	public INode getParent()
	{
		return this._parent;
	}
	
	/**
	 * @see com.aptana.xml.INode#getText()
	 */
	public String getText()
	{
		String result = EMPTY_STRING;
		
		if (this._text != null)
		{
			result = this._text.toString();
		}
		
		return result;
	}
	
	/**
	 * removeChild
	 *
	 * @param child
	 */
	public void removeChild(INode child)
	{
		if (this._children != null)
		{
			if (this._children.remove(child))
			{
				this._childCount--;
			}
			
			if (child instanceof NodeBase)
			{
				((NodeBase) child)._parent = null;
			}
		}
	}
	
	/**
	 * removeChildType
	 *
	 * @param type
	 */
	public void removeChildType(Class<?> type)
	{
		if (childTypes != null)
		{
			childTypes.remove(type);
		}
	}
	
	/**
	 * removeText
	 */
	public void removeText()
	{
		this._text = null;
	}
	
	/**
	 * setParent
	 *
	 * @param parent
	 */
	protected void setParent(INode parent)
	{
		if (this._parent != null)
		{
			// remove from old parent
			this._parent.removeChild(this);
		}
		
		this._parent = parent;
	}

	/**
	 * getColumnNumber
	 *
	 * @return int
	 */
	public int getColumnNumber()
	{
		return this._columnNumber;
	}

	/**
	 * setColumnNumber
	 *
	 * @param columnNumber
	 */
	public void setColumnNumber(int columnNumber)
	{
		this._columnNumber = columnNumber;
	}

	/**
	 * getLineNumber
	 *
	 * @return int
	 */
	public int getLineNumber()
	{
		return this._lineNumber;
	}

	/**
	 * setLineNumber
	 *
	 * @param lineNumber
	 */
	public void setLineNumber(int lineNumber)
	{
		this._lineNumber = lineNumber;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(NodeBase that)
	{
		int result = this.getLineNumber() - that.getLineNumber();
			
		if (result == 0)
		{
			result = this.getColumnNumber() - that.getColumnNumber();
		}
		
		return result;
	}
}
