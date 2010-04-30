package com.aptana.core;

public class StringUtils
{

	/**
	 * Create a string by concatenating the elements of a string array using a delimited between each item
	 * 
	 * @param delimiter
	 *            The text to place between each element in the array
	 * @param items
	 *            The array of items to join
	 * @return The resulting string
	 */
	public static String join(String delimiter, String[] items)
	{
		if (items == null)
		{
			return null;
		}

		int length = items.length;
		String result = ""; //$NON-NLS-1$
		if (length > 0)
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < length - 1; i++)
			{
				sb.append(items[i]).append(delimiter);
			}
			sb.append(items[length - 1]);

			result = sb.toString();
		}
		return result;
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
}
