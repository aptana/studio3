/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.editor.xml.TagUtil;
import com.aptana.parsing.lexer.Lexeme;

public class HTMLTagUtil
{
	/**
	 * Is the current Lexeme a HTML tag
	 * 
	 * @param lexeme
	 * @return
	 */
	public static boolean isTag(Lexeme<HTMLTokenType> lexeme)
	{
		if (lexeme != null)
		{
			HTMLTokenType type = lexeme.getType();
			return HTMLTokenType.STRUCTURE_TAG.equals(type) || HTMLTokenType.BLOCK_TAG.equals(type)
					|| HTMLTokenType.INLINE_TAG.equals(type);
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns a list of the currently unclosed tag names in the document, ordered by most-recently-opened first
	 * 
	 * @param document
	 *            The current document
	 * @param state
	 *            The HTML parse state used to compute how we interpret closing tag rules
	 * @param offset
	 *            The offset of the cursor in the document
	 * @return
	 */
	public static List<String> getUnclosedTagNames(IDocument document, int offset)
	{
		HTMLParseState state = new HTMLParseState(document.get());

		List<String> unclosedElements = new ArrayList<String>();
		try
		{
			ITypedRegion[] partitions = document.computePartitioning(0, offset);

			// see if previous tag is closed tag. If not, then abort, as we can't have overlapping tag sets
			if (partitions.length > 1)
			{
				ITypedRegion previousPartition = partitions[partitions.length - 2];
				String src = document.get(previousPartition.getOffset(), previousPartition.getLength());
				String tagName = TagUtil.getTagName(src);

				if (!StringUtil.EMPTY.equals(tagName))
				{
					if (TagUtil.isStartTag(src) && !state.isEmptyTagType(tagName))
					{
						if (TagUtil.tagClosed(document, tagName))
						{
							return unclosedElements;
						}
					}
				}
			}

			for (ITypedRegion partition : partitions)
			{
				if (partition != null && HTMLSourceConfiguration.HTML_TAG.equals(partition.getType()))
				{
					String src = document.get(partition.getOffset(), partition.getLength());
					int lessThanIndex = src.indexOf('<');

					// if '<' index outside current string, skip this partition
					if (lessThanIndex == -1 || lessThanIndex >= src.length() - 1)
					{
						continue;
					}

					// ignore tag containing offset, i.e. if cursor is at '|', <|a>, <a|> will not
					// include <a> as unclosed tag, but <a>| will.
					int greaterThanIndex = src.indexOf('>');
					if (greaterThanIndex == -1)
					{
						continue;
					}

					// get name of element and see if we are closed elsewhere in the document
					String tagName = TagUtil.getTagName(src);
					tagName = tagName.toLowerCase();
					if (!unclosedElements.contains(tagName) && !state.isEmptyTagType(tagName)
							&& !TagUtil.tagClosed(document, tagName))
					{
						unclosedElements.add(tagName);
					}
				}
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}

		// reverse order for suggesting items
		Collections.reverse(unclosedElements);

		return unclosedElements;
	}
}
