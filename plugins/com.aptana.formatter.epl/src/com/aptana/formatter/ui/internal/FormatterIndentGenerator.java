package com.aptana.formatter.ui.internal;

import java.util.Arrays;

import com.aptana.formatter.IFormatterIndentGenerator;

/**
 * @since 2.0
 */
public class FormatterIndentGenerator implements IFormatterIndentGenerator
{

	private final char[] chars;
	private final int indentationSize;
	private final int tabSize;

	public FormatterIndentGenerator(char ch, int indentationSize, int tabSize)
	{
		this.chars = new char[256];
		Arrays.fill(chars, ch);
		this.indentationSize = indentationSize;
		this.tabSize = tabSize;
	}

	public void generateIndent(final int indentLevel, StringBuilder target)
	{
		if (indentLevel > 0)
		{
			int size = indentLevel * indentationSize;
			while (size > 0)
			{
				final int step = Math.min(size, chars.length);
				target.append(chars, 0, step);
				size -= step;
			}
		}
	}

	public int getTabSize()
	{
		return tabSize;
	}

}
