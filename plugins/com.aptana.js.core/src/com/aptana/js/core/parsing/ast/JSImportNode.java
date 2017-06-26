package com.aptana.js.core.parsing.ast;

public class JSImportNode extends JSNode
{

	private final String _from;

	public JSImportNode(JSNode[] clauses, String from)
	{
		super(IJSNodeTypes.IMPORT, clauses);
		this._from = from;
	}

	public JSImportNode(String from)
	{
		super(IJSNodeTypes.IMPORT);
		this._from = from;
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
}
