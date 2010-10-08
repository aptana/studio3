package com.aptana.editor.css.parsing.ast;

public class CSSCommentNode extends CSSNode
{

	public CSSCommentNode(int start, int end)
	{
		super(CSSNodeTypes.COMMENT, start, end);
	}
}
