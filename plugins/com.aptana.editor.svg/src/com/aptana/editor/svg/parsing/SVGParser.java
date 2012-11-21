/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg.parsing;

import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.xml.parsing.XMLParser;
import com.aptana.editor.xml.parsing.ast.XMLElementNode;
import com.aptana.editor.xml.parsing.lexer.XMLTokenType;
import com.aptana.js.core.IJSConstants;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;

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
			IParseNode result = ParserPoolFactory.parse(language, source, startingOffset).getRootNode();

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
					this.processLanguage(IJSConstants.CONTENT_TYPE_JS, elementName);
				}
				else if ("style".equals(elementName)) //$NON-NLS-1$
				{
					this.processLanguage(ICSSConstants.CONTENT_TYPE_CSS, elementName);
				}
			}
		}
	}
}
