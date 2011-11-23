/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import beaver.Scanner;
import beaver.Scanner.Exception;
import beaver.Symbol;

import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.ITypePredicate;
import com.aptana.parsing.lexer.Lexeme;

/**
 * FlexLexemeProvider
 */
public abstract class FlexLexemeProvider<T extends ITypePredicate> extends AbstractLexemeProvider<T, Scanner>
{
	/**
	 * FlexLexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @param scanner
	 */
	protected FlexLexemeProvider(IDocument document, int offset, Scanner scanner)
	{
		super(document, offset, scanner);
	}

	/**
	 * FlexLexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @param includeOffset
	 * @param scanner
	 */
	protected FlexLexemeProvider(IDocument document, int offset, int includeOffset, Scanner scanner)
	{
		super(document, offset, includeOffset, scanner);
	}

	/**
	 * FlexLexemeProvider
	 * 
	 * @param document
	 * @param range
	 * @param scanner
	 */
	protected FlexLexemeProvider(IDocument document, IRange range, Scanner scanner)
	{
		super(document, range, scanner);
	}

	/**
	 * createLexemeList
	 * 
	 * @param source
	 * @param scanner
	 */
	protected void createLexemeList(IDocument document, int offset, int length, Scanner scanner)
	{
		try
		{
			setSource(scanner, document.get(offset, length));

			Symbol token = scanner.nextToken();

			while (token.getId() != 0)
			{
				T type = getTypeFromId(token.getId());
				int start = token.getStart() + offset;
				int end = token.getEnd() + offset;
				Lexeme<T> lexeme = new Lexeme<T>(type, start, end, token.value.toString());

				addLexeme(lexeme);

				token = scanner.nextToken();
			}
		}
		catch (IOException e)
		{
		}
		catch (Exception e)
		{
		}
		catch (BadLocationException e)
		{
		}
	}

	/**
	 * getTypeFromId
	 * 
	 * @param id
	 * @return
	 */
	protected abstract T getTypeFromId(short id);

	/**
	 * setSource
	 * 
	 * @param source
	 */
	protected abstract void setSource(Scanner scanner, String source);
}
