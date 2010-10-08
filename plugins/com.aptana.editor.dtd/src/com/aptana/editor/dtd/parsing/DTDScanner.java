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
package com.aptana.editor.dtd.parsing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.editor.dtd.DTDSourceScanner;
import com.aptana.editor.dtd.parsing.lexer.DTDTokenType;

public class DTDScanner extends Scanner
{
	private static class DTDParserScanner extends DTDSourceScanner
	{
		protected IToken createToken(DTDTokenType type)
		{
			return new Token(type);
		}

		protected IDocument getDocument()
		{
			return this.fDocument;
		}
	}

	private static final Pattern ENTITY = Pattern.compile("%([^; \\t\\n]+);"); //$NON-NLS-1$

	private DTDParserScanner _sourceScanner;
	private IDocument _document;
	private Map<String, String> _entities;
	private Stack<DTDParserScanner> _nestedScanners;

	/**
	 * DTDScanner
	 */
	public DTDScanner()
	{
		this._sourceScanner = new DTDParserScanner();
		this._nestedScanners = new Stack<DTDParserScanner>();
	}

	/**
	 * createNestedScanner
	 * 
	 * @param text
	 */
	protected void createNestedScanner(String text)
	{
		DTDParserScanner nestedScanner = new DTDParserScanner();
		IDocument document = new Document(text);

		nestedScanner.setRange(document, 0, document.getLength());

		this._nestedScanners.push(nestedScanner);
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
		DTDParserScanner scanner;
		IDocument document;

		if (this._nestedScanners.size() > 0)
		{
			scanner = this._nestedScanners.peek();
			document = scanner.getDocument();
		}
		else
		{
			scanner = this._sourceScanner;
			document = this._document;
		}

		int offset = scanner.getTokenOffset();
		int length = scanner.getTokenLength();
		DTDTokenType type = (data == null) ? DTDTokenType.EOF : (DTDTokenType) data;

		try
		{
			int totalLength = document.getLength();

			if (offset > totalLength)
			{
				offset = totalLength;
			}
			if (length == -1)
			{
				length = 0;
			}

			return new Symbol(type.getIndex(), offset, offset + length - 1, document.get(offset, length));
		}
		catch (BadLocationException e)
		{
			throw new Scanner.Exception(e.getLocalizedMessage());
		}
	}

	/**
	 * getToken
	 * 
	 * @return
	 */
	protected IToken getToken()
	{
		IToken token = null;

		while (this._nestedScanners.size() > 0)
		{
			DTDParserScanner nestedScanner = this._nestedScanners.peek();

			token = nestedScanner.nextToken();

			if (token.isWhitespace() == false && token.getData() == null)
			{
				this._nestedScanners.pop();
				token = null;

				if (Platform.inDevelopmentMode())
				{
					int end = nestedScanner.getTokenOffset() + nestedScanner.getTokenLength();
					int length = nestedScanner.getDocument().getLength();

					if (end != length)
					{
						System.out.println("end = " + end + ", length = " + length); //$NON-NLS-1$ //$NON-NLS-2$
						System.out.println(nestedScanner.getDocument().get());
					}
				}
			}
			else
			{
				break;
			}
		}

		if (token == null)
		{
			token = this._sourceScanner.nextToken();
		}

		return token;
	}

	/**
	 * getValue
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key)
	{
		String result = null;

		if (this._entities != null)
		{
			result = this._entities.get(key);
		}

		return result;
	}

	/**
	 * isComment
	 * 
	 * @param data
	 * @return
	 */
	protected boolean isComment(Object data)
	{
		return (data != null && ((DTDTokenType) data) == DTDTokenType.COMMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see beaver.Scanner#nextToken()
	 */
	@Override
	public Symbol nextToken() throws IOException, Exception
	{
		IToken token = this.getToken();
		Object data = token.getData();

		while (token.isWhitespace() || isComment(data))
		{
			token = this.getToken();
			data = token.getData();
		}

		Symbol result = this.createSymbol(data);

		if (data == DTDTokenType.STRING)
		{
			String text = (String) result.value;
			StringBuffer buffer = new StringBuffer();
			Matcher m = ENTITY.matcher(text);

			while (m.find())
			{
				String name = m.group(1);
				String newText = this.getValue(name);

				if (newText == null)
				{
					newText = name;
				}

				m.appendReplacement(buffer, newText);
			}

			m.appendTail(buffer);

			result = new Symbol(result.getId(), result.getStart(), result.getEnd(), buffer.toString());
		}
		else if (data == DTDTokenType.PE_REF)
		{
			// grab key minus the leading '%' and trailing ';'
			String key = (String) result.value;
			key = key.substring(1, key.length() - 1);

			// grab entity's value
			String text = this.getValue(key);

			// create new scanner
			this.createNestedScanner(text);

			result = this.nextToken();
		}

		return result;
	}

	/**
	 * register
	 * 
	 * @param key
	 * @param value
	 */
	public void register(String key, String value)
	{
		if (this._entities == null)
		{
			this._entities = new HashMap<String, String>();
		}

		// According to the XML 1.1 Specification in Section 4.2:
		// If the same entity is declared more than once, the first declaration encountered is binding;
		// at user option, an XML processor may issue a warning if entities are declared multiple times.
		if (this._entities.containsKey(key) == false)
		{
			this._entities.put(key, value);
		}
	}

	/**
	 * setSource
	 * 
	 * @param document
	 */
	public void setSource(IDocument document)
	{
		this._document = document;
		this._sourceScanner.setRange(document, 0, document.getLength());
		this._nestedScanners.clear();
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
