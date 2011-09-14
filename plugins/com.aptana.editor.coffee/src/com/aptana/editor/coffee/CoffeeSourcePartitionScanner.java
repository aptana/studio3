/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee;

import org.eclipse.jface.text.BadLocationException;

import com.aptana.editor.common.text.rules.SourceConfigurationPartitionScanner;
import com.aptana.editor.js.IRegexpDivisionDisambiguator;

public class CoffeeSourcePartitionScanner extends SourceConfigurationPartitionScanner implements IRegexpDivisionDisambiguator
{
	/**
	 * CoffeeSourcePartitionScanner
	 */
	public CoffeeSourcePartitionScanner()
	{
		super(CoffeeSourceConfiguration.getDefault());
	}

	/**
	 * isValidDivisionStart This was copy-pasted from the JSSourcePartitionScanner.
	 * 
	 * @return
	 */
	public boolean isValidDivisionStart()
	{
		// start backtracking one character before the current position
		int offset = fOffset - 1;
		boolean result = false;

		try
		{
			while (offset >= 0)
			{
				char c = fDocument.getChar(offset);

				// keep backtracking if we hit whitespace
				if (!Character.isWhitespace(c))
				{
					// Compare to the set of last characters from the following tokens:
					// IDENTIFIER, NUMBER, REGEX, STRING, RPAREN, PLUS_PLUS, MINUS_MINUS,
					// RBRACKET, RCURLY, FALSE, NULL, THIS, TRUE. Note that we make an
					// exception with PLUS_PLUS and MINUS_MINUS and backtrack one extra
					// character to differentiate those from PLUS and MINUS, respectively.
					// We use this same set of tokens in JSTokenScanner.isValidDivisionStart.
					switch (c)
					{
						case '$':
						case '_':
						case '/':
						case '\'':
						case '"':
						case ')':
						case ']':
						case '}':
							result = true;
							break;

						case '-':
						case '+':
							char curr = c;

							if (offset > 0)
							{
								c = fDocument.getChar(offset - 1);

								result = (c == curr);
							}
							break;

						default:
							result = ('0' <= c && c <= '9') || ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
					}

					break;
				}

				offset--;
			}
		}
		catch (BadLocationException e) // $codepro.audit.disable emptyCatchClause
		{
		}

		return result;

	}

}
