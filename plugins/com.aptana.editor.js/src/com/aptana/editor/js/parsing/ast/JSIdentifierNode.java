package com.aptana.editor.js.parsing.ast;

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
		super(JSNodeTypes.IDENTIFIER, (String) identifier.value);
		
		this.setLocation(identifier.getStart(), identifier.getEnd());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}
}
