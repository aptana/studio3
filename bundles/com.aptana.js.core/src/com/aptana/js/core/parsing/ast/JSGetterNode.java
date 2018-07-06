package com.aptana.js.core.parsing.ast;

public class JSGetterNode extends JSNameValuePairNode
{

	private final boolean isStatic;

	public JSGetterNode(int start, int end, boolean isStatic)
	{
		super(start, end);
		this.isStatic = isStatic;
	}

	@Override
	public boolean isGetter()
	{
		return true;
	}

	public boolean isStatic()
	{
		return isStatic;
	}
}
