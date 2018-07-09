package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

public class JSExportNode extends JSNode
{

	private final boolean _isDefault;
	private final String _from;

	public JSExportNode(boolean isDefault, Symbol star, String from)
	{
		super(IJSNodeTypes.EXPORT);
		this._isDefault = isDefault;
		this._from = from;
	}

	public JSExportNode(boolean isDefault, JSNode[] exportClauses, String from)
	{
		super(IJSNodeTypes.EXPORT, exportClauses);
		this._isDefault = isDefault;
		this._from = from;
	}

	public JSExportNode(boolean isDefault, JSNode[] exportClauses)
	{
		this(isDefault, exportClauses, null);
	}

	public JSExportNode(boolean isDefault, JSVarNode var)
	{
		super(IJSNodeTypes.EXPORT, var);
		this._isDefault = isDefault;
		this._from = null;
	}

	public JSExportNode(boolean isDefault, JSNode decl)
	{
		super(IJSNodeTypes.EXPORT, decl);
		this._isDefault = isDefault;
		this._from = null;
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

	public String getFrom()
	{
		return _from;
	}

	public boolean isDefault()
	{
		return _isDefault;
	}
}
