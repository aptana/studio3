package com.aptana.editor.js.parsing.ast;

public class JSRegexNode extends JSPrimitiveNode
{
	/**
	 * JSRegexNode
	 * 
	 * @param text
	 * @param start
	 * @param end
	 */
	public JSRegexNode(String text, int start, int end)
	{
		super(JSNodeTypes.REGEX, text, start, end);
	}
}
