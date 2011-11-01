/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beaver.Scanner;
import beaver.Symbol;

/**
 * CachingScanner
 */
public class CachingScanner extends Scanner
{
	private static final Symbol EOF = new Symbol(Terminals.EOF, "<end-of-file>");

	Symbol[] _tokens;
	private int _offset;

	public CachingScanner(String source)
	{
		JSFlexScanner scanner = new JSFlexScanner();

		scanner.setSource(source);

		List<Symbol> tokens = new ArrayList<Symbol>();

		try
		{
			Symbol s = scanner.nextToken();

			while (s.getId() != Terminals.EOF)
			{
				tokens.add(s);
				s = scanner.nextToken();
			}
		}
		catch (Throwable t)
		{
		}

		_tokens = tokens.toArray(new Symbol[tokens.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see beaver.Scanner#nextToken()
	 */
	@Override
	public Symbol nextToken() throws IOException, Exception
	{
		return (_offset < _tokens.length) ? _tokens[_offset++] : EOF;
	}

	public void reset()
	{
		_offset = 0;
	}
}
