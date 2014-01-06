/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.contentassist.ILexemeProvider;
import com.aptana.editor.html.HTMLTestUtil;
import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor.LocationType;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;

public abstract class LocationTestCase
{
	/**
	 * createDocument
	 * 
	 * @param partitionType
	 * @param source
	 * @return
	 */
	protected IDocument createDocument(String source, boolean stripCursor)
	{
		return HTMLTestUtil.createDocument(source, stripCursor);
	}

	/**
	 * coarseLocationTests
	 * 
	 * @param source
	 * @param startingOffset
	 * @param endingOffset
	 * @param expectedLocation
	 */
	protected void coarseLocationTests(String source, LocationTypeRange... ranges)
	{
		IDocument document = this.createDocument(source, false);
		HTMLContentAssistProcessor processor = new HTMLContentAssistProcessor(null);

		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				ILexemeProvider<HTMLTokenType> lexemeProvider = processor.createLexemeProvider(document, offset);
				LocationType LocationType = processor.getCoarseLocationType(document, lexemeProvider, offset);
				String message = MessageFormat.format("Expected {0} at LocationType {1} of ''{2}''",
						range.LocationType.toString(), Integer.toString(offset), source);
				assertEquals(message, range.LocationType, LocationType);
			}
		}
	}

	/**
	 * fineLocationTests
	 * 
	 * @param source
	 * @param startingOffset
	 * @param endingOffset
	 * @param expectedLocation
	 */
	protected void fineLocationTests(String source, LocationTypeRange... ranges)
	{
		IDocument document = this.createDocument(source, false);
		HTMLContentAssistProcessor processor = new HTMLContentAssistProcessor(null);

		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				ILexemeProvider<HTMLTokenType> lexemeProvider = processor.createLexemeProvider(document, offset);
				LocationType LocationType = processor.getOpenTagLocationType(lexemeProvider, offset);
				String message = MessageFormat.format("Expected {0} at LocationType {1} of ''{2}''",
						range.LocationType.toString(), Integer.toString(offset), source);
				assertEquals(message, range.LocationType, LocationType);
			}
		}
	}
}
