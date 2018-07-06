package com.aptana.js.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSClassNode extends JSNode
{

	private final boolean _hasName;
	private final boolean _hasSuperclass;

	public JSClassNode(JSIdentifierNode ident, JSNode heritage, JSStatementsNode body)
	{
		super(IJSNodeTypes.CLASS, ident, heritage, body);
		this._hasName = true;
		this._hasSuperclass = true;
	}

	public JSClassNode(JSNode heritage, JSStatementsNode body)
	{
		super(IJSNodeTypes.CLASS, heritage, body);
		this._hasName = false;
		this._hasSuperclass = true;
	}

	public JSClassNode(JSIdentifierNode ident, JSStatementsNode body)
	{
		super(IJSNodeTypes.CLASS, ident, body);
		this._hasName = true;
		this._hasSuperclass = false;
	}

	public JSClassNode(JSStatementsNode body)
	{
		super(IJSNodeTypes.CLASS, body);
		this._hasName = false;
		this._hasSuperclass = false;
	}

	/**
	 * Used by ANTLR AST
	 */
	public JSClassNode(boolean hasName, boolean hasSuperClass)
	{
		super(IJSNodeTypes.CLASS);
		this._hasName = hasName;
		this._hasSuperclass = hasSuperClass;
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

	public boolean hasName()
	{
		return _hasName;
	}

	public JSStatementsNode getBody()
	{
		return (JSStatementsNode) getLastChild();
	}

	public boolean hasSuperClass()
	{
		return _hasSuperclass;
	}

	public JSNode getSuperClass()
	{
		// second to last child
		int count = getChildCount();
		return (JSNode) getChild(count - 2);
	}
	
	@Override
	public boolean isExported()
	{
		IParseNode parent = getParent();
		return parent != null && parent instanceof JSExportNode;
	}
}
