package com.aptana.editor.js.parsing.ast;

import java.util.List;

public class JSObjectNode extends JSNaryNode
{
	/**
	 * JSObjectNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSObjectNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.OBJECT_LITERAL, start, end, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendCloseText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendCloseText(StringBuilder buffer)
	{
		buffer.append("}"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendOpenText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendOpenText(StringBuilder buffer)
	{
		buffer.append("{"); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#addReturnTypes(java.util.List)
	 */
	@Override
	protected void addReturnTypes(List<String> types)
	{
		types.add("Object");
	}
}
