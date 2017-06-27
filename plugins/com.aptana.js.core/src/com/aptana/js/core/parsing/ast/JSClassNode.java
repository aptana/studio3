package com.aptana.js.core.parsing.ast;

public class JSClassNode extends JSNode
{

	private final boolean _hasName;
	private final boolean _hasSuperclass;

	public JSClassNode(JSIdentifierNode ident, JSNode heritage, JSStatementsNode body)
	{
		super(IJSNodeTypes.CLASS, ident, heritage, body);
		_hasName = true;
		_hasSuperclass = true;
	}

	public JSClassNode(JSNode heritage, JSStatementsNode body)
	{
		super(IJSNodeTypes.CLASS, heritage, body);
		_hasName = false;
		_hasSuperclass = true;
	}

	public JSClassNode(JSIdentifierNode ident, JSStatementsNode body)
	{
		super(IJSNodeTypes.CLASS, ident, body);
		_hasName = true;
		_hasSuperclass = false;
	}

	public JSClassNode(JSStatementsNode body)
	{
		super(IJSNodeTypes.CLASS, body);
		_hasName = false;
		_hasSuperclass = false;
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
}
