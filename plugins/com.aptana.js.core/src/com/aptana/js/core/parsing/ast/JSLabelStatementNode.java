/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

/**
 * Represents continue and break statements.
 */
abstract class JSLabelStatementNode extends JSNode
{
	private Symbol _label;

	/**
	 * JSLabelStatementNode
	 * 
	 * @param type
	 */
	protected JSLabelStatementNode(short type)
	{
		this(type, null);
	}

	/**
	 * JSLabelStatementNode
	 * 
	 * @param type
	 * @param label
	 */
	protected JSLabelStatementNode(short type, Symbol label)
	{
		super(type);

		this._label = label;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof JSLabelStatementNode))
		{
			return false;
		}

		JSLabelStatementNode other = (JSLabelStatementNode) obj;

		return (_label == null) ? other._label == null : _label.value.equals(other._label.value);
	}

	/**
	 * getIdentifier
	 * 
	 * @return
	 */
	public Symbol getLabel()
	{
		return this._label;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = super.hashCode();

		return hash * 31 + ((_label == null) ? 0 : _label.value.hashCode());
	}
}
