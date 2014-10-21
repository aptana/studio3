/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.internal;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;

import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.parsing.lexer.IRange;
import com.aptana.xml.core.parsing.XMLTokenType;

/**
 * XMLLexemeProvider
 */
public class XMLLexemeProvider extends LexemeProvider<XMLTokenType>
{
	/**
	 * Convert the partition that contains the given offset into a list of lexemes.
	 * 
	 * @param document
	 * @param offset
	 * @param includeOffset
	 * @param scanner
	 */
	public XMLLexemeProvider(IDocument document, int offset, int includeOffset, ITokenScanner scanner)
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
	public XMLLexemeProvider(IDocument document, int offset, ITokenScanner scanner)
	{
		super(document, offset, 0, scanner);
	}

	/**
	 * Convert the specified range of text into a list of lexemes
	 * 
	 * @param document
	 * @param range
	 * @param scanner
	 */
	public XMLLexemeProvider(IDocument document, IRange range, ITokenScanner scanner)
	{
		super(document, range, scanner);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.LexemeProvider#getTypeFromData(java.lang.Object)
	 */
	@Override
	protected XMLTokenType getTypeFromData(Object data)
	{
		if (data instanceof XMLTokenType)
		{
			return (XMLTokenType) data;
		}
		return XMLTokenType.getByScope(data.toString());
	}
}
