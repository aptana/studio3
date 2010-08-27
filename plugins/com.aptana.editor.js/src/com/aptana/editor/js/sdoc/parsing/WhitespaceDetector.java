package com.aptana.editor.js.sdoc.parsing;

import org.eclipse.jface.text.rules.IWordDetector;

class WhitespaceDetector implements IWordDetector
{
	@Override
	public boolean isWordPart(char c)
	{
		return c == ' ' || c == '\t';
	}

	@Override
	public boolean isWordStart(char c)
	{
		return this.isWordPart(c);
	}
}
