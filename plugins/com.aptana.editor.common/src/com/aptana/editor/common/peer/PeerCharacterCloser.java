package com.aptana.editor.common.peer;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;

import com.aptana.editor.common.CommonEditorPlugin;

/**
 * A class that can be installed on a ITextViewer and will auto-insert the closing peer character for typical paired
 * characters like (), [], {}, '', "", ``, <>
 * 
 * @author cwilliams
 */
public class PeerCharacterCloser implements VerifyKeyListener
{

	private ITextViewer textViewer;

	private PeerCharacterCloser(ITextViewer textViewer)
	{
		this.textViewer = textViewer;
	}

	public static PeerCharacterCloser install(ITextViewer textViewer)
	{
		PeerCharacterCloser pairMatcher = new PeerCharacterCloser(textViewer);
		textViewer.getTextWidget().addVerifyKeyListener(pairMatcher);
		return pairMatcher;
	}

	/**
	 * @see org.eclipse.swt.custom.VerifyKeyListener#verifyKey(org.eclipse.swt.events.VerifyEvent)
	 */
	public void verifyKey(VerifyEvent event)
	{
		// early pruning to slow down normal typing as little as possible
		if (!event.doit || !isAutoInsertEnabled() || !isAutoInsertCharacter(event.character))
		{
			return;
		}

		IDocument document = textViewer.getDocument();
		final Point selection = textViewer.getSelectedRange();
		final int offset = selection.x;
		final int length = selection.y;

		try
		{
			// TODO Don't auto-close if we have an open pair!
			if (length > 0)
			{
				wrapSelection(event, document, offset, length);
				return;
			}

			final char closingCharacter = getPeerCharacter(event.character);
			// Check if the next character in source is the closing character (and don't close if it is)!
			if (offset < document.getLength())
			{
				String restOfDoc = document.get(offset, document.getLength() - offset).trim();
				if (restOfDoc.length() > 0 && restOfDoc.charAt(0) == closingCharacter)
				{
					return;
				}
			}
			final StringBuffer buffer = new StringBuffer();
			buffer.append(closingCharacter);
			if (offset == document.getLength())
			{
				String delim = null;
				if (document instanceof IDocumentExtension4)
				{
					delim = ((IDocumentExtension4) document).getDefaultLineDelimiter();
				}
				if (delim == null)
				{
					delim = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				buffer.append(delim);
			}

			document.replace(offset, length, buffer.toString());
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	private void wrapSelection(VerifyEvent event, IDocument document, final int offset, final int length)
			throws BadLocationException
	{
		final char closingCharacter = getPeerCharacter(event.character);
		final StringBuffer buffer = new StringBuffer();
		buffer.append(event.character);
		buffer.append(document.get(offset, length));
		buffer.append(closingCharacter);
		document.replace(offset, length, buffer.toString());
		event.doit = false;
	}

	private char getPeerCharacter(char character)
	{
		switch (character)
		{
			case '[':
				return ']';
			case '(':
				return ')';
			case '{':
				return '}';
			case '<':
				return '>';
			default:
				return character;
		}
	}

	private boolean isAutoInsertCharacter(char character)
	{
		switch (character)
		{
			case '[':
			case '(':
			case '{':
			case '\'':
			case '"':
			case '<':
			case '`':
				return true;
			default:
				return false;
		}
	}

	private boolean isAutoInsertEnabled()
	{
		// TODO Set up a pref to turn this on or off
		return true;
	}
}
