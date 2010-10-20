package com.aptana.editor.css.formatter.nodes;

import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterCommentNode;

public class FormatterCSSCommentNode extends FormatterCommentNode
{

	/**
	 * Constructs a new formatter node for HTML comments
	 * 
	 * @param document
	 * @param startOffset
	 * @param endOffset
	 */
	public FormatterCSSCommentNode(IFormatterDocument document, int startOffset, int endOffset)
	{
		super(document, startOffset, endOffset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterCommentNode#getWrappingKey()
	 */
	public String getWrappingKey()
	{
		return CSSFormatterConstants.WRAP_COMMENTS;
	}

}
