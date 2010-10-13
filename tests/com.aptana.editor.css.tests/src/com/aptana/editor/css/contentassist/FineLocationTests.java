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

public class FineLocationTests extends TestCase
{
	/**
	 * fineLocationTests
	 * 
	 * @param source
	 * @param startingOffset
	 * @param endingOffset
	 * @param expectedLocation
	 */
	protected void fineLocationTests(String source, LocationTypeRange ... ranges)
	{
		IDocument document = TestUtil.createDocument(source);
		CSSContentAssistProcessor processor = new CSSContentAssistProcessor(null);
		
		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				LexemeProvider<CSSTokenType> lexemeProvider = processor.createLexemeProvider(document, offset); 
				LocationType location = processor.getInsideLocationType(lexemeProvider, offset);
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
	 * testEmptyBody
	 */
	public void testEmptyBody()
	{
		String source = "body {}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6)
		);
	}
	
	/**
	 * testEmptyBody2
	 */
	public void testEmptyBody2()
	{
		String source = "body {\n  \n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6, 10)
		);
	}
	
	/**
	 * testTwoRules
	 */
	public void testTwoRules()
	{
		String source = "body {\n  \n}\n\ntable {\n  \n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6, 10),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 20, 24)
		);
	}
	
	/**
	 * testProperty
	 */
	public void testProperty()
	{
		String source = "body{background}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 15)
		);
	}
	
	/**
	 * testProperty2
	 */
	public void testProperty2()
	{
		String source = "body{\n  background}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 18)
		);
	}
	
	/**
	 * testPropertyNoValue
	 */
	public void testPropertyNoValue()
	{
		String source = "body{background:}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 15),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 16)
		);
	}
	
	/**
	 * testPropertyNoValue
	 */
	public void testPropertyNoValue2()
	{
		String source = "body{\n  background:}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 18),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 19)
		);
	}
	
	/**
	 * testPropertyAndValueNoSemi
	 */
	public void testPropertyAndValueNoSemi()
	{
		String source = "body{background:red}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 15),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 16, 19)
		);
	}
	
	/**
	 * testPropertyAndValueNoSemi2
	 */
	public void testPropertyAndValueNoSemi2()
	{
		String source = "body{\n  background:red\n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 18),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 19, 22),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 23)
		);
	}
	
	/**
	 * testPropertyAndValue
	 */
	public void testPropertyAndValue()
	{
		String source = "body{background:red;}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 15),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 16, 19),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 20)
		);
	}
	
	/**
	 * testPropertyAndValue2
	 */
	public void testPropertyAndValue2()
	{
		String source = "body{\n  background:red;\n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 18),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 19, 22),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 23, 24)
		);
	}
	
	/**
	 * testMultipleProperties
	 */
	public void testMultipleProperties()
	{
		String source = "body{background:red;border: 1 solid black}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 15),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 16, 19),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 20, 26),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 27, 41)
		);
	}
	
	/**
	 * testMultipleProperties2
	 */
	public void testMultipleProperties2()
	{
		String source = "body{\n  background: red;\n  border: 1 solid black\n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 18),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 19, 23),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 24, 33),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 34, 48),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 49)
		);
	}
	
	/**
	 * testSpaceAfterColon
	 */
	public void testSpaceAfterColon()
	{
		String source = ".hello {\n  background: \n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 8, 21),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 22, 24)
		);
	}
	
	/**
	 * testDotInProperty
	 */
	public void testDotInProperty()
	{
		String source = "body { background-color.}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6, 23),
			new LocationTypeRange(LocationType.ERROR, 24)
		);
	}
	
	/**
	 * testHashInProperty
	 */
	public void testHashInProperty()
	{
		String source = "body { background-color#}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6, 23),
			new LocationTypeRange(LocationType.ERROR, 24)
		);
	}
}
