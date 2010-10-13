package com.aptana.editor.js.parsing.ast;

public class JSCommentNode extends JSNode
{
	/**
	 * JSCommentNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 */
	public JSCommentNode(short type, int start, int end)
	{
		super(type);
		
		this.setLocation(start, end);
	}
}
