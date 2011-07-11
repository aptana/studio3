/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.nodes;

import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.IFormatterDocument;

public class AbstractFormatterNodeBuilder
{
	private final Stack<IFormatterContainerNode> stack = new Stack<IFormatterContainerNode>();
	private static final Pattern COMMENT_PREFIX = Pattern.compile("^[\\s]*(//|/\\*|#|<!--)(.|\\n|\\r)*"); //$NON-NLS-1$

	protected void start(IFormatterContainerNode root)
	{
		stack.clear();
		stack.push(root);
	}

	public IFormatterContainerNode peek()
	{
		return stack.peek();
	}

	public void push(IFormatterContainerNode node)
	{
		addChild(node);
		stack.push(node);
	}

	public IFormatterNode addChild(IFormatterNode node)
	{
		IFormatterContainerNode parentNode = peek();
		if (!node.isEmpty())
		{
			advanceParent(node, parentNode, node.getStartOffset());
		}
		parentNode.addChild(node);
		return node;
	}

	private void advanceParent(IFormatterNode node, IFormatterContainerNode parentNode, int pos)
	{
		int startOffset = parentNode.getEndOffset();
		IFormatterDocument document = parentNode.getDocument();
		String text = document.get(startOffset, pos);

		if (startOffset < pos)
		{
			// Check if the node should consume any gaps that we have to previous node end offset.
			// This way, we can consume all white-spaces in between.
			// The check take into consideration the value of the previous node's getSpacesCountAfter(), so that we do
			// not trim more then what that node was adding.
			if (node.shouldConsumePreviousWhiteSpaces())
			{
				List<IFormatterNode> children = parentNode.getChildren();
				int preservedSpaces = 0;
				if (!children.isEmpty())
				{
					preservedSpaces = children.get(children.size() - 1).getSpacesCountAfter();
				}
				if (text.trim().length() == 0 && preservedSpaces == 0)
				{
					return;
				}
				int rightPos = text.length() - 1;
				for (; rightPos > 0; rightPos--)
				{
					if (!Character.isWhitespace(text.charAt(rightPos)))
					{
						break;
					}
				}
				int newPos = Math.max(startOffset, pos - (text.length() - rightPos + preservedSpaces) + 1);
				if (newPos < pos && preservedSpaces > 0)
				{
					if (!Character.isWhitespace(document.charAt(newPos)))
					{
						newPos = pos;// revert
					}
				}
				pos = newPos;
			}

			// Skip spaces and tabs if it is a comment
			String trimmedText = text.trim();
			if (COMMENT_PREFIX.matcher(trimmedText).matches())
			{
				while ((document.charAt(startOffset) == ' ' || document.charAt(startOffset) == '\t')
						&& (startOffset < pos - 1))
				{
					startOffset++;
				}

				// For block comments, we also skip trailing white spaces

				if ((trimmedText.startsWith("/*") && trimmedText.endsWith("*/")) || (trimmedText.startsWith("<!--") && trimmedText.endsWith("-->"))) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				{
					while ((document.charAt(pos - 1) == ' ' || document.charAt(pos - 1) == '\t')
							&& (startOffset < pos - 1))
					{
						pos--;
					}
				}
			}

			parentNode.addChild(createTextNode(document, startOffset, pos));
		}
	}

	/**
	 * A utility method that locates the given char in the document, skipping any white-spaces. In case the character
	 * was not found between the given offset and the next non-white-space char, the original offset is returned.
	 * 
	 * @param document
	 *            A {@link FormatterDocument}
	 * @param startOffset
	 *            The start offset of the search
	 * @param c
	 *            The character to search for by scanning the document characters forward from the given start offset
	 *            (including the offset)
	 * @param caseSensitive
	 *            Indicate that the matching of the characters should be done in a case sensitive way or not.
	 * @return The offset of the char; The original offset is returned in case the search for the char failed.
	 */
	public static int locateCharacterSkippingWhitespaces(FormatterDocument document, int startOffset, char c,
			boolean caseSensitive)
	{
		char toCheck = (caseSensitive) ? c : Character.toLowerCase(c);
		for (int i = startOffset; i < document.getLength(); i++)
		{
			char next = document.charAt(i);
			if (!caseSensitive)
			{
				next = Character.toLowerCase(next);
			}
			if (toCheck == next)
			{
				startOffset = i;
				break;
			}
			if (Character.isWhitespace(next))
			{
				continue;
			}
			break;
		}
		return startOffset;
	}

	/**
	 * Returns the next non-white char offset. In case all the chars after the given start offset are white-spaces,
	 * return the given start offset.
	 * 
	 * @param document
	 * @param startOffset
	 * @return The next non-white char; Or the given start-offset if all the chars right to the start offset were
	 *         whitespaces.
	 */
	public int getNextNonWhiteCharOffset(FormatterDocument document, int startOffset)
	{
		int length = document.getLength();
		for (int offset = startOffset; offset < length; offset++)
		{
			char charAt = document.charAt(offset);
			if (!Character.isWhitespace(charAt))
			{
				return offset;
			}
		}
		return startOffset;
	}

	public void checkedPop(IFormatterContainerNode expected, int bodyEnd)
	{
		IFormatterContainerNode top = stack.pop();
		if (top != expected)
		{
			throw new IllegalStateException();
		}
		if (bodyEnd > 0 && expected.getEndOffset() < bodyEnd)
		{
			expected.addChild(createTextNode(expected.getDocument(), expected.getEndOffset(), bodyEnd));
		}
	}

	/**
	 * @return
	 */
	public IFormatterTextNode createTextNode(IFormatterDocument document, int startIndex, int endIndex)
	{
		return new FormatterTextNode(document, startIndex, endIndex);
	}

	/**
	 * Try to locate the given char by traversing backwards on the given document from the start offset.<br>
	 * In case no match is located, the original start offset is returned.
	 * 
	 * @param document
	 * @param c
	 * @param start
	 * @return The char offset, and if not found - the original start offset.
	 */
	public int locateCharBackward(FormatterDocument document, char c, int start)
	{
		for (int offset = start; offset >= 0; offset--)
		{
			if (document.charAt(offset) == c)
			{
				return offset;
			}
		}
		return start;
	}

	/**
	 * Try to locate the given char by traversing forward on the given document from the start offset.<br>
	 * In case no match is located, the original start offset is returned.
	 * 
	 * @param document
	 * @param c
	 * @param start
	 * @return The char offset, and if not found - the original start offset.
	 */
	public int locateCharForward(FormatterDocument document, char c, int start)
	{
		int length = document.getLength();
		for (int offset = start; offset < length; offset++)
		{
			if (document.charAt(offset) == c)
			{
				return offset;
			}
		}
		return start;
	}
}
