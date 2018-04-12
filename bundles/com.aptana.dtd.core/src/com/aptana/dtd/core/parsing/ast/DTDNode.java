/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.dtd.core.parsing.ast;

import com.aptana.dtd.core.IDTDConstants;
import com.aptana.parsing.ast.ParseNode;

public class DTDNode extends ParseNode
{
	private DTDNodeType _type;

	/**
	 * DTDNode
	 */
	public DTDNode()
	{
		this(DTDNodeType.EMPTY);
	}

	/**
	 * DTDNodeType
	 * 
	 * @param type
	 */
	protected DTDNode(DTDNodeType type)
	{
		super();

		this._type = type;
	}

	public String getLanguage()
	{
		return IDTDConstants.CONTENT_TYPE_DTD;
	}

	/**
	 * accept
	 * 
	 * @param walker
	 */
	public void accept(DTDTreeWalker walker)
	{
		// sub-classes must override this method so their types will be
		// recognized properly
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getNodeType()
	 */
	public short getNodeType()
	{
		return this._type.getIndex();
	}
}
