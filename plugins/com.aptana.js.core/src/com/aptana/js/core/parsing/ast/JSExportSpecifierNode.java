package com.aptana.js.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

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

	/**
	 * Used by ANTLR AST
	 */
	public JSExportSpecifierNode()
	{
		super(IJSNodeTypes.EXPORT_SPECIFIER);
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

	public boolean hasAlias()
	{
		return getChildCount() > 1;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public IParseNode getName()
	{
		return this.getChild(0);
	}
}
