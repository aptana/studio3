package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.parsing.lexer.JSTokens;

/**
 * Represents continue and break statements.
 */
public class JSLabelStatementNode extends JSNode
{

	private short fType;
	private String fIdentifier;

	private String fText;

	public JSLabelStatementNode(short type, int start, int end)
	{
		this(type, null, start, end);
	}

	public JSLabelStatementNode(short type, String identifier, int start, int end)
	{
		fType = type;
		fIdentifier = identifier;
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString()
	{
		if (fText == null)
		{
			StringBuilder text = new StringBuilder();
			text.append(JSTokens.getTokenName(fType));
			if (fIdentifier != null)
			{
				text.append(" ").append(fIdentifier); //$NON-NLS-1$
			}
			if (getSemicolonIncluded())
			{
				text.append(";"); //$NON-NLS-1$
			}
			fText = text.toString();
		}
		return fText;
	}
}
