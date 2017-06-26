package com.aptana.js.core.parsing.ast;

public class JSExportSpecifierNode extends JSNode
{

	public JSExportSpecifierNode(JSIdentifierNode name, JSIdentifierNode alias)
	{
		super(IJSNodeTypes.EXPORT_SPECIFIER, name, alias);
	}

	public JSExportSpecifierNode(JSIdentifierNode name)
	{
		super(IJSNodeTypes.EXPORT_SPECIFIER, name);
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
