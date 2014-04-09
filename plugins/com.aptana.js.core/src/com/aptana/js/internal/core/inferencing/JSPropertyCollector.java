/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.inferencing;

import java.util.ArrayList;
import java.util.List;

import com.aptana.js.core.inferencing.JSPropertyCollection;
import com.aptana.js.core.parsing.ast.JSAssignmentNode;
import com.aptana.js.core.parsing.ast.JSGetElementNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSObjectNode;
import com.aptana.js.core.parsing.ast.JSStringNode;
import com.aptana.js.core.parsing.ast.JSTreeWalker;
import com.aptana.parsing.ast.IParseNode;

public class JSPropertyCollector extends JSTreeWalker
{
	private JSPropertyCollection _object;
	private JSPropertyCollection _currentObject;

	/**
	 * JSPropertyCollector
	 * 
	 * @param global
	 */
	public JSPropertyCollector(JSPropertyCollection global)
	{
		this._object = this._currentObject = global;
	}

	/**
	 * activateProperty
	 * 
	 * @param name
	 */
	void activateProperty(String name)
	{
		if (this._currentObject.hasProperty(name))
		{
			this._currentObject = this._currentObject.getProperty(name);
		}
		else
		{
			JSPropertyCollection property = new JSPropertyCollection();

			this._currentObject.setProperty(name, property);
			this._currentObject = property;
		}
	}

	/**
	 * addPropertyValue
	 * 
	 * @param name
	 * @param value
	 */
	private void addPropertyValue(String name, IParseNode value)
	{
		if (name != null && name.length() > 0 && value instanceof JSNode)
		{
			List<JSNode> values = new ArrayList<JSNode>(1);

			values.add((JSNode) value);

			this.addPropertyValues(name, values);
		}
	}

	/**
	 * addPropertyValue
	 * 
	 * @param name
	 * @param values
	 */
	private void addPropertyValues(String name, List<JSNode> values)
	{
		if (name != null && name.length() > 0 && values != null && !values.isEmpty())
		{
			JSPropertyCollection property;

			if (this._currentObject.hasProperty(name))
			{
				// use the currently existing property
				property = this._currentObject.getProperty(name);
			}
			else
			{
				// create a new property
				property = new JSPropertyCollection();

				// add it to the current object
				this._currentObject.setProperty(name, property);
			}

			for (JSNode value : values)
			{
				if (value instanceof JSObjectNode)
				{
					// save current object
					JSPropertyCollection current = this._currentObject;

					this._currentObject = property;
					this.visit((JSObjectNode) value);

					// restore original object
					this._currentObject = current;
				}

				property.addValue(value);
			}
		}
	}

	/**
	 * getCurrentObject
	 * 
	 * @return the currentObject
	 */
	public JSPropertyCollection getCurrentObject()
	{
		return this._currentObject;
	}

	/**
	 * getObject
	 * 
	 * @return the object
	 */
	public JSPropertyCollection getObject()
	{
		return this._object;
	}

	/**
	 * resetGlobal
	 */
	private void resetGlobal()
	{
		this._currentObject = this._object;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSAssignmentNode)
	 */
	@Override
	public void visit(JSAssignmentNode node)
	{
		IParseNode lhs = node.getLeftHandSide();
		IParseNode rhs = node.getRightHandSide();

		if (lhs instanceof JSNode && rhs instanceof JSNode)
		{
			// start from global
			this.resetGlobal();

			((JSNode) lhs).accept(this);

			this._currentObject.addValue((JSNode) rhs);

			// TODO: Do we really want to potentially expand an object hierarchy
			// once for each assignment or can we take advantage of the fact
			// that we have multiple assignments to the same object and then
			// re-use the generated type for each?

			// grab the actual value if we have a stream of assignments
			IParseNode rightmostValue = rhs;

			while (rightmostValue instanceof JSAssignmentNode)
			{
				rightmostValue = rightmostValue.getLastChild();
			}

			// perform special processing on object literals to transform them
			// to our JSObject structure
			if (rightmostValue instanceof JSObjectNode)
			{
				this.visit((JSObjectNode) rightmostValue);
			}

			// expand properties of right-hand assignments as well
			if (rhs instanceof JSAssignmentNode)
			{
				this.visit((JSAssignmentNode) rhs);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGetElementNode)
	 */
	@Override
	public void visit(JSGetElementNode node)
	{
		IParseNode lhs = node.getLeftHandSide();
		IParseNode rhs = node.getRightHandSide();

		if (lhs instanceof JSNode)
		{
			((JSNode) lhs).accept(this);

			if (rhs instanceof JSStringNode)
			{
				String name = rhs.getText();

				if (name != null && name.length() > 2)
				{
					name = name.substring(1, name.length() - 1);

					this.activateProperty(name);
				}
			}
			// else ?
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSIdentifierNode)
	 */
	@Override
	public void visit(JSIdentifierNode node)
	{
		this.activateProperty(node.getText());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSObjectNode)
	 */
	@Override
	public void visit(JSObjectNode node)
	{
		for (IParseNode child : node)
		{
			IParseNode key = child.getFirstChild();
			IParseNode value = child.getLastChild();
			String name = key.getText();

			if (key instanceof JSStringNode)
			{
				name = name.substring(1, name.length() - 1);
			}

			if (value instanceof JSObjectNode)
			{
				JSObjectNode objectLiteral = (JSObjectNode) value;

				// remember current object
				JSPropertyCollection currentObject = this._currentObject;

				// create a new one for this object
				this.activateProperty(name);
				this._currentObject.addValue(objectLiteral);

				this.visit(objectLiteral);

				// reset
				this._currentObject = currentObject;
			}
			else
			{
				this.addPropertyValue(name, value);
			}
		}
	}
}
