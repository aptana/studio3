package com.aptana.editor.js.parsing.ast;

/**
 * Represents continue and break statements.
 */
public class JSLabelStatementNode extends JSNode
{
	private String fIdentifier;
	private String fText;

	/**
	 * JSLabelStatementNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 */
	public JSLabelStatementNode(short type, int start, int end)
	{
		this(type, start, end, null);
	}

	/**
	 * JSLabelStatementNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 * @param identifier
	 */
	public JSLabelStatementNode(short type, int start, int end, String identifier)
	{
		super(type, start, end);
		fIdentifier = identifier;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof JSLabelStatementNode))
		{
			return false;
		}
		JSLabelStatementNode other = (JSLabelStatementNode) obj;
		return fIdentifier == null ? other.fIdentifier == null : fIdentifier.equals(other.fIdentifier);
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
