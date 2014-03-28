/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class JSScope
{
	private JSScope parent;
	private List<JSScope> children;
	private JSPropertyCollection object;
	private IRange range;

	/**
	 * JSScope
	 */
	public JSScope()
	{
		object = new JSPropertyCollection();
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

			if (children == null)
			{
				children = new ArrayList<JSScope>();
			}

			children.add(scope);
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

		if (object.hasProperty(name))
		{
			property = object.getProperty(name);
		}
		else
		{
			property = new JSPropertyCollection();

			object.setProperty(name, property);
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
		List<JSScope> result = children;

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
		return object.getProperty(name);
	}

	/**
	 * Return a list of symbol names defined in this scope
	 * 
	 * @return Returns a list of strings
	 */
	public List<String> getLocalSymbolNames()
	{
		return object.getPropertyNames();
	}

	/**
	 * getObject
	 * 
	 * @return
	 */
	public JSPropertyCollection getObject()
	{
		return object;
	}

	/**
	 * getParent
	 * 
	 * @return
	 */
	public JSScope getParentScope()
	{
		return parent;
	}

	/**
	 * getRange
	 * 
	 * @return
	 */
	public IRange getRange()
	{
		return (range != null) ? range : Range.EMPTY;
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
	 * Searches local scope up parents for a symbol with the given name.
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
		return object.hasProperty(name);
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
	private void setParent(JSScope parent)
	{
		this.parent = parent;
	}

	/**
	 * setRange
	 * 
	 * @param range
	 */
	public void setRange(IRange range)
	{
		this.range = range;
	}
}
