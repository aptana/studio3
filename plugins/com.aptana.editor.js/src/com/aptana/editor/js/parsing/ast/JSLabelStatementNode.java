package com.aptana.editor.js.parsing.ast;

/**
 * Represents continue and break statements.
 */
public abstract class JSLabelStatementNode extends JSNode
{
	private String fIdentifier;
	private String fText;

	/**
	 * JSLabelStatementNode
	 * 
	 * @param type
	 */
	public JSLabelStatementNode(short type)
	{
		this(type, null);
	}

	/**
	 * JSLabelStatementNode
	 * 
	 * @param type
	 * @param identifier
	 */
	public JSLabelStatementNode(short type, String identifier)
	{
		super(type);
		
		fIdentifier = identifier;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#equals(java.lang.Object)
	 */
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

	/**
	 * geKeyword
	 * 
	 * @return
	 */
	protected abstract String getKeyword();

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		return hash * 31 + (fIdentifier == null ? 0 : fIdentifier.hashCode());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	@Override
	public String toString()
	{
		if (fText == null)
		{
			StringBuilder text = new StringBuilder();

			text.append(this.getKeyword());

			if (fIdentifier != null)
			{
				text.append(" ").append(fIdentifier); //$NON-NLS-1$
			}

			this.appendSemicolon(text);

			fText = text.toString();
		}

		return fText;
	}
}
