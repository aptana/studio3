/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg.parsing;

import java.io.IOException;

import com.aptana.css.core.ICSSConstants;
import com.aptana.js.core.IJSConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.xml.core.parsing.Terminals;
import com.aptana.xml.core.parsing.XMLParser;
import com.aptana.xml.core.parsing.ast.XMLElementNode;

/**
 * SVGParser
 */
public class SVGParser extends XMLParser
{
	private String fSource;

	/**
	 * advanceToCloseTag
	 * 
	 * @param elementName
	 */
	private int advanceToCloseTag(String elementName)
	{
		int start = -1;
		try
		{
			advance();

			while (fCurrentLexeme.getId() != Terminals.EOF)
			{
				if (fCurrentLexeme.getId() == Terminals.LESS_SLASH)
				{
					start = fCurrentLexeme.getStart() - 1;
					advance();
					String text = (String) fCurrentLexeme.value;

					if (text.equals(elementName))
					{
						// Read '>'
						advance();

						// adjusts the ending offset of current element to include the entire block
						((ParseNode) fCurrentElement).setLocation( //
								fCurrentElement.getStartingOffset(), //
								fCurrentLexeme.getEnd() //
								);

						closeElement();

						return start;
					}
				}

				advance();
			}
		}
		catch (Exception e)
		{
		}
		return -1;
	}

	@Override
	protected void parse(IParseState parseState, WorkingParseResult working) throws Exception
	{
		try
		{
			fSource = parseState.getSource();
			super.parse(parseState, working);
		}
		finally
		{
			fSource = null;
		}
	}

	/**
	 * processLanguage
	 * 
	 * @param language
	 * @param elementName
	 * @throws IOException
	 * @throws beaver.Scanner.Exception
	 */
	private void processLanguage(String language, String elementName) throws beaver.Scanner.Exception, IOException
	{
		// grab offset after '>' in open tag
		int startingOffset = fCurrentLexeme.getEnd() + 1;

		// advance to the matching close tag
		int endingOffset = advanceToCloseTag(elementName);

		// grab the source between the open and close tag
		String source = getSource(startingOffset, endingOffset);

		try
		{
			IParseNode result = ParserPoolFactory.parse(language, source, startingOffset).getRootNode();

			fCurrentElement.addChild(result);
		}
		catch (Exception e)
		{
		}

		processEndTag();
	}

	private String getSource(int start, int end)
	{
		return fSource.substring(start, end + 1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.xml.parsing.XMLParser#processStartTag()
	 */
	@Override
	protected void processStartTag() throws beaver.Scanner.Exception, IOException
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
