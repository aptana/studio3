package com.aptana.js.core.parsing.ast;

public class JSGetterNode extends JSNameValuePairNode
{

	public JSGetterNode(int start, int end)
	{
		super(start, end);
	}

	@Override
	public boolean isGetter()
	{
		return true;
	}
}
