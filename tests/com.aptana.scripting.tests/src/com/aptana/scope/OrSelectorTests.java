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
package com.aptana.scope;

import junit.framework.TestCase;

public class OrSelectorTests extends TestCase
{
	/**
	 * testPrefixThenNonMatch
	 */
	public void testPrefixThenNonMatch()
	{
		IScopeSelector selector = new ScopeSelector("source, string.quoted.single.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertFalse(selector.matches("string.quoted.double.ruby"));
	}

	/**
	 * testNonMatchThenPrefix
	 */
	public void testNonMatchThenPrefix()
	{
		IScopeSelector selector = new ScopeSelector("source.php, string.quoted");

		assertFalse(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	/**
	 * testNamesArePrefixes
	 */
	public void testNamesArePrefixes()
	{
		IScopeSelector selector = new ScopeSelector("source, string.quoted");

		assertTrue(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	/**
	 * testExactThenNonMatch
	 */
	public void testExactThenNonMatch()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby, string.quoted.single.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertFalse(selector.matches("string.quoted.double.ruby"));
	}

	/**
	 * testNonMatchThenExact
	 */
	public void testNonMatchThenExact()
	{
		IScopeSelector selector = new ScopeSelector("source.php, string.quoted.double.ruby");

		assertFalse(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	/**
	 * testNamesAreExact
	 */
	public void testNamesAreExact()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby, string.quoted.double.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	/**
	 * testMixedMatch
	 */
	public void testMixedMatch()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string, source.php string");

		assertTrue(selector.matches("source.ruby string"));
		assertTrue(selector.matches("source.php string"));
		assertTrue(selector.matches("source.ruby string.quoted"));
		assertTrue(selector.matches("source.php string.quoted"));
	}
}
