package com.aptana.js.core.parsing.ast;

import com.aptana.js.core.JSLanguageConstants;

import beaver.Symbol;

public class JSImportSpecifierNode extends JSNode
{

	private final Symbol _star;

	public JSImportSpecifierNode(Symbol star, JSIdentifierNode alias)
	{
		super(IJSNodeTypes.IMPORT_SPECIFIER, alias);
		this._star = star;
	}

	public JSImportSpecifierNode(JSIdentifierNode name, JSIdentifierNode alias)
	{
		super(IJSNodeTypes.IMPORT_SPECIFIER, name, alias);
		this._star = null;
	}

	public JSImportSpecifierNode(JSIdentifierNode name)
	{
		super(IJSNodeTypes.IMPORT_SPECIFIER, name);
		this._star = null;
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

	public boolean isWildcard()
	{
		return this._star != null;
	}

	public String getSpecifier()
	{
		if (this.isWildcard())
		{
			return JSLanguageConstants.STAR;
		}
		return getFirstChild().toString();
	}

	public String getAlias()
	{
		if (this.isWildcard())
		{
			return getFirstChild().getText();
		}
		return getChild(1).getText();
	}

	public boolean hasAlias()
	{
		return isWildcard() || getChildCount() > 1;
	}
}
