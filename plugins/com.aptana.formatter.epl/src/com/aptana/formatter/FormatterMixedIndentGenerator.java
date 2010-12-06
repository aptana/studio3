/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter;


/**
 * @since 2.0
 */
public class FormatterMixedIndentGenerator implements IFormatterIndentGenerator
{

	private final int indentSize;
	private final int tabSize;

	/**
	 * @param indentSize
	 * @param tabSize
	 */
	public FormatterMixedIndentGenerator(int indentSize, int tabSize)
	{
		this.indentSize = Math.max(indentSize, 1);
		this.tabSize = Math.max(tabSize, 1);
	}

	public void generateIndent(int indentLevel, StringBuilder target)
	{
		final int indent = indentLevel * indentSize;
		final int tabCount = indent / tabSize;
		for (int i = 0; i < tabCount; ++i)
		{
			target.append('\t');
		}
		final int spaceCount = indent % tabSize;
		for (int i = 0; i < spaceCount; ++i)
		{
			target.append(' ');
		}
	}

	public int getTabSize()
	{
		return tabSize;
	}

}
