package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSNaryNode extends JSNode
{
	/**
	 * JSNaryNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSNaryNode(short type, int start, int end, JSNode... children)
	{
		super(type, start, end, children);
	}

	/**
	 * appendChildren
	 * 
	 * @param buffer
	 */
	protected void appendChildren(StringBuilder buffer)
	{
		boolean first = true;
		String delimiter = this.getDelimiter();

		for (IParseNode child : this)
		{
			if (!first)
			{
				buffer.append(delimiter); //$NON-NLS-1$
			}
			else
			{
				first = false;
			}

			buffer.append(child);
		}
	}

	/**
	 * appendCloseText
	 * 
	 * @param buffer
	 */
	protected void appendCloseText(StringBuilder buffer)
	{
		// do nothing, sub-classes should override
	}

	/**
	 * appendOpenText
	 * 
	 * @param buffer
	 */
	protected void appendOpenText(StringBuilder buffer)
	{
		// do nothing, sub-classes should override
	}

	/**
	 * getDelimiter
	 * 
	 * @return
	 */
	protected String getDelimiter()
	{
		return ", ";
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();

		this.appendOpenText(buffer);
		this.appendChildren(buffer);
		this.appendCloseText(buffer);

		if (getSemicolonIncluded())
		{
			buffer.append(";");
		}

		return buffer.toString();
	}
}
