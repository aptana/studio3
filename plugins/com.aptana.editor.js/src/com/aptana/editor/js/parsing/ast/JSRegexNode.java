package com.aptana.editor.js.parsing.ast;

public class JSRegexNode extends JSPrimitiveNode
{
	/**
	 * JSRegexNode
	 * 
	 * @param start
	 * @param end
	 * @param text
	 */
	public JSRegexNode(int start, int end, String text)
	{
		super(JSNodeTypes.REGEX, start, end, text);
	}
}
