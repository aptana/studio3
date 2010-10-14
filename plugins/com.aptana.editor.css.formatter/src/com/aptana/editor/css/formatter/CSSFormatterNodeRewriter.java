package com.aptana.editor.css.formatter;

import com.aptana.editor.css.formatter.nodes.FormatterCSSCommentNode;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterNodeRewriter;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

public class CSSFormatterNodeRewriter extends FormatterNodeRewriter
{

	public CSSFormatterNodeRewriter(IParseRootNode parseResult, FormatterDocument document)
	{
		for (IParseNode commentNode : parseResult.getCommentNodes())
		{
			addComment(commentNode.getStartingOffset(), commentNode.getEndingOffset(), commentNode);
		}
	}

	public void rewrite(IFormatterContainerNode root)
	{
		super.rewrite(root);
		attachComments(root);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterNodeRewriter#createCommentNode(com.aptana.formatter.IFormatterDocument,
	 * int, int, java.lang.Object)
	 */
	@Override
	protected IFormatterNode createCommentNode(IFormatterDocument document, int startOffset, int endOffset,
			Object object)
	{
		return new FormatterCSSCommentNode(document, startOffset, endOffset);
	}

}