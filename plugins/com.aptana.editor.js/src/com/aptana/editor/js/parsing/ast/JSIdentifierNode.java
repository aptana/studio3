package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.contentassist.LocationType;

import beaver.Symbol;

public class JSIdentifierNode extends JSPrimitiveNode
{
	/**
	 * JSIdentifierNode
	 * 
	 * @param identifier
	 */
	public JSIdentifierNode(Symbol identifier)
	{
		this(identifier.getStart(), identifier.getEnd(), (String) identifier.value);
	}
	
	/**
	 * JSIdentifierNode
	 * 
	 * @param start
	 * @param end
	 * @param text
	 */
	public JSIdentifierNode(int start, int end, String text)
	{
		super(JSNodeTypes.IDENTIFIER, start, end, text);
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#getLocationType(int)
	 */
	@Override
	LocationType getLocationType(int offset)
	{
		LocationType result = LocationType.IN_GLOBAL;
		
		if (this.contains(offset))
		{
			result = LocationType.IN_VARIABLE_NAME;
		}
		
		return result;
	}
}
