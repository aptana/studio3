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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Max Stepanov
 */
public final class TextUtils
{

	// TODO Move to util plugin

	/**
	 * 
	 */
	private TextUtils()
	{
	}

	/**
	 * Combines by flattening the string arrays into a single string array. Does not add duplicate strings!
	 * 
	 * @param arrays
	 * @return
	 */
	public static String[] combine(String[][] arrays)
	{
		List<String> list = new ArrayList<String>();
		for (String[] array : arrays)
		{
			for (String i : array)
			{
				if (!list.contains(i))
				{
					list.add(i);
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Flattens each 2d String array into a single 2D array containing them all. {{"1", "2"}, {"3", "4"}} and {{"5",
	 * "6"}} becomes {"1", "2"}, {"3", "4"}, {"5", "6"}}. Duplicates are retained.
	 * 
	 * @param arraysArray
	 * @return
	 */
	public static String[][] combineArrays(String[][]... arraysArray)
	{
		List<String[]> list = new ArrayList<String[]>();
		for (String[][] arrays : arraysArray)
		{
			for (String[] array : arrays)
			{
				list.add(array);
			}
		}
		String[][] arrays = new String[list.size()][1];
		for (int i = 0; i < list.size(); i++)
		{
			arrays[i] = list.get(i);
		}

		return arrays;
	}

	public static char[][] removeDuplicates(char[][] arrays)
	{
		List<char[]> list = new ArrayList<char[]>();
		Set<String> strings = new HashSet<String>();
		for (char[] i : arrays)
		{
			String string = String.valueOf(i);
			if (!strings.contains(string))
			{
				list.add(i);
				strings.add(string);
			}
		}
		return list.toArray(new char[list.size()][]);
	}

}
