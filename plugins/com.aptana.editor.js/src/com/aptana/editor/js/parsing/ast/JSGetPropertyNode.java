package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSGetPropertyNode extends JSBinaryOperatorNode
{
	/**
	 * JSGetPropertyOperatorNode
	 * 
	 * @param left
	 * @param right
	 */
	public JSGetPropertyNode(JSNode left, Symbol operator, JSNode right)
	{
		super(left, operator, right);
		
		this.setType(JSNodeTypes.GET_PROPERTY);
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSBinaryOperatorNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();

		text.append(this.getLeftHandSide());
		text.append("."); //$NON-NLS-1$
		text.append(this.getRightHandSide());

		this.appendSemicolon(text);

		return text.toString();
	}
}
