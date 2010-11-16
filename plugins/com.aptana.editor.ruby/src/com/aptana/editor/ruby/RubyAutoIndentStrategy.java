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
package com.aptana.editor.ruby;

import java.io.StringReader;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.jrubyparser.Parser.NullWarnings;
import org.jrubyparser.lexer.LexerSource;
import org.jrubyparser.lexer.SyntaxException;
import org.jrubyparser.parser.ParserConfiguration;
import org.jrubyparser.parser.ParserSupport;
import org.jrubyparser.parser.Ruby18Parser;
import org.jrubyparser.parser.RubyParser;

import com.aptana.editor.common.text.RubyRegexpAutoIndentStrategy;

/**
 * Special subclass of auto indenter that will auto-close methods/blocks/classes/types with 'end" when needed.
 * 
 * @author cwilliams
 */
class RubyAutoIndentStrategy extends RubyRegexpAutoIndentStrategy
{
	private final Pattern openBlockPattern = Pattern.compile(".*[\\S].*do[\\w|\\s]*"); //$NON-NLS-1$
	private static final String BLOCK_CLOSER = "end"; //$NON-NLS-1$

	RubyAutoIndentStrategy(String contentType, SourceViewerConfiguration svc, ISourceViewer sourceViewer)
	{
		super(contentType, svc, sourceViewer);
	}

	@Override
	protected boolean autoIndent(IDocument d, DocumentCommand c)
	{
		if (!super.autoIndent(d, c))
		{
			try
			{
				int p = (c.offset == d.getLength() ? c.offset - 1 : c.offset);
				int line = d.getLineOfOffset(p);
				IRegion currentLineRegion = d.getLineInformation(line);
				int startOfCurrentLine = currentLineRegion.getOffset();
				String lineString = d.get(startOfCurrentLine, c.offset - startOfCurrentLine);

				if (lineString.startsWith("=begin")) //$NON-NLS-1$
				{
					// TODO If doesn't start at beginning of line, move to first column?
					String indent = getIndentString();
					c.text += indent;
					c.caretOffset = c.offset + indent.length();
					c.shiftsCaret = false;
					c.text += TextUtilities.getDefaultLineDelimiter(d) + "=end"; //$NON-NLS-1$
					return true;
				}
			}
			catch (BadLocationException e)
			{
				// ignore
			}
			return false;
		}

		// Ruble says we're at an indentation point, this is where we should look for closing with "end"
		try
		{
			int p = (c.offset == d.getLength() ? c.offset - 1 : c.offset);
			int line = d.getLineOfOffset(p);
			IRegion currentLineRegion = d.getLineInformation(line);
			int startOfCurrentLine = currentLineRegion.getOffset();
			String trimmed = getTrimmedLine(d, startOfCurrentLine, c.offset);

			if (trimmed.equals("=begin")) //$NON-NLS-1$
			{
				// TODO If doesn't start at beginning of line, move to first column
				String indent = getIndentString();
				c.text += indent;
				c.caretOffset = c.offset + indent.length();
				c.shiftsCaret = false;
				c.text += TextUtilities.getDefaultLineDelimiter(d) + "=end"; //$NON-NLS-1$
			}
			// insert closing "end" on new line after an unclosed block
			if (closeBlock() && unclosedBlock(d, trimmed, c.offset))
			{
				String previousLineIndent = getAutoIndentAfterNewLine(d, c);
				c.text += TextUtilities.getDefaultLineDelimiter(d) + previousLineIndent + BLOCK_CLOSER;
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		return true;
	}

	private boolean unclosedBlock(IDocument d, String trimmed, int offset)
	{
		// FIXME wow is this ugly! There has to be an easier way to tell if there's an unclosed block besides parsing
		// and catching a syntaxError!
		if (!atStartOfBlock(trimmed))
		{
			return false;
		}

		ParserConfiguration config = new ParserConfiguration();
		ParserSupport support = new ParserSupport();
		support.setConfiguration(config);
		support.setWarnings(new NullWarnings());
		RubyParser parser = new Ruby18Parser(support);
		LexerSource lexerSource = null;
		try
		{
			lexerSource = LexerSource.getSource("", new StringReader(d.get()), config); //$NON-NLS-1$
			parser.parse(config, lexerSource);
		}
		catch (SyntaxException e)
		{
			if (e.getPid() != SyntaxException.PID.GRAMMAR_ERROR)
			{
				return false;
			}
			try
			{
				StringBuffer buffer = new StringBuffer(d.get());
				buffer.insert(offset, TextUtilities.getDefaultLineDelimiter(d) + BLOCK_CLOSER);
				lexerSource = LexerSource.getSource("", new StringReader(buffer.toString()), config); //$NON-NLS-1$
				parser.parse(config, lexerSource);
			}
			catch (SyntaxException syntaxException)
			{
				return false;
			}
			return true;
		}
		return false;
	}

	private String getTrimmedLine(IDocument d, int start, int offset) throws BadLocationException
	{
		return d.get(start, offset - start).trim();
	}

	@SuppressWarnings("nls")
	private boolean atStartOfBlock(String line)
	{
		return line.startsWith("class ") || line.startsWith("if ") || line.startsWith("while ") || line.startsWith("module ")
				|| line.startsWith("unless ") || line.startsWith("def ") || line.equals("begin")
				|| line.startsWith("case ") || line.startsWith("for ") || openBlockPattern.matcher(line).matches();
	}

	private boolean closeBlock()
	{
		// TODO Set up a pref value for user to turn this behavior off?
		return true;
	}

}
