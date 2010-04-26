package com.aptana.editor.ruby.core;

import com.aptana.editor.common.text.CommonDoubleClickStrategy;

public class RubyDoubleClickStrategy extends CommonDoubleClickStrategy
{

	@Override
	protected boolean isIdentifierPart(char c)
	{
		return super.isIdentifierPart(c) || c == '!' || c == '?';
	}
}
