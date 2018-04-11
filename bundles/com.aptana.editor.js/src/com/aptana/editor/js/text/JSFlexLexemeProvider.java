/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import org.eclipse.jface.text.IDocument;

import beaver.Scanner;

import com.aptana.editor.common.contentassist.FlexLexemeProvider;
import com.aptana.js.core.parsing.JSFlexScanner;
import com.aptana.js.core.parsing.JSTokenType;
import com.aptana.parsing.lexer.IRange;

/**
 * JSFlexLexemeProvider
 */
public class JSFlexLexemeProvider extends FlexLexemeProvider<JSTokenType>
{
	/**
	 * Convert the partition that contains the given offset into a list of lexemes.
	 * 
	 * @param document
	 * @param offset
	 * @param includeOffset
	 * @param scanner
	 */
	public JSFlexLexemeProvider(IDocument document, int offset, int includeOffset, Scanner scanner)
	{
		super(document, offset, includeOffset, scanner);
	}

	/**
	 * Convert the partition that contains the given offset into a list of lexemes.
	 * 
	 * @param document
	 * @param offset
	 * @param scanner
	 */
	public JSFlexLexemeProvider(IDocument document, int offset, Scanner scanner)
	{
		super(document, offset, scanner);
	}

	/**
	 * Convert the specified range of text into a list of lexemes
	 * 
	 * @param document
	 * @param range
	 * @param scanner
	 */
	public JSFlexLexemeProvider(IDocument document, IRange range, Scanner scanner)
	{
		super(document, range, scanner);
	}

	@Override
	protected JSTokenType getTypeFromId(short id)
	{
		return JSTokenType.get(id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.FlexLexemeProvider#setSource(java.lang.String)
	 */
	@Override
	protected void setSource(Scanner scanner, String source)
	{
		if (scanner instanceof JSFlexScanner)
		{
			((JSFlexScanner) scanner).setSource(source);
		}
	}
}
