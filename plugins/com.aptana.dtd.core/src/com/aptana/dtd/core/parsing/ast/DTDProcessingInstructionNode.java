/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.dtd.core.parsing.ast;

public class DTDProcessingInstructionNode extends DTDNode
{
	private String _text;

	/**
	 * DTDProcessingInstructionNode
	 */
	public DTDProcessingInstructionNode(String text)
	{
		super(DTDNodeType.PROCESSING_INSTRUCTION);

		this._text = text;
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
	 * getText
	 */
	public String getText()
	{
		return this._text;
	}
}
