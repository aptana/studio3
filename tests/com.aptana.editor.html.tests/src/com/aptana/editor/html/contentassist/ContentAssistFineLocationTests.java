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
package com.aptana.editor.html.contentassist;

import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor.LocationType;

public class ContentAssistFineLocationTests extends LocationTestCase
{
	/**
	 * testOpenBracket
	 */
	public void testOpenBracket()
	{
		String source = "<";

		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1)
		);
	}
	
	/**
	 * testUnclosedOpenTag
	 */
	public void testUnclosedOpenTag()
	{
		String source = "<body";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length())
		);
	}
	
	/**
	 * testUnclosedOpenTagWithIncompleteAttribute
	 */
	public void testUnclosedOpenTagWithIncompleteAttribute()
	{
		String source = "<body onload";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, source.length())
		);
	}
	
	/**
	 * testUnclosedOpenTagWithIncompleteAttribute2
	 */
	public void testUnclosedOpenTagWithIncompleteAttribute2()
	{
		String source = "<body onload=";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, source.length() - 1),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_VALUE, source.length())
		);
	}
	
	/**
	 * testUnclosedOpenTagWithAttribute
	 */
	public void testUnclosedOpenTagWithAttribute()
	{
		String source = "<body onload=\"init()\"";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, 12),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_VALUE, 13, source.length() - 1),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, source.length())
		);
	}
	
	/**
	 * testOpenTagNoElementName
	 */
	public void testOpenTagNoElementName()
	{
		String source = "<>";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1)
		);
	}
	
	/**
	 * testOpenTagElementName
	 */
	public void testOpenTagElementName()
	{
		String source = "<body>";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 1)
		);
	}
	
	/**
	 * testOpenTagElementName2
	 */
	public void testOpenTagElementName2()
	{
		String source = "<body >";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 2),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, source.length() - 1)
		);
	}
	
	/**
	 * testOpenScriptElement
	 */
	public void testOpenScriptElement()
	{
		String source = "<script>";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 1)
		);
	}
	
	/**
	 * testOpenStyleElement
	 */
	public void testOpenStyleElement()
	{
		String source = "<style>";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 1)
		);
	}
	
	/**
	 * testOpenTagWithClassAttribute
	 */
	public void testOpenTagWithClassAttribute()
	{
		String source = "<body class=\"testing\">";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, 11),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_VALUE, 12, source.length() - 2),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, source.length() - 1)
		);
	}
	
	/**
	 * testOpenTagWithIDAttribute
	 */
	public void testOpenTagWithIDAttribute()
	{
		String source = "<body id=\"testing\">";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, 8),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_VALUE, 9, source.length() - 2),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, source.length() - 1)
		);
	}

	/**
	 * testSelfClosingTag
	 */
	public void testSelfClosingTag()
	{
		String source = "<body/>";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5)
		);
	}
}
