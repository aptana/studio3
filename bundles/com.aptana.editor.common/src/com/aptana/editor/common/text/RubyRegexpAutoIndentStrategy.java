/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.jruby.RubyRegexp;
import org.jruby.RubyString;
import org.jruby.runtime.Block;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonSourceViewerConfiguration;
import com.aptana.scripting.model.BundleManager;

/**
 * This implementation uses ruby regular expressions contributed by bundles. It grabs the regexp with the best match for
 * the current scope and then applies that regexp against the line.
 *
 * @author cwilliams
 */
public class RubyRegexpAutoIndentStrategy extends CommonAutoIndentStrategy
{

	private static final String TAB_CHAR = "\t"; //$NON-NLS-1$

	public RubyRegexpAutoIndentStrategy(String contentType, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer, IPreferenceStore prefStore)
	{
		super(contentType, configuration, sourceViewer, prefStore);
	}

	public void customizeDocumentCommand(IDocument document, DocumentCommand command)
	{
		if (command.length == 0 && command.text != null)
		{
			if (isLineDelimiter(document, command.text) && !autoIndent(document, command))
			{
				autoIndentAfterNewLine(document, command);
			}
			else if (!isLineDelimiter(document, command.text))
			{
				autoDedent(document, command);
			}
		}
	}

	private void autoDedent(IDocument d, DocumentCommand c)
	{
		if (c.offset <= 0 || d.getLength() == 0 || c.text.length() > 1)
		{
			return;
		}

		try
		{
			// Get the line and run a regexp check against it
			IRegion curLineRegion = d.getLineInformationOfOffset(c.offset);
			// Only de-dent when at end of line!
			int endOffset = curLineRegion.getOffset() + curLineRegion.getLength();
			if (c.offset != endOffset)
			{
				return;
			}

			String scope = getScopeAtOffset(d, c.offset);
			RubyRegexp decreaseIndentRegexp = getDecreaseIndentRegexp(scope);
			// what line will be after new char is inserted....
			String lineContent = d.get(curLineRegion.getOffset(), c.offset - curLineRegion.getOffset());
			if (matchesRegexp(decreaseIndentRegexp, lineContent + c.text))
			{
				int lineNumber = d.getLineOfOffset(c.offset);
				if (lineNumber == 0) // first line, should be no indent yet...
				{
					return;
				}
				int endIndex = findEndOfWhiteSpace(d, curLineRegion.getOffset(), curLineRegion.getOffset()
						+ curLineRegion.getLength());
				String currentLineIndent = d.get(curLineRegion.getOffset(), endIndex - curLineRegion.getOffset());
				if (currentLineIndent.length() == 0)
				{
					return;
				}
				// Textmate just assumes we subtract one indent level, unless the matching level it should be at is >=
				// what we're at now!
				String decreasedIndent = ""; //$NON-NLS-1$

				// if we subtract one indent level and it is shorter than matching indent, then don't subtract!
				String matchingIndent = findCorrectIndentString(d, lineNumber, currentLineIndent);

				String indentString = getIndentString();
				if (currentLineIndent.length() > indentString.length())
				{
					decreasedIndent = currentLineIndent
							.substring(0, currentLineIndent.length() - indentString.length());
				}
				// if indent level hasn't changed, or shouldn't be moved back, return early!
				if (decreasedIndent.equals(currentLineIndent) || decreasedIndent.length() < matchingIndent.length())
				{
					return;
				}
				// Shift the current line...
				int i = 0;
				while (i < lineContent.length() && Character.isWhitespace(lineContent.charAt(i)))
				{
					i++;
				}
				// Just shift the content beforehand
				String newContent = decreasedIndent + lineContent.substring(i);
				d.replace(curLineRegion.getOffset(), lineContent.length(), newContent);
				c.doit = true;
				int diff = currentLineIndent.length() - decreasedIndent.length();
				c.offset -= diff;
				return;
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}

		return;
	}

	/**
	 * Returns the string that makes up ONE indent level (typically 2 spaces, maybe a tab char, or 4 spaces, etc).
	 *
	 * @return
	 */
	protected String getIndentString()
	{
		if (getSourceViewerConfiguration() instanceof CommonSourceViewerConfiguration)
		{
			return ((CommonSourceViewerConfiguration) getSourceViewerConfiguration()).getIndent();
		}
		return TAB_CHAR;
	}

	protected boolean shouldAutoDedent()
	{
		return true;
	}

	/**
	 * @param d
	 *            the document to work on
	 * @param c
	 *            the command to deal with
	 * @return true if the indentation occurred, false otherwise
	 */
	@Override
	protected boolean autoIndent(IDocument d, DocumentCommand c)
	{
		if (c.offset <= 0 || d.getLength() == 0 || !shouldAutoIndent())
		{
			return false;
		}

		String newline = c.text;
		try
		{
			// Get the line and run a regexp check against it
			IRegion curLineRegion = d.getLineInformationOfOffset(c.offset);
			String scope = getScopeAtOffset(d, c.offset);
			RubyRegexp increaseIndentRegexp = getIncreaseIndentRegexp(scope);
			String lineContent = d.get(curLineRegion.getOffset(), c.offset - curLineRegion.getOffset());

			if (matchesRegexp(increaseIndentRegexp, lineContent))
			{
				String previousLineIndent = getAutoIndentAfterNewLine(d, c);
				String restOfLine = d.get(c.offset, curLineRegion.getLength() - (c.offset - curLineRegion.getOffset()));
				String startIndent = newline + previousLineIndent + getIndentString();
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
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}

		return false;
	}

	protected RubyRegexp getDecreaseIndentRegexp(String scope)
	{
		return BundleManager.getInstance().getDecreaseIndentRegexp(scope);
	}

	protected RubyRegexp getIncreaseIndentRegexp(String scope)
	{
		return BundleManager.getInstance().getIncreaseIndentRegexp(scope);
	}

	protected String getScopeAtOffset(IDocument d, int offset) throws BadLocationException
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(getSourceViewer(), offset);
	}

	protected boolean matchesRegexp(RubyRegexp regexp, String lineContent)
	{
		if (regexp == null)
		{
			return false;
		}
		RubyString string = regexp.getRuntime().newString(lineContent);
		IRubyObject matcher = regexp.match_m19(regexp.getRuntime().getCurrentContext(), string, Block.NULL_BLOCK);
		return !matcher.isNil();
	}

	/**
	 * This method determines the corrected indent string for the current line on dedent trigger. We walk the lines
	 * backward and try to find the matching open/indent (by using the increase indent regexp). If found, we grab that
	 * line's exact indent string and re-use it.
	 *
	 * @param d
	 * @param lineNumber
	 * @param currentLineIndent
	 * @return
	 * @throws BadLocationException
	 */
	protected String findCorrectIndentString(IDocument d, int lineNumber, String currentLineIndent)
			throws BadLocationException
	{
		// Walk lines backward and find the corresponding indenting line for this one. This means we need to match the
		// regexps against each line and keep a stack of them...
		int stack = 0;
		for (int i = lineNumber - 1; i >= 0; i--)
		{
			IRegion region = d.getLineInformation(i);
			String scope = CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(getSourceViewer(), region.getOffset());
			String endScope = CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(getSourceViewer(), region.getOffset() + region.getLength());
			RubyRegexp increaseIndentRegexp = getIncreaseIndentRegexp(scope);
			RubyRegexp decreaseIndentRegexp = getDecreaseIndentRegexp(endScope);

			String lineText = d.get(region.getOffset(), region.getLength());
			if (matchesRegexp(increaseIndentRegexp, lineText))
			{
				// Found an open
				stack++;
				if (stack >= 1)
				{
					// Yay, we found our open! Grab it's indent!
					int endIndex = findEndOfWhiteSpace(d, region.getOffset(), region.getOffset() + region.getLength());
					return d.get(region.getOffset(), endIndex - region.getOffset());
				}
			}
			else if (matchesRegexp(decreaseIndentRegexp, lineText))
			{
				// found a close
				stack--;
			}
		}
		return dedentBasedOnPreviousLine(d, lineNumber, currentLineIndent);
	}

	/**
	 * A less intelligent way of determining new indent on dedent trigger. We look at previous line and if our indent is
	 * of same length or greater, we just assume we need to dedent from that previous line.
	 *
	 * @param d
	 * @param lineNumber
	 * @param currentLineIndent
	 * @return
	 * @throws BadLocationException
	 */
	protected String dedentBasedOnPreviousLine(IDocument d, int lineNumber, String currentLineIndent)
			throws BadLocationException
	{
		int endIndex;
		// Grab previous line's indent level
		IRegion previousLine = d.getLineInformation(lineNumber - 1);
		endIndex = findEndOfWhiteSpace(d, previousLine.getOffset(), previousLine.getOffset() + previousLine.getLength());
		String previousLineIndent = d.get(previousLine.getOffset(), endIndex - previousLine.getOffset());

		// Try to generate a string for a decreased indent level... First, just set to previous line's indent.
		String decreasedIndent = previousLineIndent;
		if (previousLineIndent.length() >= currentLineIndent.length())
		{
			// previous indent level is same or greater than current line's, we should shift current back one
			// level
			if (previousLineIndent.endsWith(TAB_CHAR))
			{
				// Just remove the tab at end
				decreasedIndent = decreasedIndent.substring(0, decreasedIndent.length() - 1);
			}
			else
			{
				// We need to try and remove upto tab-width spaces from end, stop if we hit a tab first
				int tabWidth = guessTabWidth(d, lineNumber);
				String toRemove = decreasedIndent.substring(decreasedIndent.length() - tabWidth);
				int lastTabIndex = toRemove.lastIndexOf(TAB_CHAR);
				if (lastTabIndex != -1)
				{
					// compare last tab index to number of spaces we want to remove.
					tabWidth -= lastTabIndex + 1;
				}
				decreasedIndent = decreasedIndent.substring(0, decreasedIndent.length() - tabWidth);
			}
		}
		return decreasedIndent;
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
				{
					continue;
				}
				// We need two different lengths to guess at tab width
				if (lengths.size() < 2 && !lengths.contains(length))
				{
					lengths.add(length);
				}
				if (lengths.size() >= 2)
				{
					break;
				}
			}
			// now we need to do a GCD of the lengths
			int tabWidth = gcd(lengths.get(0), lengths.get(1));
			if (tabWidth != 1)
			{
				return tabWidth;
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}

		return getTabWidth();
	}

	private int gcd(int a, int b)
	{
		if (b == 0)
		{
			return a;
		}
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
	protected boolean indentAndPushTrailingContentAfterNewlineAndCursor(String contentBeforeNewline,
			String contentAfterNewline)
	{
		// TODO How would bundles specify something like this? Should we just hard-code the common cases of [], (), {},
		// <tag></tag>?
		if (contentBeforeNewline == null || contentAfterNewline == null || contentBeforeNewline.trim().length() == 0
				|| contentAfterNewline.trim().length() == 0)
		{
			return false;
		}
		char before = contentBeforeNewline.charAt(contentBeforeNewline.length() - 1);
		char after = contentAfterNewline.charAt(0);
		if (before == '[' && after == ']')
		{
			return true;
		}
		if (before == '{' && after == '}')
		{
			return true;
		}
		if (before == '(' && after == ')')
		{
			return true;
		}
		if (contentAfterNewline.length() >= 2)
		{
			char afterAfter = contentAfterNewline.charAt(1);
			if (before == '>' && after == '<' && afterAfter == '/')
			{
				return true;
			}
		}
		return false;
	}
}
