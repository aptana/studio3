package com.aptana.editor.html.parsing;

public class HTMLUtils
{

	/**
	 * Removes the "<" and "</" from the beginning and ">" from the end of a tag.
	 * 
	 * @param tag
	 *            the tag text to strip
	 * @return a string with the necessary items removed
	 */
	public static String stripTagEndings(String tag)
	{
		String name = tag.replaceAll("^\\s*</", ""); //$NON-NLS-1$ //$NON-NLS-2$
		name = name.replaceAll(">\\s*$", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return name.replaceAll("^\\s*<", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
