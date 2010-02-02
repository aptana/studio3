package com.aptana.editor.html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.editor.common.CommonSourceViewerConfiguration;
import com.aptana.editor.common.text.CommonAutoIndentStrategy;

/**
 * @author cwilliams
 */
public class HTMLAutoIndentStrategy extends CommonAutoIndentStrategy
{

	// TODO Combine common code with RubyAutoIndentStrategy
	private static final String INCREASE_INDENT_REGEXP = "<(body|div|form|frame|head|html|menu|ol|script|style|table|ul)([^>]*)?>$"; //$NON-NLS-1$
	private static final Pattern INCREASE_INDENT_PATTERN = Pattern.compile(INCREASE_INDENT_REGEXP);

	public HTMLAutoIndentStrategy(String contentType, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
	{
		super(contentType, configuration, sourceViewer);
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

			Matcher m = INCREASE_INDENT_PATTERN.matcher(lineContent);
			if (m.find())
			{
				String restOfLine = d.get(c.offset, region.getLength() - (c.offset - region.getOffset()));
				String startIndent = newline + indent + tab;
				if (inMiddleOfIndentingPair(lineContent, restOfLine))
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
	 * Handle [], (), and {} special. We want to push close of pair two lines down and put cursor on next line with
	 * indent.
	 * 
	 * @param contentBeforeNewline
	 * @param contentAfterNewline
	 * @return
	 */
	private boolean inMiddleOfIndentingPair(String contentBeforeNewline, String contentAfterNewline)
	{
		if (contentBeforeNewline == null || contentAfterNewline == null || contentBeforeNewline.trim().length() == 0
				|| contentAfterNewline.trim().length() == 0)
			return false;
		// TODO If we're in middle of matching start/end pair, return true. <html>|</html>
		return true;
	}
}
