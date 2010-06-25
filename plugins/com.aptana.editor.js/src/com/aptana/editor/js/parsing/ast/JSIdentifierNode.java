package com.aptana.editor.js.parsing.ast;

import java.util.List;

import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.parsing.Scope;

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
	 * @see com.aptana.editor.js.parsing.ast.JSNode#addReturnTypes(java.util.List, com.aptana.parsing.Scope)
	 */
	@Override
	protected void addTypes(List<String> types, Scope<JSNode> scope)
	{
		String name = this.getText();
		List<JSNode> nodes = scope.getSymbol(name);
		
		if (nodes.isEmpty() == false)
		{
			for (JSNode node : nodes)
			{
				// look up type
				List<String> nodeTypes = node.getTypes(scope);
				
				types.addAll(nodeTypes);
			}
		}
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
