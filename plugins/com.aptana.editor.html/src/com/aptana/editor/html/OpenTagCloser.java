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
package com.aptana.editor.html;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;

import com.aptana.editor.html.parsing.HTMLParseState;

@SuppressWarnings("nls")
public class OpenTagCloser implements VerifyKeyListener
{

	private ITextViewer textViewer;

	public OpenTagCloser(ITextViewer textViewer)
	{
		this.textViewer = textViewer;
	}

	public static OpenTagCloser install(ITextViewer textViewer)
	{
		OpenTagCloser pairMatcher = new OpenTagCloser(textViewer);
		textViewer.getTextWidget().addVerifyKeyListener(pairMatcher);
		return pairMatcher;
	}

	public void verifyKey(VerifyEvent event)
	{
		// early pruning to slow down normal typing as little as possible
		if (!isAutoInsertEnabled() || !isAutoInsertCharacter(event.character))
		{
			return;
		}

		IDocument document = textViewer.getDocument();
		final Point selection = textViewer.getSelectedRange();
		int offset = selection.x;
		final int length = selection.y;

		try
		{
			boolean nextIsLessThan = false;
			if (offset < document.getLength())
			{
				char c = document.getChar(offset);
				if (c == '>')
				{
					nextIsLessThan = true;
					event.doit = false; // no matter what we'll auto-close or overwrite the existing '>'
					textViewer.setSelectedRange(offset + 1, 0);
				}
			}

			// give a chance to exit early
			if (!shouldAutoClose(document, offset, event))
			{
				return;
			}

			String openTag = getOpenTag(document, offset, event);
			if (openTag == null || skipOpenTag(openTag))
				return;

			String closeTag = getMatchingCloseTag(openTag);
			if (closeTag == null)
				return;

			// Check to see if this tag is already closed...
			if (tagClosed(document, openTag))
			{
				return;
			}

			final StringBuffer buffer = new StringBuffer();
			// check if the char already exists next in doc! This is the special case of when we auto-paired the '<>' in
			// linked mode...
			boolean overwrite = openTag.endsWith(">>");
			if (nextIsLessThan)
			{
				overwrite = true;
				offset++;
			}
			if (!overwrite)
			{
				buffer.append(event.character);
			}
			buffer.append(closeTag);
			document.replace(offset, length, buffer.toString());
			if (nextIsLessThan || overwrite)
			{
				// move cursor one?
				textViewer.setSelectedRange(offset, 0);
			}
			else
			{
				textViewer.setSelectedRange(offset + 1, 0);
			}
			event.doit = false;
		}
		catch (BadLocationException e)
		{
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
		}
	}

	/**
	 * Allows quick return if we happen to be in a partition where we don't want auto-closed tags. Currently will only
	 * auto-close when in HTML partitions.
	 * 
	 * @param document
	 * @param offset
	 * @param event
	 * @return
	 */
	protected boolean shouldAutoClose(IDocument document, int offset, VerifyEvent event)
	{
		if (document.getDocumentPartitioner() == null)
		{
			return true;
		}
		// Only auto-close in HTML
		ITypedRegion[] typedRegions = document.getDocumentPartitioner().computePartitioning(offset, 0);
		if (typedRegions != null && typedRegions.length > 0)
		{
			if (typedRegions[0].getType().startsWith(HTMLSourceConfiguration.PREFIX))
			{
				return true;
			}
		}
		return false;
	}

	protected boolean skipOpenTag(String openTag)
	{
		return openTag == null || openTag.startsWith("<%") || openTag.startsWith("<!");
	}

	public static boolean tagClosed(IDocument document, String openTag)
	{
		// Actually make a "stack" of open and close tags for this tag name and see if it's unbalanced
		String tagName = getTagName(openTag);

		int stack = 0;
		String src = document.get();
		int x = 0;
		int toAdd = tagName.length() + 1;
		// Count number of open tags
		while (true)
		{
			x = src.indexOf("<" + tagName, x);
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
			x = src.indexOf("</" + tagName, x);
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
	 * Assumes tag is in format <tag.name(\s*.*)>, returns tag.name
	 * 
	 * @param openTag
	 * @return
	 */
	protected static String getTagName(String openTag)
	{
		if (openTag.startsWith("<"))
		{
			openTag = openTag.substring(1);
		}
		int index = openTag.indexOf(">");
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

	private String getMatchingCloseTag(String openTag)
	{

		int index = openTag.indexOf(' ');
		if (index == -1)
		{
			index = openTag.indexOf(">");
		}
		String closeTag = "</" + openTag.substring(1, index);
		if (!closeTag.endsWith(">"))
			closeTag += ">";
		return closeTag;
	}

	private String getOpenTag(IDocument document, int offset, VerifyEvent event) throws BadLocationException
	{
		// Read current tag, see if it's self-closing or has been closed later...
		int start = offset - 1;
		boolean foundFirstChar = false;
		for (int i = offset - 1; i >= 0; i--)
		{
			char c = document.getChar(i);
			if (c == '<')
			{
				start = i;
				break;
			}
			// if last non-WS char is slash, tag is closed
			else if (!Character.isWhitespace(c) && !foundFirstChar)
			{
				if (c == '/')
					return null;
				foundFirstChar = true;
			}
		}
		start++;
		int length = offset - start;
		if (length <= 0)
			return null;
		String tagName = document.get(start, length).trim();
		// Modify tag for some tag name checks
		String toCheck = tagName;
		int spaceIndex = toCheck.indexOf(' ');
		if (spaceIndex != -1)
		{
			toCheck = toCheck.substring(0, spaceIndex);
		}
		if (toCheck.endsWith(">"))
		{
			toCheck = toCheck.substring(0, toCheck.length() - 1);
		}
		if (toCheck.startsWith("/"))
		{
			return null;
		}
		HTMLParseState state = new HTMLParseState();
		state.setEditState(document.get(), null, 0, 0);
		if (state.isEmptyTagType(toCheck))
		{
			return null;
		}
		// Return a not necessarily good tag. May contain attrs and an additional ">", but we rely on that later...
		return "<" + tagName + ">";
	}

	protected boolean isAutoInsertCharacter(char character)
	{
		return character == '>';
	}

	protected boolean isAutoInsertEnabled()
	{
		return true;
	}

	protected ITextViewer getTextViewer()
	{
		return textViewer;
	}
}
