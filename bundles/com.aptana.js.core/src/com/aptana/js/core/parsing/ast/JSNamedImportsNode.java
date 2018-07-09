package com.aptana.js.core.parsing.ast;

public class JSNamedImportsNode extends JSNode
{

	public JSNamedImportsNode()
	{
		super(IJSNodeTypes.NAMED_IMPORTS);
	}

	public JSNamedImportsNode(JSNode[] imports)
	{
		super(IJSNodeTypes.NAMED_IMPORTS, imports);
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
