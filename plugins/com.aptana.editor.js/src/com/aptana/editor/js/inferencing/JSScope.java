/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
