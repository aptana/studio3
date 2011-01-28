/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.idl.parsing.ast;

import com.aptana.editor.idl.parsing.IDLParserConstants;
import com.aptana.parsing.ast.ParseNode;

public class IDLNode extends ParseNode
{
	private IDLNodeType _type;

	/**
	 * IDLNode
	 */
	public IDLNode()
	{
		this(IDLNodeType.EMPTY);
	}

	/**
	 * IDLNode
	 * 
	 * @param type
	 */
	public IDLNode(IDLNodeType type)
	{
		super(IDLParserConstants.LANGUAGE);

		this._type = type;
	}

	/**
	 * accept
	 * 
	 * @param walker
	 */
	public void accept(IDLTreeWalker walker)
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
