package com.aptana.editor.xml;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;

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
		// Count number of open tags
		while (true)
		{
			x = src.indexOf("<" + tagName, x); //$NON-NLS-1$
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
			x = src.indexOf("</" + tagName, x); //$NON-NLS-1$
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
			XMLPlugin.logError(e.getMessage(), e);
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
		List<ITypedRegion> previousPartitions = Arrays.asList(document.computePartitioning(start, length));
		if (!findClose)
		{
			// search backwards
			Collections.reverse(previousPartitions);
		}

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
			if (src.startsWith("</" + tagName + " ") || src.startsWith("</" + tagName + ">")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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
			else if (src.startsWith("<" + tagName + " ") || src.startsWith("<" + tagName + ">")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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
	private static String getTagName(String openTag)
	{
		if (openTag.startsWith("<")) //$NON-NLS-1$
		{
			openTag = openTag.substring(1);
		}
		if (openTag.startsWith("/")) //$NON-NLS-1$
		{
			openTag = openTag.substring(1);
		}
		int index = openTag.indexOf(">"); //$NON-NLS-1$
		if (index != -1)
		{
			openTag = openTag.substring(0, index);
		}
		openTag = openTag.trim();
		int spaceIndex = openTag.indexOf(' ');
		if (spaceIndex != -1)
		{
			openTag = openTag.substring(0, spaceIndex);
		}
		return openTag;
	}

}
