/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.parsing;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;

import beaver.Scanner;
import beaver.Symbol;

public class CompositeParserScanner extends Scanner
{

	private CompositeTokenScanner fTokenScanner;
	private IDocument fDocument;

	public CompositeParserScanner(CompositeTokenScanner tokenScanner)
	{
		fTokenScanner = tokenScanner;
	}

	public IDocument getSource()
	{
		return fDocument;
	}

	public void setSource(String text)
	{
		setSource(new Document(text));
	}

	public void setSource(IDocument document)
	{
		fDocument = document;
		fTokenScanner.setRange(fDocument, 0, fDocument.getLength());
	}

	@Override
	public Symbol nextToken() throws IOException, Exception
	{
		IToken token = fTokenScanner.nextToken();
		while (isIgnored(token))
		{
			token = fTokenScanner.nextToken();
		}

		int offset = fTokenScanner.getTokenOffset();
		int length = fTokenScanner.getTokenLength();
		if (token.isEOF()) {
			return createSymbol(offset, offset, "", token); //$NON-NLS-1$
		}

		try
		{
			String text = fDocument.get(offset, length);
			return createSymbol(offset, offset + length - 1, text, token);
		}
		catch (BadLocationException e)
		{
			throw new Scanner.Exception(e.getLocalizedMessage());
		}
	}

	protected Symbol createSymbol(int start, int end, String text, IToken token)
	{
		return new Symbol((short) 0, start, end, text);
	}

	protected boolean isIgnored(IToken token)
	{
		// by default ignores whitespace
		return token.isWhitespace();
	}

	protected CompositeTokenScanner getTokenScanner()
	{
		return fTokenScanner;
	}
}
