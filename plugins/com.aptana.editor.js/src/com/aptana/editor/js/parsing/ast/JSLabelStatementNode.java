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
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof JSLabelStatementNode))
			return false;
		// FIXME What about when it's null!?
		return fIdentifier.equals(((JSLabelStatementNode) obj).fIdentifier);
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		return hash * 31 + (fIdentifier == null ? 0 : fIdentifier.hashCode());
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
			fText = appendSemicolon(text.toString());
		}
		return fText;
	}
}
