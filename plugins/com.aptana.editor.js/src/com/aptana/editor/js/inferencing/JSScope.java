/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class JSScope
{
	private JSScope _parent;
	private List<JSScope> _children;
	private JSPropertyCollection _object;
	private IRange _range;

	/**
	 * JSScope
	 */
	public JSScope()
	{
		this._object = new JSPropertyCollection();
	}

	/**
	 * addScope
	 * 
	 * @param scope
	 */
	public void addScope(JSScope scope)
	{
		if (scope != null)
		{
			scope.setParent(this);

			if (this._children == null)
			{
				this._children = new ArrayList<JSScope>();
			}

			this._children.add(scope);
		}
	}

	/**
	 * addSymbol - Note that value can be null
	 * 
	 * @param name
	 * @param value
	 */
	public void addSymbol(String name, JSNode value)
	{
		JSPropertyCollection property;

		if (this._object.hasProperty(name))
		{
			property = this._object.getProperty(name);
		}
		else
		{
			property = new JSPropertyCollection();

			this._object.setProperty(name, property);
		}

		property.addValue(value);
	}

	/**
	 * getChildren
	 * 
	 * @return
	 */
	public List<JSScope> getChildren()
	{
		List<JSScope> result = this._children;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getLocalSymbol
	 * 
	 * @param name
	 * @return
	 */
	public JSPropertyCollection getLocalSymbol(String name)
	{
		return this._object.getProperty(name);
	}

	/**
	 * getLocalSymbolNames
	 * 
	 * @return
	 */
	public List<String> getLocalSymbolNames()
	{
		return this._object.getPropertyNames();
	}

	/**
	 * getObject
	 * 
	 * @return
	 */
	public JSPropertyCollection getObject()
	{
		return this._object;
	}

	/**
	 * getParent
	 * 
	 * @return
	 */
	public JSScope getParentScope()
	{
		return this._parent;
	}

	/**
	 * getRange
	 * 
	 * @return
	 */
	public IRange getRange()
	{
		return (this._range != null) ? this._range : Range.EMPTY;
	}

	/**
	 * getScopeAtOffset
	 * 
	 * @param offset
	 * @return
	 */
	public JSScope getScopeAtOffset(int offset)
	{
		JSScope result = null;

		if (this.getRange().contains(offset))
		{
			result = this;

			for (JSScope child : this.getChildren())
			{
				JSScope candidate = child.getScopeAtOffset(offset);

				if (candidate != null)
				{
					result = candidate;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * getScopeWithSymbol
	 * 
	 * @param name
	 * @return
	 */
	public JSScope getScopeWithSymbol(String name)
	{
		JSScope current = this;

		while (current != null)
		{
			if (current.hasLocalSymbol(name))
			{
				break;
			}
			else
			{
				current = current.getParentScope();
			}
		}

		return current;
	}

	/**
	 * getSymbol
	 * 
	 * @param name
	 * @return
	 */
	public JSPropertyCollection getSymbol(String name)
	{
		JSScope current = this;
		JSPropertyCollection result = null;

		while (current != null)
		{
			if (current.hasLocalSymbol(name))
			{
				result = current.getLocalSymbol(name);
				break;
			}
			else
			{
				current = current.getParentScope();
			}
		}

		return result;
	}

	/**
	 * getSymbolNames
	 * 
	 * @return
	 */
	public List<String> getSymbolNames()
	{
		Set<String> result = new HashSet<String>();

		JSScope current = this;

		while (current != null)
		{
			result.addAll(current.getLocalSymbolNames());

			current = current.getParentScope();
		}

		return new ArrayList<String>(result);
	}

	/**
	 * hasLocalSymbol
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasLocalSymbol(String name)
	{
		return this._object.hasProperty(name);
	}

	/**
	 * hasSymbol
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasSymbol(String name)
	{
		JSScope current = this;
		boolean result = false;

		while (current != null)
		{
			if (current.hasLocalSymbol(name))
			{
				result = true;
				break;
			}
			else
			{
				current = current.getParentScope();
			}
		}

		return result;
	}

	/**
	 * setParent
	 * 
	 * @param parent
	 */
	protected void setParent(JSScope parent)
	{
		this._parent = parent;
	}

	/**
	 * setRange
	 * 
	 * @param range
	 */
	public void setRange(IRange range)
	{
		this._range = range;
	}
}
