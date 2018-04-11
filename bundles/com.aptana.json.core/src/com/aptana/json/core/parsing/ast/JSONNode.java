/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json.core.parsing.ast;

import com.aptana.json.core.IJSONConstants;
import com.aptana.parsing.ast.ParseNode;

/**
 * JSONNode
 */
public class JSONNode extends ParseNode
{
	private JSONNodeType _type;

	/**
	 * JSONNode
	 */
	public JSONNode()
	{
		this(JSONNodeType.EMPTY);
	}

	/**
	 * JSONNode
	 * 
	 * @param type
	 */
	public JSONNode(JSONNodeType type)
	{
		this._type = type;
	}

	public String getLanguage()
	{
		return IJSONConstants.CONTENT_TYPE_JSON;
	}

	/**
	 * accept
	 * 
	 * @param walker
	 */
	public void accept(JSONTreeWalker walker)
	{
		// sub-classes must override this method so their types will be
		// recognized properly
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getNodeType()
	 */
	@Override
	public short getNodeType()
	{
		return this._type.getIndex();
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public JSONNodeType getType()
	{
		return this._type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		return this._type.toString();
	}
}
