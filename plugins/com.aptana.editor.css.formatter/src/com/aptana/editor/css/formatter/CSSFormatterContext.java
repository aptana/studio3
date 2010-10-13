package com.aptana.editor.css.formatter;

import com.aptana.formatter.FormatterContext;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterNode;

/**
 * An CSS formatter context.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class CSSFormatterContext extends FormatterContext
{

	/**
	 * @param indent
	 */
	public CSSFormatterContext(int indent)
	{
		super(indent);
	}

	/**
	 * Returns true only if the given node is a container node (of type {@link IFormatterContainerNode}).
	 * 
	 * @param node
	 *            An {@link IFormatterNode}
	 * @return True only if the given node is a container node; False, otherwise.
	 * @see com.aptana.formatter.FormatterContext#isCountable(com.aptana.formatter.nodes.IFormatterNode)
	 */
	protected boolean isCountable(IFormatterNode node)
	{
		return node instanceof IFormatterContainerNode;
	}

	/**
	 * Check if the char sequence starts with a '&lt!' sequence or a '&lt!--' sequence. If so, return the length of the
	 * sequence; Otherwise, return 0.
	 * 
	 * @see IFormatterContext#getCommentStartLength(CharSequence, int)
	 */
	public int getCommentStartLength(CharSequence chars, int offset)
	{
		if (chars.length() > offset + 1 && chars.charAt(offset) == '/' && chars.charAt(offset+ 1) == '*')
		{
			return 2;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#getWrappingCommentPrefix()
	 */
	public String getWrappingCommentPrefix()
	{
		return " * ";
	}
}