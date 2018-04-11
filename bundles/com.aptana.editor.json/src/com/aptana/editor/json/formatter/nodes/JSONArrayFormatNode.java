/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.formatter.nodes;

import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.json.core.parsing.ast.JSONArrayNode;
import com.aptana.json.core.parsing.ast.JSONObjectNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * JSONObjectNode
 */
public class JSONArrayFormatNode extends FormatterBlockWithBeginEndNode
{
	private JSONArrayNode _referenceNode;
	private boolean _arrayElement;

	/**
	 * JSONObjectFormatNode
	 * 
	 * @param document
	 */
	public JSONArrayFormatNode(IFormatterDocument document, JSONArrayNode referenceNode)
	{
		super(document);

		this._referenceNode = referenceNode;
		
		IParseNode parent = referenceNode.getParent();
		this._arrayElement = (parent instanceof JSONArrayNode);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		return (this._arrayElement) ? 0 : 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */
	@Override
	protected boolean isAddingEndNewLine()
	{
		boolean result = false;

		for (IParseNode child : this._referenceNode)
		{
			if (child instanceof JSONArrayNode || child instanceof JSONObjectNode)
			{
				result = true;
				break;
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return (this._arrayElement == false);
	}
}
