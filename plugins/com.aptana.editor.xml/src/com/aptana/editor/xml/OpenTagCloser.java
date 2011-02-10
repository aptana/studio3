/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;

@SuppressWarnings("nls")
public class OpenTagCloser implements VerifyKeyListener
{

	private ITextViewer textViewer;

	public OpenTagCloser(ITextViewer textViewer)
	{
		this.textViewer = textViewer;
	}

	public void install()
	{
		textViewer.getTextWidget().addVerifyKeyListener(this);
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

			String openTag = getOpenTag(document, offset);
			if (openTag == null || skipOpenTag(openTag))
				return;

			String closeTag = getMatchingCloseTag(openTag);
			if (closeTag == null)
				return;

			// Check to see if this tag is already closed...
			if (TagUtil.tagClosed(document, openTag))
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
			XMLPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, XMLPlugin.PLUGIN_ID, e.getMessage(), e));
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
		// FIXME What criteria do we use for XML?
		return true;
	}

	protected boolean skipOpenTag(String openTag)
	{
		return openTag == null || openTag.startsWith("<!");
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

	private String getOpenTag(IDocument document, int offset) throws BadLocationException
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
		if (isEmptyTagType(document, toCheck))
		{
			return null;
		}
		// Return a not necessarily good tag. May contain attrs and an additional ">", but we rely on that later...
		return "<" + tagName + ">";
	}

	protected boolean isEmptyTagType(IDocument doc, String tagName)
	{
		// FIXME Check DTD or whatever to see if tag is a self-closing one for this XML doc!
		return false;
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
