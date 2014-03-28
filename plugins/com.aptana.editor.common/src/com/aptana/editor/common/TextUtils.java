/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.rules.ICharacterScanner;

/**
 * @author Max Stepanov
 */
public final class TextUtils {

	// TODO Move to util plugin

	/**
	 * 
	 */
	private TextUtils() {
	}

	/**
	 * Combines by flattening the string arrays into a single string array. Does
	 * not add duplicate strings!
	 * 
	 * @param arrays
	 * @return
	 */
	public static String[] combine(String[][] arrays) {
		List<String> list = new ArrayList<String>();
		for (String[] array : arrays) {
			for (String i : array) {
				if (!list.contains(i)) {
					list.add(i);
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Flattens each 2d String array into a single 2D array containing them all.
	 * {{"1", "2"}, {"3", "4"}} and {{"5",
	 * "6"}} becomes {"1", "2"}, {"3", "4"}, {"5", "6"}}. Duplicates are
	 * retained.
	 * 
	 * @param arraysArray
	 * @return
	 */
	static String[][] combineArrays(String[][]... arraysArray) {
		List<String[]> list = new ArrayList<String[]>();
		for (String[][] arrays : arraysArray) {
			for (String[] array : arrays) {
				list.add(array);
			}
		}
		String[][] arrays = new String[list.size()][1];
		for (int i = 0; i < list.size(); i++) {
			arrays[i] = list.get(i);
		}

		return arrays;
	}

	static char[][] removeDuplicates(char[][] arrays) {
		List<char[]> list = new ArrayList<char[]>();
		Set<String> strings = new HashSet<String>();
		for (char[] i : arrays) {
			String string = String.valueOf(i);
			if (!strings.contains(string)) {
				list.add(i);
				strings.add(string);
			}
		}
		return list.toArray(new char[list.size()][]);
	}

	public static char[][] replace(char[][] arrays, char character, char[][] replacements) {
		List<char[]> list = new ArrayList<char[]>();
		for (char[] array : arrays) {
			String string = String.valueOf(array);
			if (string.indexOf(character) >= 0) {
				for (char[] replacement : replacements) {
					list.add(string.replaceAll(String.valueOf(character), String.valueOf(replacement)).toCharArray());
				}
			} else {
				list.add(array);
			}
		}
		return list.toArray(new char[list.size()][]);
	}

	public static char[][] rsort(char[][] arrays) {
		arrays = arrays.clone();
		Arrays.sort(arrays, new Comparator<char[]>() {
			public int compare(char[] o1, char[] o2) {
				return o1.length - o2.length;
			}
		});
		return arrays;
	}

	public static boolean sequenceDetected(ICharacterScanner characterScanner, char[] sequence, boolean ignoreCase) {
		for (int i = 1; i < sequence.length; ++i) {
			int c = characterScanner.read();
			if ((ignoreCase && Character.toLowerCase(c) != Character.toLowerCase(sequence[i])) || (!ignoreCase && c != sequence[i])) {
				// Non-matching character detected, rewind the scanner back to the start.
				// Do not unread the first character.
				characterScanner.unread();
				for (int j = i - 1; j > 0; --j) {
					characterScanner.unread();
				}
				return false;
			}
		}
		for (int j = sequence.length - 1; j > 0; --j) {
			characterScanner.unread();
		}
		return true;
	}

}
