/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;

import com.aptana.core.logging.IdeLog;

/**
 * Utilities for dealing with tags.
 * 
 * @author Ingo Muschenetz
 */
public class TagUtil
{

	public static boolean tagClosed(IDocument document, String openTag)
	{
		// TODO use findMatchingClose(document, region)?
		// Actually make a "stack" of open and close tags for this tag name and see if it's unbalanced
		String tagName = getTagName(openTag);

		int stack = 0;
		String src = document.get();
		int x = 0;
		int toAdd = tagName.length() + 1;
		String openTagPrefix = "<" + tagName; //$NON-NLS-1$
		String closeTagPrefix = "</" + tagName; //$NON-NLS-1$
		// Count number of open tags
		while (true)
		{
			x = src.indexOf(openTagPrefix, x);
			if (x == -1)
				break;
			x += toAdd;
			char c = '>';
			if (x < src.length())
			{
				c = src.charAt(x);
			}
			if (c == '>' || Character.isWhitespace(c))
			{
				stack++;
			}
		}

		// Subtract number of close tags
		x = 0;
		toAdd = tagName.length() + 2;
		while (true)
		{
			x = src.indexOf(closeTagPrefix, x);
			if (x == -1)
				break;
			x += toAdd;
			char c = '>';
			if (x < src.length())
			{
				c = src.charAt(x);
			}
			if (c == '>' || Character.isWhitespace(c))
			{
				stack--;
			}
		}
		// if we had more equal number of closed (or more than open), then the tag is closed.
		return stack <= 0;
	}

	/**
	 * Returns null when no match found! Assumes the document has been partitioned via HTML partition scanner
	 * 
	 * @param document
	 * @param offset
	 * @param partitionsToSearch
	 *            A collection of the string partition names to search for tags. Optimization to avoid looking in non
	 *            HTML/XML tags!
	 * @return
	 */
	public static IRegion findMatchingTag(IDocument document, int offset, Collection<String> partitionsToSearch)
	{
		try
		{
			ITypedRegion region = document.getPartition(offset);
			if (!partitionsToSearch.contains(region.getType()))
			{
				return null;
			}
			String src = document.get(region.getOffset(), region.getLength());
			if (src.startsWith("</")) //$NON-NLS-1$
			{
				return findMatchingOpen(document, region, partitionsToSearch);
			}
			// Handle self-closing tags!
			if (src.endsWith("/>")) //$NON-NLS-1$
			{
				return null;
			}
			return findMatchingClose(document, region, partitionsToSearch);
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(XMLPlugin.getDefault(), e);
		}
		return null;
	}

	private static IRegion findMatchingClose(IDocument document, ITypedRegion region,
			Collection<String> partitionsToSearch) throws BadLocationException
	{
		return findMatch(document, region, true, partitionsToSearch);
	}

	private static IRegion findMatchingOpen(IDocument document, ITypedRegion region,
			Collection<String> partitionsToSearch) throws BadLocationException
	{
		return findMatch(document, region, false, partitionsToSearch);
	}

	private static IRegion findMatch(IDocument document, ITypedRegion region, boolean findClose,
			Collection<String> partitionsToSearch) throws BadLocationException
	{
		String tagSrc = document.get(region.getOffset(), region.getLength());
		if (tagSrc == null)
		{
			return null;
		}

		String tagName = getTagName(tagSrc);
		int start;
		int length;
		if (findClose)
		{
			// search forwards
			start = region.getOffset() + region.getLength();
			length = document.getLength() - start;
		}
		else
		{
			// search backwards
			start = 0;
			length = region.getOffset() - 1;
		}

		if (length < 0)
		{
			return null;
		}

		List<ITypedRegion> previousPartitions = Arrays.asList(document.computePartitioning(start, length));
		if (!findClose)
		{
			// search backwards
			Collections.reverse(previousPartitions);
		}

		final String closeTag = "</" + tagName + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		final String closeTagWithSpace = "</" + tagName + " "; //$NON-NLS-1$ //$NON-NLS-2$

		final String openTag = "<" + tagName + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		final String openTagWithSpace = "<" + tagName + " "; //$NON-NLS-1$ //$NON-NLS-2$

		// Actually make a "stack" of open and close tags for this tag name and see if it's
		// unbalanced
		int stack = 1;
		for (ITypedRegion pp : previousPartitions)
		{
			if (!partitionsToSearch.contains(pp.getType()))
			{
				continue;
			}
			String src = document.get(pp.getOffset(), pp.getLength());
			if (src.startsWith(closeTagWithSpace) || src.startsWith(closeTag))
			{
				// close!
				if (findClose)
				{
					stack--;
				}
				else
				{
					stack++;
				}
			}
			else if (src.startsWith(openTagWithSpace) || src.startsWith(openTag))
			{
				// open!
				if (findClose)
				{
					stack++;
				}
				else
				{
					stack--;
				}
			}
			if (stack == 0)
			{
				return pp;
			}
		}

		return null;
	}

	/**
	 * Assumes tag is in format <tag.name(\s*.*)>, returns tag.name
	 * 
	 * @param openTag
	 * @return
	 */
	public static String getTagName(String openTag)
	{
		String result = openTag;
		if (result.length() > 0 && result.charAt(0) == '<')
		{
			result = result.substring(1);
		}
		if (result.length() > 0 && result.charAt(0) == '/')
		{
			result = result.substring(1);
		}
		int index = result.indexOf('>');
		if (index != -1)
		{
			result = result.substring(0, index);
		}
		result = result.trim();
		int spaceIndex = result.indexOf(' ');
		if (spaceIndex != -1)
		{
			result = result.substring(0, spaceIndex);
		}
		return result;
	}

	/**
	 * isStartTag
	 * 
	 * @param source
	 * @return boolean
	 */
	public static boolean isStartTag(String source)
	{
		return (source.length() > 0 && source.charAt(0) == '<') && !isEndTag(source);
	}

	/**
	 * isEndTag
	 * 
	 * @param lexeme
	 * @return boolean
	 */
	public static boolean isEndTag(String source)
	{
		// is source ending with /> also an end tag?
		return source.startsWith("</"); //$NON-NLS-1$ 
	}
}
