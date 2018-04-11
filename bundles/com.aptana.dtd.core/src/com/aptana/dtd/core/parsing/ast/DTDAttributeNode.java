/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.dtd.core.parsing.ast;

public class DTDAttributeNode extends DTDNode
{
	private String _name;
	private String _mode;

	/**
	 * DTDAttributeNode
	 */
	public DTDAttributeNode(String name, DTDNode type, String mode)
	{
		super(DTDNodeType.ATTRIBUTE);

		this._name = name;
		this.addChild(type);
		this._mode = mode;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.dtd.parsing.ast.DTDNode#accept(com.aptana.editor.dtd.parsing.ast.DTDTreeWalker)
	 */
	public void accept(DTDTreeWalker walker)
	{
		walker.visit(this);
	}

	/**
	 * getMode
	 */
	public String getMode()
	{
		return this._mode;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}
}
