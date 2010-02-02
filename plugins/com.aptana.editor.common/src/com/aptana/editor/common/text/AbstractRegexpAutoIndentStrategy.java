package com.aptana.editor.common.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.editor.common.CommonSourceViewerConfiguration;

public abstract class AbstractRegexpAutoIndentStrategy extends CommonAutoIndentStrategy
{

	private Pattern regexp;

	public AbstractRegexpAutoIndentStrategy(String regexp, String contentType, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
	{
		super(contentType, configuration, sourceViewer);
		this.regexp = Pattern.compile(regexp);
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

		String indent = getAutoIndentAfterNewLine(d, c);
		String newline = c.text;
		String tab = "\t"; //$NON-NLS-1$
		if (getSourceViewerConfiguration() instanceof CommonSourceViewerConfiguration)
		{
			tab = ((CommonSourceViewerConfiguration) getSourceViewerConfiguration()).getIndent();
		}

		try
		{
			// Get the line and run a regexp check against it
			IRegion region = d.getLineInformationOfOffset(c.offset);
			String lineContent = d.get(region.getOffset(), c.offset - region.getOffset());

			Matcher m = regexp.matcher(lineContent);
			if (m.find())
			{
				String restOfLine = d.get(c.offset, region.getLength() - (c.offset - region.getOffset()));
				String startIndent = newline + indent + tab;
				if (indentAndPushTrailingContentAfterNewlineAndCursor(lineContent, restOfLine))
				{
					c.text = startIndent + newline + indent;
				}
				else
				{
					c.text = startIndent;
				}
				c.shiftsCaret = false;
				c.caretOffset = c.offset + startIndent.length();
				return true;
			}
		}
		catch (BadLocationException e)
		{
		}

		return false;
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
