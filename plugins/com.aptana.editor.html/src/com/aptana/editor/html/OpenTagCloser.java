package com.aptana.editor.html;

import java.util.HashSet;
import java.util.Set;

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

	private static Set<String> SELF_CLOSING_TAGS = new HashSet<String>();
	static
	{
		SELF_CLOSING_TAGS.add("br");
		SELF_CLOSING_TAGS.add("hr");
		SELF_CLOSING_TAGS.add("area");
		SELF_CLOSING_TAGS.add("base");
		SELF_CLOSING_TAGS.add("basefont");
		SELF_CLOSING_TAGS.add("input");
		SELF_CLOSING_TAGS.add("img");
		SELF_CLOSING_TAGS.add("link");
		SELF_CLOSING_TAGS.add("meta");
	}

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

	@Override
	public void verifyKey(VerifyEvent event)
	{
		// early pruning to slow down normal typing as little as possible
		if (!isAutoInsertEnabled() || !isAutoInsertCharacter(event.character))
		{
			return;
		}

		IDocument document = textViewer.getDocument();
		final Point selection = textViewer.getSelectedRange();
		final int offset = selection.x;
		final int length = selection.y;

		try
		{
			if (offset < document.getLength())
			{
				char c = document.getChar(offset);
				if (c == '>')
					return;
			}
			String openTag = isUnclosedOpenTag(document, offset, event);
			if (openTag == null)
				return;

			String closeTag = getMatchingCloseTag(openTag);
			if (closeTag == null)
				return;

			// Read forward to see if the next non-WS is the close tag for this, and if so do nothing!
			if (tagClosed(document, offset, openTag, closeTag))
				return;

			final StringBuffer buffer = new StringBuffer();
			// check if the char already exists next in doc! This is the special case of when we auto-paired the '<>' in
			// linked mode...
			if (!openTag.endsWith(">>"))
			{
				buffer.append(event.character);
			}
			buffer.append(closeTag);
			document.replace(offset, length, buffer.toString());
			event.doit = false;
		}
		catch (BadLocationException e)
		{
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
		}
	}

	private boolean tagClosed(IDocument document, int offset, String openTag, String closeTag)
			throws BadLocationException
	{
		// Actually make a "stack" of open and close tags for this tag name and see if it's unbalanced
		String tagName = openTag.substring(1, openTag.indexOf(">"));

		int stack = 0;
		String src = document.get();
		int x = 0;
		while (true)
		{
			x = src.indexOf("<" + tagName, x);
			if (x == -1)
				break;
			x += tagName.length() + 1;
			stack++;
		}

		x = 0;
		while (true)
		{
			x = src.indexOf("</" + tagName, x);
			if (x == -1)
				break;
			x += tagName.length() + 2;
			stack--;
		}

		return stack <= 0;
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

	private String isUnclosedOpenTag(IDocument document, int offset, VerifyEvent event) throws BadLocationException
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
		int spaceIndex = tagName.indexOf(' ');
		if (spaceIndex != -1)
		{
			tagName = tagName.substring(0, spaceIndex);
		}
		String toCheck = tagName;
		if (toCheck.endsWith(">"))
		{
			toCheck = toCheck.substring(0, toCheck.length() - 1);
		}
		if (toCheck.startsWith("/") || SELF_CLOSING_TAGS.contains(toCheck))
		{
			return null;
		}
		return "<" + tagName + ">";
	}

	private boolean isAutoInsertCharacter(char character)
	{
		return character == '>';
	}

	private boolean isAutoInsertEnabled()
	{
		// TODO Auto-generated method stub
		return true;
	}
}
