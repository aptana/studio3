package com.aptana.editor.markdown.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

class EscapeCharacterDetector implements IWordDetector
{
	private boolean toggle = false;

	public boolean isWordStart(char c)
	{
		if (c == '\\')
		{
			toggle = true;
			return true;
		}
		return false;
	}

	public boolean isWordPart(char c)
	{
		if (toggle)
		{
			toggle = false;
			return true;
		}
		return false;
	}
}