package com.aptana.editor.js.parsing.ast;

public abstract class JSPrimitiveNode extends JSNode
{
	private String fText;

	/**
	 * JSPrimitiveNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 * @param text
	 */
	public JSPrimitiveNode(short type, String text)
	{
		super(type);

		fText = text;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
		{
			return false;
		}
		if (!(obj instanceof JSPrimitiveNode))
		{
			return false;
		}

		return fText.equals(((JSPrimitiveNode) obj).fText);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#getText()
	 */
	@Override
	public String getText()
	{
		return fText;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return 31 * super.hashCode() + fText.hashCode();
	}
}
