package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSTrueNode extends JSPrimitiveNode
{
	/**
	 * JSTrueNode
	 * 
	 * @param identifier
	 */
	public JSTrueNode(Symbol identifier)
	{
		this(identifier.getStart(), identifier.getEnd());
	}

	/**
	 * JSTrueNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSTrueNode(int start, int end)
	{
		super(JSNodeTypes.TRUE, start, end, "true");
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
