package com.aptana.js.core.parsing.ast;

public class JSSetterNode extends JSNameValuePairNode
{

	public JSSetterNode(int start, int end)
	{
		super(start, end);
	}

	@Override
	public boolean isSetter()
	{
		return true;
	}
}
