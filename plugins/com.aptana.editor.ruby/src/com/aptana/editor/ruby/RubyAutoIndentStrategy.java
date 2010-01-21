package com.aptana.editor.ruby;

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
public class RubyAutoIndentStrategy extends CommonAutoIndentStrategy
{
	/**
	 * Nasty regexp taken from Textmate for auto-indenting.
	 */
	private static final String INCREASE_INDENT_REGEXP = "(\\s*(module|class|def|unless|if|else|elsif|case|when|begin|rescue|ensure|for|while|until|(?=.*?\\b(do|begin|case|if|unless)\\b)(\"(\\.|[^\\\"])*+\"|''(\\.|[^\\''])*+''|[^#\"''])*(\\s(do|begin|case)|[-+=&|*/~%^<>~](?<!\\$.)\\s*+(if|unless)))\\b(?![^;]*+;.*?\bend\b)|(\"(\\.|[^\\\"])*+\"|''(\\.|[^\\''])*+''|[^#\"''])*(\\{(?![^}]*+\\})|\\[(?![^\\]]*+\\]))).*$"; //$NON-NLS-1$
	private static final Pattern INCREASE_INDENT_PATTERN = Pattern.compile(INCREASE_INDENT_REGEXP);

	public RubyAutoIndentStrategy(String contentType, SourceViewerConfiguration configuration,
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
		contentBeforeNewline = contentBeforeNewline.trim();
		contentAfterNewline = contentAfterNewline.trim();
		char before = contentBeforeNewline.charAt(contentBeforeNewline.length() - 1);
		char after = contentAfterNewline.charAt(0);
		if (before == '[' && after == ']')
			return true;
		if (before == '{' && after == '}')
			return true;
		if (before == '(' && after == ')')
			return true;
		return false;
	}
}
