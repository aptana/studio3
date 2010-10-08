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
package com.aptana.editor.css.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.editor.css.parsing.lexer.CSSTokenType;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class CSSScanner extends Scanner
{

	private CSSTokenScanner fTokenScanner;
	private IDocument fDocument;

	private List<IRange> fComments;

	public CSSScanner()
	{
		fTokenScanner = new CSSTokenScanner();
		fComments = new ArrayList<IRange>();
	}

	public void setSource(String text)
	{
		setSource(new Document(text));
	}

	public void setSource(IDocument document)
	{
		fDocument = document;
		fTokenScanner.setRange(fDocument, 0, fDocument.getLength());
		fComments.clear();
	}

	public IRange[] getComments()
	{
		return fComments.toArray(new IRange[fComments.size()]);
	}

	@Override
	public Symbol nextToken() throws IOException, Exception
	{
		IToken token = fTokenScanner.nextToken();
		Object data = token.getData();
		while (token.isWhitespace() || (data != null && data.equals(CSSTokenType.COMMENT)))
		{
			// ignores whitespace and keeps a record of the comments
			if (CSSTokenType.COMMENT.equals(data))
			{
				int offset = fTokenScanner.getTokenOffset();
				int length = fTokenScanner.getTokenLength();
				fComments.add(new Range(offset, offset + length - 1));
			}
			token = fTokenScanner.nextToken();
			data = token.getData();
		}

		int offset = fTokenScanner.getTokenOffset();
		int length = fTokenScanner.getTokenLength();

		short type = CSSTokenType.EOF.getShort();
		if (data != null)
		{
			type = ((CSSTokenType) data).getShort();
		}
		try
		{
			return new Symbol(type, offset, offset + length - 1, fDocument.get(offset, length));
		}
		catch (BadLocationException e)
		{
			throw new Scanner.Exception(e.getLocalizedMessage());
		}
	}
}
