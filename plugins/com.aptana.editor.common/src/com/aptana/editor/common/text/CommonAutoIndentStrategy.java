/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.preferences.IPreferenceConstants;

/**
 * This class will auto-indent after curly braces {} by default (and won't auto dedent on close brace). Subclasses
 * should override to provide their own rules for indenting.
 * 
 * @author Ingo Muschenetz
 * @author Michael Xia (mxia@aptana.com)
 */
public abstract class CommonAutoIndentStrategy implements IAutoEditStrategy
{
	private static final String SPACE = " "; //$NON-NLS-1$
	protected static final char COMMENT_CHAR = '*';
	protected static final String DOCUMENTATION_START = "/**"; //$NON-NLS-1$
	protected static final String COMMENT_END = "*/"; //$NON-NLS-1$
	protected static final String COMMENT_START = "/*"; //$NON-NLS-1$
	private String fContentType;
	private SourceViewerConfiguration fViewerConfiguration;
	private ISourceViewer fSourceViewer;
	private IPreferenceStore fPrefStore;

	protected CommonAutoIndentStrategy(String contentType, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer, IPreferenceStore prefStore)
	{
		fContentType = contentType;
		fViewerConfiguration = configuration;
		fSourceViewer = sourceViewer;
		fPrefStore = prefStore;
	}

	protected SourceViewerConfiguration getSourceViewerConfiguration()
	{
		return fViewerConfiguration;
	}

	protected ISourceViewer getSourceViewer()
	{
		return fSourceViewer;
	}

	/**
	 * Returns the first offset greater than <code>offset</code> and smaller than <code>end</code> whose character is
	 * not a space or tab character. If no such offset is found, <code>end</code> is returned.
	 * 
	 * @param document
	 *            the document to search in
	 * @param offset
	 *            the offset at which searching starts
	 * @param end
	 *            the offset at which searching stops
	 * @return the offset in the specified range whose character is not a space or tab
	 * @exception BadLocationException
	 *                if position is an invalid range in the given document
	 */
	protected int findEndOfWhiteSpace(IDocument document, int offset, int end) throws BadLocationException
	{
		while (offset < end)
		{
			char c = document.getChar(offset);
			if (c != ' ' && c != '\t')
			{
				return offset;
			}
			++offset;
		}
		return end;
	}

	/**
	 * Copies the indentation of the previous line.
	 * 
	 * @param d
	 *            the document to work on
	 * @param c
	 *            the command to deal with
	 */
	protected void autoIndentAfterNewLine(IDocument d, DocumentCommand c)
	{
		c.text += getAutoIndentAfterNewLine(d, c);
	}

	/**
	 * @param d
	 *            the document to work on
	 * @param c
	 *            the command to deal with
	 * @return true if the indentation occurred, false otherwise
	 */
	abstract protected boolean autoIndent(IDocument d, DocumentCommand c);

	/**
	 * Gets the indentation of the previous line.
	 * 
	 * @param d
	 *            the document to work on
	 * @param c
	 *            the command to deal with
	 * @return String the indentation of the previous line
	 */
	protected String getAutoIndentAfterNewLine(IDocument d, DocumentCommand c)
	{
		if (c.offset == -1 || d.getLength() == 0)
		{
			return StringUtil.EMPTY;
		}

		StringBuilder buf = new StringBuilder();
		try
		{
			// finds the start of line
			IRegion info = d.getLineInformationOfOffset(c.offset);
			int start = info.getOffset();
			// finds the white spaces
			int end = findEndOfWhiteSpace(d, start, c.offset);

			String indent = StringUtil.EMPTY;
			char c1 = d.getChar(end);
			if (end > start)
			{
				indent = d.get(start, end - start);
			}
			// append to input
			buf.append(indent);

			String line = d.get(info.getOffset(), info.getLength());
			int lineOffset = c.offset - info.getOffset();
			String upToOffset = line.substring(0, lineOffset);
			String trimmedUpToOffset = upToOffset.trim();

			// What we want is to add a star if user hit return inside a comment block
			if (c1 == COMMENT_CHAR && !trimmedUpToOffset.startsWith(COMMENT_END)
					&& upToOffset.lastIndexOf(COMMENT_END) <= upToOffset.lastIndexOf(COMMENT_START)
					&& isComment(c.offset))
			{
				buf.append(COMMENT_CHAR);
				if (d.getChar(c.offset) != '/')
				{
					buf.append(' ');
				}
			}
			// Return after /*
			else if (trimmedUpToOffset.startsWith(COMMENT_START) && !upToOffset.contains(COMMENT_END))
			{
				String commentIndent = SPACE; // space to line up asterisk
				String restOfLine = line.substring(lineOffset);

				// get the line delimiter of the text file
				String lineDelimiter = TextUtilities.getDefaultLineDelimiter(d);

				if (trimmedUpToOffset.startsWith(DOCUMENTATION_START)) // ScriptDoc block
				{
					handleScriptDoc(d, c, buf, commentIndent, lineDelimiter);
				}
				// if next char is an asterisk, no need to add an asterisk
				else if (!StringUtil.startsWith(restOfLine, COMMENT_CHAR))
				{
					buf.append(commentIndent).append(COMMENT_CHAR).append(SPACE);
				}
				else
				{
					buf.append(commentIndent);
				}

				// Already closed block comment on this line after we hit return, so don't close it again
				if (restOfLine.contains(COMMENT_END))
				{
					return buf.toString();
				}
				if (checkNextLineForComment(d, c))
				{
					return buf.toString();
				}

				String toEnd = SPACE + COMMENT_END;
				d.replace(c.offset, 0, lineDelimiter + indent + toEnd);
			}
			else if (trimmedUpToOffset.endsWith(COMMENT_START) || trimmedUpToOffset.endsWith(DOCUMENTATION_START))
			{
				// the rest of the line is the content before the start of the comment
				int index = line.lastIndexOf(COMMENT_START);
				String restOfLine = line.substring(0, index);
				// space to line up asterisk
				String commentIndent = StringUtil.pad(StringUtil.EMPTY, restOfLine.length() - indent.length() + 1, ' ');

				// get the line delimiter of the text file
				String lineDelimiter = TextUtilities.getDefaultLineDelimiter(d);

				if (trimmedUpToOffset.endsWith(DOCUMENTATION_START)) // ScriptDoc block
				{
					handleScriptDoc(d, c, buf, commentIndent, lineDelimiter);
				}
				else
				{
					buf.append(commentIndent).append(COMMENT_CHAR).append(SPACE);
				}

				if (checkNextLineForComment(d, c))
				{
					return buf.toString();
				}

				String toEnd = commentIndent + COMMENT_END;
				d.replace(c.offset, 0, lineDelimiter + indent + toEnd);
			}
			else if (buf.length() != 0 && upToOffset.endsWith(COMMENT_END) && buf.charAt(buf.length() - 1) == ' ')
			{
				// We want to delete an extra space when closing block comments
				buf.deleteCharAt(buf.length() - 1);
			}

		}
		catch (BadLocationException e)
		{
		}
		return buf.toString();
	}

	protected boolean checkNextLineForComment(IDocument document, DocumentCommand command)
	{
		try
		{
			IRegion nextLineInfo = document.getLineInformationOfOffset(command.offset + 1);
			String nextLine = document.get(nextLineInfo.getOffset(), nextLineInfo.getLength()).trim();
			return (StringUtil.startsWith(nextLine, COMMENT_CHAR) && !StringUtil.startsWith(nextLine, COMMENT_END))
					|| nextLine.endsWith(COMMENT_END);
		}
		catch (BadLocationException e)
		{
		}
		return false;
	}

	protected void handleScriptDoc(IDocument document, DocumentCommand command, StringBuilder buf,
			String commentIndent, String lineDelimiter)
	{
		buf.append(commentIndent).append(COMMENT_CHAR).append(SPACE);

		command.shiftsCaret = false;
		command.caretOffset = command.offset + buf.length() + 1;

		List<String> list = getAdditionalComments(document, command);
		if (list != null && list.size() > 0)
		{
			buf.append(lineDelimiter).append(commentIndent).append(COMMENT_CHAR).append(SPACE);
			buf.append(StringUtil.join(lineDelimiter + commentIndent + COMMENT_CHAR + SPACE, list));
		}
	}

	/**
	 * Returns additional lines of information to append to the auto-inserted comments. Each entry will become a
	 * separate line
	 * 
	 * @param d
	 *            The current document
	 * @param c
	 *            The current document command
	 * @return
	 */
	protected List<String> getAdditionalComments(IDocument d, DocumentCommand c)
	{
		return null;
	}

	protected boolean isComment(int offset)
	{
		if (IDocument.DEFAULT_CONTENT_TYPE.equals(fContentType))
		{
			return false;
		}
		if (fContentType.contains("_comment")) //$NON-NLS-1$
		{
			return true;
		}
		// TODO Check the scope?
		return false;
	}

	/**
	 * Calculates the whitespace prefix based on user preferences and the existing line, e.g. if the line prefix is five
	 * spaces, and user pref is tabs of width 4, then the result will be "/t ".
	 * 
	 * @param d
	 *            the document
	 * @param offset
	 *            the offset at which searching starts
	 * @param end
	 *            the offset at which searching ends
	 * @return Returns the whitespace prefix based on user preferences and the existing line
	 */
	protected String getIndentationString(IDocument d, int offset, int end)
	{
		String lineIndent = ""; //$NON-NLS-1$
		try
		{
			lineIndent = d.get(offset, end - offset);
		}
		catch (BadLocationException e)
		{
		}
		if (lineIndent.equals("")) { //$NON-NLS-1$
			return lineIndent;
		}

		int indentSize = 0;
		int tabWidth = Math.max(1, getTabWidth());
		char[] indentChars = lineIndent.toCharArray();
		for (char e : indentChars)
		{
			if (e == '\t')
			{
				indentSize += tabWidth - (indentSize % tabWidth);
			}
			else
			{
				indentSize++;
			}
		}

		String indentCharStr = getIndentCharString();
		int indentStringWidth = indentCharStr.equals("\t") ? tabWidth : indentCharStr.length(); //$NON-NLS-1$
		// return in case tab width is zero
		if (indentStringWidth == 0)
		{
			return ""; //$NON-NLS-1$
		}

		String indentation = ""; //$NON-NLS-1$
		int indentCount = indentSize / indentStringWidth;
		for (int i = 0; i < indentCount; ++i)
		{
			indentation += indentCharStr;
		}

		int spaceCount = indentSize % indentStringWidth;
		for (int i = 0; i < spaceCount; ++i)
		{
			indentation += SPACE;
		}

		return indentation;
	}

	protected int getTabWidth()
	{
		return fViewerConfiguration.getTabWidth(fSourceViewer);
	}

	protected String getIndentCharString()
	{
		String[] indents = fViewerConfiguration.getIndentPrefixes(fSourceViewer, fContentType);
		boolean hasIndents = indents != null && indents.length > 0;
		return hasIndents ? indents[0] : "\t"; //$NON-NLS-1$
	}

	protected static boolean isLineDelimiter(IDocument d, String text)
	{
		String[] delimiters = d.getLegalLineDelimiters();
		if (delimiters == null)
		{
			return false;
		}
		return TextUtilities.equals(delimiters, text) > -1;
	}

	/**
	 * Subclasses can override to allow users to set a pref to determine if we have auto-indent on.
	 * 
	 * @return
	 */
	protected boolean shouldAutoIndent()
	{
		if (fPrefStore != null)
		{
			return fPrefStore.getBoolean(IPreferenceConstants.EDITOR_AUTO_INDENT);
		}
		return true;
	}
}
