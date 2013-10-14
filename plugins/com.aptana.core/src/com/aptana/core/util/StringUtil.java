/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;
import com.aptana.core.util.replace.SimpleTextPatternReplacer;

public class StringUtil
{

	/**
	 * EMPTY
	 */
	public static final String EMPTY = ""; //$NON-NLS-1$

	/**
	 * regexp for counting lines and splitting strings by lines.
	 */
	public static final String LINE_SPLITTER_REGEX = "\r?\n|\r"; //$NON-NLS-1$

	/**
	 * regex for newline group
	 */
	public static final String REGEX_NEWLINE_GROUP = MessageFormat.format("({0})", StringUtil.LINE_SPLITTER_REGEX); //$NON-NLS-1$

	/**
	 * Pattern for counting lines and splitting strings by lines.
	 */
	public static final Pattern LINE_SPLITTER = Pattern.compile(LINE_SPLITTER_REGEX);

	/**
	 * TextPatternReplacer to sanitize html/xml to entities.
	 */
	private static final SimpleTextPatternReplacer ENTITY_SANITIZER;
	static
	{
		ENTITY_SANITIZER = new SimpleTextPatternReplacer();
		ENTITY_SANITIZER.addPattern("&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
		ENTITY_SANITIZER.addPattern("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
		ENTITY_SANITIZER.addPattern(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	private static final Pattern HTML_TAG_PATTERN = Pattern.compile("\\<.*?\\>"); //$NON-NLS-1$

	/**
	 * Compares two strings for equality taking into account that none, one, or both may be null
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 * @deprecated Use the more generic {@link ObjectUtil#areEqual(Object, Object)} in place of this method
	 */
	public static boolean areEqual(String s1, String s2)
	{
		return ObjectUtil.areEqual(s1, s2);
	}

	/**
	 * Compares two strings for equality taking into account that none, one, or both may be null
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 * @deprecated Use the more generic {@link ObjectUtil#areNotEqual(Object, Object)} in place of this method
	 */
	public static boolean areNotEqual(String s1, String s2)
	{
		return ObjectUtil.areNotEqual(s1, s2);
	}

	/**
	 * characterInstanceCount
	 * 
	 * @param source
	 * @param c
	 * @return
	 */
	public static int characterInstanceCount(String source, char c)
	{
		int result = -1;

		if (source != null)
		{
			int length = source.length();

			result = 0;

			for (int i = 0; i < length; i++)
			{
				if (source.charAt(i) == c)
				{
					result++;
				}
			}
		}

		return result;
	}

	/**
	 * Compares two strings for ordering taking into account that none, one, or both may be null. We respect case
	 * differences here
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int compare(String s1, String s2)
	{
		s1 = getStringValue(s1);
		s2 = getStringValue(s2);

		return s1.compareTo(s2);
	}

	/**
	 * Compares two strings for ordering taking into account that none, one, or both may be null. We ignore case
	 * differences here
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int compareCaseInsensitive(String s1, String s2)
	{
		s1 = getStringValue(s1);
		s2 = getStringValue(s2);

		return s1.compareToIgnoreCase(s2);
	}

	/**
	 * Concatenate a collection of strings
	 * 
	 * @param items
	 *            The collection of strings to concatenate
	 * @return
	 */
	public static String concat(Collection<String> items)
	{
		return (items != null) ? join(null, items.toArray(new String[items.size()])) : null;
	}

	/**
	 * Concatenate a list of strings.
	 * 
	 * @param items
	 *            The list of strings to concatenate
	 * @return
	 */
	public static String concat(String... items)
	{
		return join(null, items);
	}

	/**
	 * Determine if a specified string exists in an array of strings
	 * 
	 * @param set
	 *            The array of strings to test against
	 * @param toFind
	 *            The string to search for in the array of strings
	 * @return
	 */
	public static boolean contains(String[] set, String toFind)
	{
		if (set != null && toFind != null)
		{
			for (String value : set)
			{
				if (value.equals(toFind))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Adds an ellipsis to the end of a string, generally indicating that this string leads to another choice (like a
	 * dialog)
	 * 
	 * @param message
	 * @return The ellipsif-ied string
	 */
	public static String ellipsify(String message)
	{
		return message == null ? null : message + "..."; //$NON-NLS-1$
	}

	/**
	 * Walk forwards through the string to find the next whitespace character. If the current char is a whitespace char,
	 * return the current offset
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	public static int findNextWhitespaceOffset(String string, int offset)
	{
		int i = -1;

		if (string == null || EMPTY.equals(string) || offset > string.length())
			return i;

		int j = offset;
		while (j < string.length())
		{
			char ch = string.charAt(j);
			if (Character.isWhitespace(ch))
			{
				i = j;
				break;
			}
			j++;
		}
		return i;
	}

	/**
	 * Walk backwards through the string to find the previous whitespace character.
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	public static int findPreviousWhitespaceOffset(String string, int offset)
	{
		int i = -1;

		if (string == null || EMPTY.equals(string) || offset > string.length())
			return i;

		int j = offset;
		while (j > 0)
		{
			char ch = string.charAt(j - 1);
			if (Character.isWhitespace(ch))
			{
				i = j - 1;
				break;
			}
			j--;
		}
		return i;
	}

	/**
	 * For a null Object, returns {@value #EMPTY}, otherwise returns {@link Object#toString()}
	 * 
	 * @param object
	 * @return
	 */
	public static String getStringValue(Object object)
	{
		return (object != null) ? object.toString() : EMPTY;
	}

	/**
	 * Determines if a string is empty, which we define as null, or having nothing but whitespace as content.
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isEmpty(String text)
	{
		return text == null || text.trim().length() == 0;
	}

	/**
	 * Create a string by concatenating the elements of a collection using a delimiter between each item
	 * 
	 * @param delimiter
	 *            The text to place between each element in the array
	 * @param items
	 *            The collection of items to join
	 * @return The resulting string
	 */
	public static String join(String delimiter, Collection<String> items)
	{
		return (items != null) ? join(delimiter, items.toArray(new String[items.size()])) : null;
	}

	/**
	 * Create a string by concatenating the elements of a collection using a delimiter between each item
	 * 
	 * @param delimiter
	 *            The text to place between each element in the array
	 * @param items
	 *            The array of items
	 * @return The resulting string
	 */
	public static String join(String delimiter, Object... items)
	{
		String[] s = new String[items.length];
		for (int i = 0; i < items.length; i++)
		{
			Object item = items[i];
			if (item == null)
			{
				s[i] = "null"; //$NON-NLS-1$

			}
			else
			{
				s[i] = item.toString();
			}
		}
		return join(delimiter, s);
	}

	/**
	 * Create a string by concatenating the elements of a collection using a delimiter between each item
	 * 
	 * @param delimiter
	 *            The text to place between each element in the array
	 * @param items
	 *            The array of chars
	 * @return The resulting string
	 */
	public static String join(String delimiter, char... items)
	{
		String[] strings = new String[items.length];
		for (int i = 0; i < items.length; i++)
		{
			strings[i] = new String(items, i, 1);
		}
		return join(delimiter, strings);
	}

	/**
	 * Create a string by concatenating the elements of a string array using a delimiter between each item
	 * 
	 * @param delimiter
	 *            The text to place between each element in the array
	 * @param items
	 *            The array of items to join
	 * @return The resulting string
	 */
	public static String join(String delimiter, String... items)
	{
		String result = null;

		if (items != null)
		{
			switch (items.length)
			{
				case 0:
				{
					result = EMPTY;
					break;
				}

				case 1:
				{
					result = items[0];
					break;
				}

				// NOTE: consider adding additional cases here, probably for at least 2, by unrolling the loop from the
				// default section below

				default:
				{
					int lastIndex = items.length - 1;

					// determine length of the delimiter
					int delimiterLength = (delimiter != null) ? delimiter.length() : 0;

					// determine the length of the resulting string, starting with the length of all delimiters
					int targetLength = (lastIndex) * delimiterLength;

					// now add in the length of each item in our list of items
					for (int i = 0; i <= lastIndex; i++)
					{
						targetLength += items[i].length();
					}

					// build the resulting character array
					int offset = 0;
					char[] accumulator = new char[targetLength];

					// NOTE: We test for delimiter length here to avoid having a conditional within the for-loops in the
					// true/false blocks. Moving the conditional inside the for-loop barely improved the performance of
					// this implementation from the StringBuilder version we had before
					if (delimiterLength != 0)
					{
						// copy all items (except last) and all delimiters
						for (int i = 0; i < lastIndex; i++)
						{
							String item = items[i];

							// cache current item's length
							int length = item.length();

							// copy the item into the accumulator
							item.getChars(0, length, accumulator, offset);
							offset += length;

							// copy in the delimiter
							delimiter.getChars(0, delimiterLength, accumulator, offset);
							offset += delimiterLength;
						}

						String item = items[lastIndex];
						item.getChars(0, item.length(), accumulator, offset);
					}
					else
					{
						// NOTE: use classic iteration to avoid the overhead of an iterator
						for (int i = 0; i <= lastIndex; i++)
						{
							String item = items[i];

							// cache current item's length
							int length = item.length();

							// copy the item into the accumulator
							item.getChars(0, length, accumulator, offset);
							offset += length;
						}
					}

					// convert the result to a String and return that value
					result = new String(accumulator);
				}
			}
		}

		return result;
	}

	/**
	 * Adds a colon to the end of the string, as if making a form label
	 * 
	 * @param message
	 * @return string + colon
	 */
	public static String makeFormLabel(String message)
	{
		return message == null ? null : message + ":"; //$NON-NLS-1$
	}

	/**
	 * Generate an MD5 hash of a string.
	 * 
	 * @param lowerCase
	 * @return null if an exception occurs.
	 */
	public static String md5(String lowerCase)
	{
		if (lowerCase == null)
		{
			return null;
		}
		try
		{
			byte[] bytesOfMessage = lowerCase.getBytes(IOUtil.UTF_8);
			MessageDigest md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
			byte[] thedigest = md.digest(bytesOfMessage);
			BigInteger bigInt = new BigInteger(1, thedigest);
			String hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			return pad(hashtext, 32, '0');
		}
		catch (Exception e)
		{
			CorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, e.getMessage(), e));
		}
		return null;
	}

	/**
	 * Pads the beginning of a string with a given character.
	 * 
	 * @param string
	 * @param desiredLength
	 * @param padChar
	 * @return
	 */
	public static String pad(String string, int desiredLength, char padChar)
	{
		if (string == null)
		{
			string = EMPTY;
		}

		int diff = desiredLength - string.length();
		if (diff > 0)
		{
			string = repeat(padChar, diff) + string;
		}
		return string;
	}

	/**
	 * Add single quotes around the given string.
	 * 
	 * @param string
	 * @return
	 */
	public static String quote(String string)
	{
		return string == null ? null : '\'' + string + '\'';
	}

	/**
	 * Replace one string with another
	 * 
	 * @param str
	 * @param pattern
	 * @param replace
	 * @return String
	 */
	public static String replace(String str, String pattern, String replace)
	{
		if (str == null)
		{
			return null;
		}

		int s = 0;
		int e = 0;
		StringBuilder result = new StringBuilder();
		while ((e = str.indexOf(pattern, s)) >= 0)
		{
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}
		result.append(str.substring(s));

		return result.toString();
	}

	/**
	 * Given a raw input string template, this will do a mass search and replace for the map of variables to values.
	 * Acts like {@link String#replaceAll(String, String)}
	 * 
	 * @param template
	 * @param variables
	 * @return
	 */
	public static String replaceAll(String template, Map<String, String> variables)
	{
		if (template == null || variables == null || variables.isEmpty())
		{
			return template;
		}
		for (Map.Entry<String, String> entry : variables.entrySet())
		{
			String value = entry.getValue();
			if (value == null)
			{
				value = EMPTY;
			}
			else
			{
				value = value.replace('$', (char) 1); // To avoid illegal group reference issues if the text has
														// dollars!
			}
			template = template.replaceAll(entry.getKey(), value).replace((char) 1, '$');
		}
		return template;
	}

/**
		 * Sanitizes raw HTML to escape '&', '<' and '>' so that it is suitable for embedding into HTML.
		 * 
		 * @param raw
		 * @return
		 */
	public static String sanitizeHTML(String raw)
	{
		return ENTITY_SANITIZER.searchAndReplace(raw);
	}

	/**
	 * Does the string start with the specific char
	 * 
	 * @param string
	 *            the string to test
	 * @param c
	 *            the char to test
	 * @return true if yes, false if the no, or the string is empty or null
	 */
	public static boolean startsWith(String string, char c)
	{
		return !StringUtil.isEmpty(string) && string.charAt(0) == c;
	}

	public static boolean startsWith(String string, String prefix)
	{
		return !StringUtil.isEmpty(string) && string.startsWith(prefix);
	}

	/**
	 * Removes <.*?> inside a string. If the specified value is empty or null, then it is returned untouched
	 * 
	 * @param textWithHTML
	 * @return
	 */
	public static String stripHTMLTags(String textWithHTML)
	{
		// @formatter:off
		return (!StringUtil.isEmpty(textWithHTML))
			?	HTML_TAG_PATTERN.matcher(textWithHTML).replaceAll(EMPTY)
			:	textWithHTML;
		// @formatter:on
	}

	/**
	 * Runs the input through a StringTokenizer and gathers up all the tokens.
	 * 
	 * @param inputString
	 * @param delim
	 * @return
	 */
	public static List<String> tokenize(String inputString, String delim)
	{
		List<String> tokens = new ArrayList<String>();
		if (inputString == null)
			return tokens;
		StringTokenizer tokenizer = new StringTokenizer(inputString, delim);
		while (tokenizer.hasMoreTokens())
			tokens.add(tokenizer.nextToken());
		return tokens;
	}

	/**
	 * Truncates the string to a particular length if it's longer and appends ... in the end. So the string will
	 * actually be length + 3
	 * 
	 * @param text
	 *            the string to be truncated
	 * @param length
	 *            the length to truncate to
	 * @return the truncated string
	 */
	public static String truncate(String text, int length)
	{
		if (text == null || text.length() <= length)
		{
			return text;
		}
		return new String(ellipsify(text.substring(0, length)));
	}

	/**
	 * A smarter truncation algorithm. The max length is truly enforced, meaning if we do truncate we always return a
	 * string of length maxLength including the ellipses.
	 * 
	 * @param text
	 * @param length
	 * @return
	 */
	public static String smartTruncate(String text, int maxLength)
	{
		if (text == null || text.length() <= maxLength)
		{
			return text;
		}
		return new String(ellipsify(text.substring(0, maxLength - 3)));
	}

	/**
	 * Repeats the given char n times and returns it as a new string.
	 */
	public static String repeat(char c, int times)
	{
		char[] buf = new char[times];
		for (int i = 0; i < times; i++)
		{
			buf[i] = c;
		}
		return new String(buf);
	}

	private StringUtil()
	{
	}

	/**
	 * Splits some string given some char (that char will not appear in the returned strings) Empty strings are also
	 * never added.
	 * 
	 * @note: Returned strings will be a substring of the original string (and thus will keep its full internal array
	 *        alive).
	 */
	public static List<String> split(String string, char toSplit)
	{
		ArrayList<String> ret = new ArrayList<String>();
		int len = string.length();
		int last = 0;
		char c = 0;

		for (int i = 0; i < len; i++)
		{
			c = string.charAt(i);
			if (c == toSplit)
			{
				if (last != i)
				{
					ret.add(string.substring(last, i));
				}
				while (c == toSplit && i < len - 1)
				{
					i++;
					c = string.charAt(i);
				}
				last = i;
			}
		}
		if (c != toSplit)
		{
			if (last == 0 && len > 0)
			{
				ret.add(string); // it is equal to the original (no char to split)

			}
			else if (last < len)
			{
				ret.add(string.substring(last, len));

			}
		}
		ret.trimToSize(); // Save some memory.
		return ret;
	}

	public static List<String> dotSplit(String string)
	{
		return split(string, '.');
	}

	public static String dotFirst(String string)
	{
		int i = string.indexOf('.');
		return (i != -1 ? string.substring(0, i) : string);
	}

	/**
	 * Strips leading and trailing single/double quotes from a string. Assumes there is a trailing quote if there is a
	 * leading quote.
	 * 
	 * @param text
	 * @return
	 */
	public static String stripQuotes(String text)
	{
		if (text.length() >= 2 && (text.charAt(0) == '\'' || text.charAt(0) == '"'))
		{
			return text.substring(1, text.length() - 1);
		}
		return text;
	}

	/**
	 * This is the equivalent of {@link String#indexOf(int)} but for searching for one of many characters (not a
	 * substring).
	 * 
	 * @param string
	 *            the string to search
	 * @param chars
	 *            The set of characters we're looking for. If we find any of these characters we stop and return the
	 *            index.
	 * @return
	 */
	public static int indexOf(String string, char... chars)
	{
		return indexOf(string, 0, chars);
	}

	public static int indexOf(String string, int offset, char... chars)
	{
		if (chars == null || chars.length == 0)
		{
			return -1;
		}
		int length = string.length();
		if (length == 0)
		{
			return -1;
		}

		if (offset < 0)
		{
			offset = 0;
		}

		for (int i = offset; i < length; i++)
		{
			char c = string.charAt(i);
			for (char x : chars)
			{
				if (c == x)
				{
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * @see String#lastIndexOf(int)
	 * @param string
	 * @param chars
	 * @return
	 */
	public static int lastIndexOf(String string, char... chars)
	{
		if (string == null)
		{
			return -1;
		}
		return lastIndexOf(string, string.length() - 1, chars);
	}

	/**
	 * @see String#lastIndexOf(String, int)
	 * @param string
	 * @param fromIndex
	 * @param chars
	 * @return
	 */
	public static int lastIndexOf(String string, int fromIndex, char... chars)
	{
		if (chars == null || chars.length == 0)
		{
			return -1;
		}
		int length = string.length();
		if (length == 0)
		{
			return -1;
		}
		else if (fromIndex >= length)
		{
			fromIndex = length - 1;
		}

		for (int i = fromIndex; i >= 0; i--)
		{
			char c = string.charAt(i);
			for (char x : chars)
			{
				if (c == x)
				{
					return i;
				}
			}
		}
		return -1;
	}
}
