package com.aptana.editor.css.formatter.nodes;

import java.util.Set;

import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

/**
 * A default tag node formatter is responsible of the formatting of a tag that has a begin and end, however, should not
 * be indented.
 * 
 */
public class FormatterCSSDeclarationNode extends FormatterBlockWithBeginNode
{
	private String element;

	/**
	 * @param document
	 */
	public FormatterCSSDeclarationNode(IFormatterDocument document, String element)
	{
		super(document);
		this.element = element;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	protected boolean isIndenting()
	{
		Set<String> set = getDocument().getSet(CSSFormatterConstants.INDENT_EXCLUDED_TAGS);
		return !set.contains(element);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingNewLine()
	 */
	protected boolean isAddingNewLine()
	{
		Set<String> set = getDocument().getSet(CSSFormatterConstants.NEW_LINES_EXCLUDED_TAGS);
		return !set.contains(element);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode#getBlankLinesAfter(com.aptana.formatter.IFormatterContext
	 * )
	 */
	protected int getBlankLinesAfter(IFormatterContext context)
	{
		return getInt(CSSFormatterConstants.LINES_AFTER_ELEMENTS);
	}
}
