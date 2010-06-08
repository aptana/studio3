package com.aptana.editor.js.parsing.ast;

public class JSContinueNode extends JSLabelStatementNode
{
	/**
	 * JSContinueNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSContinueNode(int start, int end)
	{
		super(JSNodeTypes.CONTINUE, start, end);
	}

	/**
	 * JSContinueNode
	 * 
	 * @param start
	 * @param end
	 * @param identifier
	 */
	public JSContinueNode(int start, int end, String identifier)
	{
		super(JSNodeTypes.CONTINUE, start, end, identifier);
	}
}
