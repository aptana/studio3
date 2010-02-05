package com.aptana.editor.common.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonSourceViewerConfiguration;

public abstract class AbstractRegexpAutoIndentStrategy extends CommonAutoIndentStrategy
{

	private static final String SPACE_CHAR = " "; //$NON-NLS-1$
	private static final String TAB_CHAR = "\t"; //$NON-NLS-1$

	private Pattern increaseIndentRegexp;
	private Pattern decreaseIndentRegexp;

	public AbstractRegexpAutoIndentStrategy(String regexp, String decreaseRegexp, String contentType,
			SourceViewerConfiguration configuration, ISourceViewer sourceViewer)
	{
		super(contentType, configuration, sourceViewer);
		this.increaseIndentRegexp = Pattern.compile(regexp);
		if (decreaseRegexp != null)
			this.decreaseIndentRegexp = Pattern.compile(decreaseRegexp);
	}

	public AbstractRegexpAutoIndentStrategy(String regexp, String contentType, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
	{
		this(regexp, null, contentType, configuration, sourceViewer);
	}

	/**
	 * @param d
	 *            the document to work on
	 * @param c
	 *            the command to deal with
	 * @return true if the indentation occurred, false otherwise
	 */
	protected boolean autoIndent(IDocument d, DocumentCommand c)
	{
		if (c.offset <= 0 || d.getLength() == 0)
			return false;

		String newline = c.text;
		String indentString = TAB_CHAR;
		if (getSourceViewerConfiguration() instanceof CommonSourceViewerConfiguration)
		{
			indentString = ((CommonSourceViewerConfiguration) getSourceViewerConfiguration()).getIndent();
		}

		try
		{
			// Get the line and run a regexp check against it
			IRegion region = d.getLineInformationOfOffset(c.offset);
			String lineContent = d.get(region.getOffset(), c.offset - region.getOffset());

			if (increaseIndentRegexp.matcher(lineContent).find())
			{
				String previousLineIndent = getAutoIndentAfterNewLine(d, c);
				String restOfLine = d.get(c.offset, region.getLength() - (c.offset - region.getOffset()));
				String startIndent = newline + previousLineIndent + indentString;
				if (indentAndPushTrailingContentAfterNewlineAndCursor(lineContent, restOfLine))
				{
					c.text = startIndent + newline + previousLineIndent;
				}
				else
				{
					c.text = startIndent;
				}
				c.shiftsCaret = false;
				c.caretOffset = c.offset + startIndent.length();
				return true;
			}
			else if (decreaseIndentRegexp != null && decreaseIndentRegexp.matcher(lineContent).find())
			{
				int lineNumber = d.getLineOfOffset(c.offset);
				if (lineNumber == 0) // first line, should be no indent yet...
				{
					return true;
				}
				// Grab previous line's indent level
				IRegion previousLine = d.getLineInformation(lineNumber - 1);
				int endIndex = findEndOfWhiteSpace(d, previousLine.getOffset(), previousLine.getOffset()
						+ previousLine.getLength());
				String previousLineIndent = d.get(previousLine.getOffset(), endIndex - previousLine.getOffset());
				// Try to generate a string for a decreased indent level...
				String decreasedIndent = previousLineIndent;
				if (previousLineIndent.contains(TAB_CHAR))
				{
					if (previousLineIndent.contains(SPACE_CHAR))
					{
						// mixed tabs and spaces
						if (previousLineIndent.endsWith(TAB_CHAR))
						{
							// Just remove the tab at end
							decreasedIndent = decreasedIndent.substring(0, decreasedIndent.length() - 1);
						}
						else
						{
							// TODO We need to try and remove upto tab-width spaces from end, stop if we hit a tab first
						}
					}
					else
					{
						// all tabs, just remove a tab
						decreasedIndent = decreasedIndent.substring(0, decreasedIndent.length() - 1);
					}
				}
				else
				{
					// all spaces? (FIXME check if empty before declaring so, We may need to try multiple lines to see)
					int tabWidth = guessTabWidth(d, lineNumber);
					decreasedIndent = decreasedIndent.substring(0, decreasedIndent.length() - tabWidth);
				}
				// Shift the current line...
				int i = 0;
				while (Character.isWhitespace(lineContent.charAt(i)))
				{
					i++;
				}
				String newContent = decreasedIndent + lineContent.substring(i);
				d.replace(region.getOffset(), region.getLength(), newContent);
				// Set the new indent level for next line
				c.text = newline + decreasedIndent;
				c.offset -= previousLineIndent.length() - decreasedIndent.length();
				c.shiftsCaret = false;
				c.doit = true;
				return true;
			}
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
		}

		return false;
	}

	/**
	 * This method attempts to determine tab width in the file as it already exists. It checks for two indents of
	 * different sizes and returns their GCD if it's not 1. If we can't get two lines of different lenths, or their GCD
	 * is 1 then we'll fall back to using the editor's expressed tab width via the preferences.
	 * 
	 * @param d
	 * @param startLine
	 * @return
	 */
	private int guessTabWidth(IDocument d, int startLine)
	{
		try
		{
			List<Integer> lengths = new ArrayList<Integer>(3);
			for (int i = startLine; i >= 0; i--)
			{
				IRegion line = d.getLineInformation(i);
				int endofWhitespace = findEndOfWhiteSpace(d, line.getOffset(), line.getOffset() + line.getLength());
				int length = endofWhitespace - line.getOffset();
				if (length == 0)
					continue;
				// We need two different lengths to guess at tab width
				if (lengths.size() < 2 && !lengths.contains(length))
					lengths.add(length);
				if (lengths.size() >= 2)
					break;
			}
			// now we need to do a GCD of the lengths
			int tabWidth = gcd(lengths.get(0), lengths.get(1));
			if (tabWidth != 1)
				return tabWidth;
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
		}

		return getTabWidth();
	}

	private int gcd(int a, int b)
	{
		if (b == 0)
			return a;
		return gcd(b, a % b);
	}

	/**
	 * Method to determine if we want to insert an indent plus another newline and initial indent. Useful for turning
	 * something like "[]" into "[\n  \n]"
	 * 
	 * @param contentBeforeNewline
	 * @param contentAfterNewline
	 * @return
	 */
	protected abstract boolean indentAndPushTrailingContentAfterNewlineAndCursor(String contentBeforeNewline,
			String contentAfterNewline);
}
