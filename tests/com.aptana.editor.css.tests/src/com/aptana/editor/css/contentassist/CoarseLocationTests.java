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
package com.aptana.editor.css.contentassist;

import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.css.contentassist.CSSContentAssistProcessor.LocationType;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;

public class CoarseLocationTests extends TestCase
{
	/**
	 * coarseLocationTests
	 * 
	 * @param source
	 * @param startingOffset
	 * @param endingOffset
	 * @param expectedLocation
	 */
	protected void coarseLocationTests(String source, LocationTypeRange ... ranges)
	{
		IDocument document = TestUtil.createDocument(source);
		CSSContentAssistProcessor processor = new CSSContentAssistProcessor(null);
		
		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				LexemeProvider<CSSTokenType> lexemeProvider = processor.createLexemeProvider(document, offset); 
				LocationType location = processor.getCoarseLocationType(lexemeProvider, offset);
				String message = MessageFormat.format(
					"Expected {0} at location {1} of ''{2}''",
					range.location.toString(),
					Integer.toString(offset),
					source
				);
				assertEquals(message, range.location, location);
			}
		}
	}
	
	/**
	 * testNoSource
	 */
	public void testNoSource()
	{
		String source = "";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, source.length())
		);
	}
	
	/**
	 * testElementOnly
	 */
	public void testElementOnly()
	{
		String source = "body";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, source.length())
		);
	}
	
	/**
	 * testElementAndClass
	 */
	public void testElementAndClass()
	{
		String source = "body.myClass";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, source.length())
		);
	}
	
	/**
	 * testElementAndID
	 */
	public void testElementAndID()
	{
		String source = "body#myID";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, source.length())
		);
	}
	
	/**
	 * testMultipleElements
	 */
	public void testMultipleElements()
	{
		String source = "a, b, body";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, source.length())
		);
	}
	
	/**
	 * testEmptyBody
	 */
	public void testEmptyBody()
	{
		String source = "body {}";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, 5),
			new LocationTypeRange(LocationType.INSIDE_RULE, 6),
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 7, source.length())
		);
	}
	
	/**
	 * testEmptyBody2
	 */
	public void testEmptyBody2()
	{
		String source = "body {\n  \n}";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, 5),
			new LocationTypeRange(LocationType.INSIDE_RULE, 6, 10),
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 11, source.length())
		);
	}
	
	/**
	 * testEmptyBody2
	 */
	public void testTwoRules()
	{
		String source = "body {\n  \n}\n\ntable {\n  \n}";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, 5),
			new LocationTypeRange(LocationType.INSIDE_RULE, 6, 10),
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 11, 19),
			new LocationTypeRange(LocationType.INSIDE_RULE, 20, 24),
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 25, source.length())
		);
	}
}
