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
package com.aptana.editor.js.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.editor.js.parsing.lexer.JSTokenType;

public class JSScanner extends Scanner
{
	private JSTokenScanner fTokenScanner;
	private IDocument fDocument;
	private List<Symbol> fSDocComments;
	private List<Symbol> fVSDocComments;
	private List<Symbol> fSingleLineComments;
	private List<Symbol> fMultiLineComments;

	/**
	 * JSScanner
	 */
	public JSScanner()
	{
		fTokenScanner = new JSTokenScanner();
		fSDocComments = new ArrayList<Symbol>();
		fVSDocComments = new ArrayList<Symbol>();
		fSingleLineComments = new ArrayList<Symbol>();
		fMultiLineComments = new ArrayList<Symbol>();
	}

	/**
	 * createSymbol
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	protected Symbol createSymbol(Object data) throws Exception
	{
		int offset = fTokenScanner.getTokenOffset();
		int length = fTokenScanner.getTokenLength();
		JSTokenType type = (data == null) ? JSTokenType.EOF : (JSTokenType) data;

		try
		{
			int totalLength = fDocument.getLength();

			if (offset > totalLength)
			{
				offset = totalLength;
			}
			if (length == -1)
			{
				length = 0;
			}

			return new Symbol(type.getIndex(), offset, offset + length - 1, fDocument.get(offset, length));
		}
		catch (BadLocationException e)
		{
			throw new Scanner.Exception(e.getLocalizedMessage());
		}
	}

	/**
	 * getMultiLineComments
	 * 
	 * @return
	 */
	public List<Symbol> getMultiLineComments()
	{
		return fMultiLineComments;
	}
	
	/**
	 * getSDocComments
	 * 
	 * @return
	 */
	public List<Symbol> getSDocComments()
	{
		return fSDocComments;
	}

	/**
	 * getSingleLineComments
	 * 
	 * @return
	 */
	public List<Symbol> getSingleLineComments()
	{
		return fSingleLineComments;
	}
	
	/**
	 * getVSDocComments
	 * 
	 * @return
	 */
	public List<Symbol> getVSDocComments()
	{
		return fVSDocComments;
	}

	/**
	 * isComment
	 * 
	 * @param data
	 * @return
	 */
	private boolean isComment(Object data)
	{
		boolean result = false;

		if (data != null)
		{
			JSTokenType type = (JSTokenType) data;

			switch (type)
			{
				case SINGLELINE_COMMENT:
				case MULTILINE_COMMENT:
				case SDOC:
				case VSDOC:
					result = true;
					break;
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see beaver.Scanner#nextToken()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Symbol nextToken() throws IOException, Exception
	{
		Symbol vsdoc = null;

		IToken token = fTokenScanner.nextToken();
		Object data = token.getData();

		while (token.isWhitespace() || isComment(data))
		{
			// save jsdoc comments for later processing
			if (data != null)
			{
				JSTokenType type = (JSTokenType) data;

				switch (type)
				{
					case SINGLELINE_COMMENT:
						fSingleLineComments.add(createSymbol(data));
						break;
						
					case MULTILINE_COMMENT:
						fMultiLineComments.add(createSymbol(data));
						break;
						
					case SDOC:
						fSDocComments.add(createSymbol(data));
						break;

					case VSDOC:
						int offset = fTokenScanner.getTokenOffset();
						int length = fTokenScanner.getTokenLength();

						if (vsdoc == null)
						{
							vsdoc = new Symbol(JSTokenType.VSDOC.getIndex(), offset, offset + length - 1, new LinkedList<Symbol>());
						}

						((List<Symbol>) vsdoc.value).add(createSymbol(data));
						break;

					default:
						break;
				}
			}

			// ignores whitespace and comments
			token = fTokenScanner.nextToken();
			data = token.getData();
		}

		if (vsdoc != null)
		{
			fVSDocComments.add(vsdoc);
		}

		return createSymbol(data);
	}

	/**
	 * setSource
	 * 
	 * @param document
	 */
	public void setSource(IDocument document)
	{
		fDocument = document;
		fTokenScanner.setRange(fDocument, 0, fDocument.getLength());

		fSDocComments.clear();
		fVSDocComments.clear();
	}

	/**
	 * setSource
	 * 
	 * @param text
	 */
	public void setSource(String text)
	{
		setSource(new Document(text));
	}
}
