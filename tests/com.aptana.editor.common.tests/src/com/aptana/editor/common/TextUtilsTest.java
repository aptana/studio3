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
package com.aptana.editor.common;

import junit.framework.TestCase;

public class TextUtilsTest extends TestCase
{

	public void testCombine()
	{
		String[][] arrays = new String[][] { new String[] { "one", "two" }, new String[] { "two", "three" } };
		String[] result = TextUtils.combine(arrays);
		assertEquals(3, result.length);
		assertEquals("one", result[0]);
		assertEquals("two", result[1]);
		assertEquals("three", result[2]);
	}

	public void testCombineArrays()
	{
		String[][] array1 = new String[][] { new String[] { "one", "two" }, new String[] { "two", "three" } };
		String[][] array2 = new String[][] { new String[] { "2one", "2two" }, new String[] { "2two", "2three" } };
		String[][] result = TextUtils.combineArrays(array1, array2);
		assertEquals(4, result.length);
		String[] result1 = result[0];
		assertEquals("one", result1[0]);
		assertEquals("two", result1[1]);
		String[] result2 = result[1];
		assertEquals("two", result2[0]);
		assertEquals("three", result2[1]);
		String[] result3 = result[2];
		assertEquals("2one", result3[0]);
		assertEquals("2two", result3[1]);
		String[] result4 = result[3];
		assertEquals("2two", result4[0]);
		assertEquals("2three", result4[1]);
	}

	public void testRemoveDuplicates()
	{
		char[][] arrays = new char[][] { new char[] { 'h', 'i' }, new char[] { 't', 'h', 'e', 'r', 'e' },
				new char[] { 'h', 'i' } };
		char[][] result = TextUtils.removeDuplicates(arrays);
		assertEquals(2, result.length);
		assertEquals("hi", String.valueOf(result[0]));
		assertEquals("there", String.valueOf(result[1]));
	}
}
