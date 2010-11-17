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
package com.aptana.editor.svg.parsing;

import java.util.LinkedList;
import java.util.Queue;

import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.editor.xml.parsing.XMLParser;
import com.aptana.editor.xml.parsing.ast.XMLElementNode;
import com.aptana.editor.xml.parsing.lexer.XMLTokenType;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;

/**
 * SVGParser
 */
public class SVGParser extends XMLParser
{
	/**
	 * advanceToCloseTag
	 * 
	 * @param elementName
	 */
	private void advanceToCloseTag(String elementName)
	{
		String closeTagStart = "</" + elementName; //$NON-NLS-1$
		int length = closeTagStart.length();

		try
		{
			this.advance();

			while (fCurrentLexeme.getType() != XMLTokenType.EOF)
			{
				if (fCurrentLexeme.getType() == XMLTokenType.END_TAG)
				{
					String text = fCurrentLexeme.getText();

					if (text.startsWith(closeTagStart))
					{
						char c = text.charAt(length);

						if (c == '>' || Character.isWhitespace(c))
						{
							break;
						}
					}
				}

				this.advance();
			}
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * offsetNodes
	 * 
	 * @param offset
	 * @param result
	 */
	private void offsetNodes(int offset, IParseNode result)
	{
		// TODO: This really should be part of the IParseNode interface and should
		// automatically be recursive. We have code like this all of the place
		Queue<IParseNode> nodes = new LinkedList<IParseNode>();

		nodes.offer(result);

		while (nodes.isEmpty() == false)
		{
			IParseNode node = nodes.poll();

			if (node instanceof ParseNode)
			{
				((ParseNode) node).addOffset(offset);
			}

			for (IParseNode child : node)
			{
				if (child instanceof ParseNode)
				{
					nodes.offer(child);
				}
			}
		}
	}

	/**
	 * processLanguage
	 * 
	 * @param language
	 * @param elementName
	 */
	private void processLanguage(String language, String elementName)
	{
		// grab offset after '>' in open tag
		int startingOffset = fCurrentLexeme.getEndingOffset() + 1;
		
		// advance to the matching close tag
		this.advanceToCloseTag(elementName);
		
		// grab the offset just before '<' in the close tag
		int endingOffset = fCurrentLexeme.getStartingOffset() - 1;
		
		// grab the source between the open and close tag
		String source = this.getSource(startingOffset, endingOffset - startingOffset + 1);
		
		try
		{
			IParseNode result = ParserPoolFactory.parse(language, source);

			// offset to re-align with SVG source offsets
			offsetNodes(startingOffset, result);

			fCurrentElement.addChild(result);
		}
		catch (Exception e)
		{
		}

		this.processEndTag();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.xml.parsing.XMLParser#processStartTag()
	 */
	@Override
	protected void processStartTag()
	{
		super.processStartTag();

		if (fCurrentElement instanceof XMLElementNode)
		{
			XMLElementNode element = (XMLElementNode) fCurrentElement;

			if (element.isSelfClosing() == false)
			{
				String elementName = element.getName();

				if ("script".equals(elementName)) //$NON-NLS-1$
				{
					this.processLanguage(IJSParserConstants.LANGUAGE, elementName);
				}
				else if ("style".equals(elementName)) //$NON-NLS-1$
				{
					this.processLanguage(ICSSParserConstants.LANGUAGE, elementName);
				}
			}
		}
	}
}
