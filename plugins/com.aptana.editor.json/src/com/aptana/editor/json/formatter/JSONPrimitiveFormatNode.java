/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.formatter;

import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.json.core.parsing.ast.JSONArrayNode;
import com.aptana.json.core.parsing.ast.JSONNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * JSONPrimitiveFormatNode
 */
public class JSONPrimitiveFormatNode extends FormatterBlockWithBeginNode
{
	private boolean _firstElement;
	
	/**
	 * JSONPrimitiveFormatNode
	 * 
	 * @param document
	 */
	public JSONPrimitiveFormatNode(IFormatterDocument document, JSONNode referenceNode)
	{
		super(document);
		
		IParseNode parent = referenceNode.getParent();
		this._firstElement = (parent instanceof JSONArrayNode && parent.getFirstChild() == referenceNode);
	}

	/* (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		return this._firstElement ? 0 : 1;
	}
}
