package com.aptana.editor.js.parsing.ast;


/**
 * Represents continue and break statements.
 */
public class JSLabelStatementNode extends JSNode
{

	private String fIdentifier;
	private String fText;

	public JSLabelStatementNode(short type, int start, int end)
	{
		this(type, null, start, end);
	}

	public JSLabelStatementNode(short type, String identifier, int start, int end)
	{
		super(type, start, end);
		fIdentifier = identifier;
	}

	@Override
	public String toString()
	{
		if (fText == null)
		{
			StringBuilder text = new StringBuilder();
			switch (getType())
			{
				case JSNodeTypes.CONTINUE:
					text.append("continue"); //$NON-NLS-1$
					break;
				case JSNodeTypes.BREAK:
					text.append("break"); //$NON-NLS-1$
					break;
			}
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
