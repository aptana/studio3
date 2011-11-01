/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.ITypePredicate;
import com.aptana.parsing.lexer.Lexeme;

public abstract class LexemeProvider<T extends ITypePredicate> extends AbstractLexemeProvider<T, ITokenScanner>
{
	private static final Pattern WHITESPACE = Pattern.compile("\\s+", Pattern.MULTILINE); //$NON-NLS-1$

	/**
	 * LexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @param scanner
	 */
	protected LexemeProvider(IDocument document, int offset, ITokenScanner scanner)
	{
		super(document, offset, scanner);
	}

	/**
	 * LexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @param includeOffset
	 * @param scanner
	 */
	protected LexemeProvider(IDocument document, int offset, int includeOffset, ITokenScanner scanner)
	{
		super(document, offset, includeOffset, scanner);
	}

	/**
	 * LexemeProvider
	 * 
	 * @param document
	 * @param range
	 * @param scanner
	 */
	protected LexemeProvider(IDocument document, IRange range, ITokenScanner scanner)
	{
		super(document, range, scanner);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.contentassist.AbstractLexemeProvider#createLexemeList(org.eclipse.jface.text.IDocument,
	 * int, int, java.lang.Object)
	 */
	protected void createLexemeList(IDocument document, int offset, int length, ITokenScanner scanner)
	{
		try
		{
			// prime scanner
			scanner.setRange(document, offset, length);
			IToken token = scanner.nextToken();

			while (token != Token.EOF)
			{
				Object data = token.getData();

				// grab the lexeme particulars
				int tokenOffset = scanner.getTokenOffset();
				int tokenLength = scanner.getTokenLength();
				int endingOffset = tokenOffset + tokenLength;
				String text = document.get(tokenOffset, tokenLength);
				T type = null;
				// skip tokens with null data (typically whitespace)
				if (data != null)
				{
					type = this.getTypeFromData(data);
					Lexeme<T> lexeme = new Lexeme<T>(type, tokenOffset, endingOffset - 1, text);
					// add it to our list
					this.addLexeme(lexeme);
				}

				// NOTE: the following is useful during development to capture any
				// scopes that weren't converted to enumerations
				if (Platform.inDevelopmentMode())
				{
					if (data != null)
					{
						if (type == null || !type.isDefined())
						{
							System.out.println("Possible missed token type for text: [" + data + "]~" + text + "~"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
					else
					{
						Matcher m = WHITESPACE.matcher(text);

						if (!m.matches())
						{
							System.out.println("Possible missed token type for text: ~" + text + "~"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}

				// advance
				token = scanner.nextToken();
			}
		}
		catch (BadLocationException e)
		{
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	/**
	 * getTypeFromData
	 * 
	 * @param data
	 * @return
	 */
	protected abstract T getTypeFromData(Object data);
}
