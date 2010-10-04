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

import java.util.Vector;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jrubyparser.parser.Tokens;

public class RubyCodeScanner implements ITokenScanner
{

	private RubyTokenScanner fScanner;
	private boolean nextIsMethodName;
	private boolean nextIsModuleName;
	private boolean nextIsClassName;
	private boolean inPipe;
	private boolean lookForBlock;
	private boolean nextAreArgs;
	private Vector<QueuedToken> queue;
	private int fLength;
	private int fOffset;

	public RubyCodeScanner()
	{
		fScanner = new RubyTokenScanner();
	}

	public int getTokenLength()
	{
		return fLength;
	}

	public int getTokenOffset()
	{
		return fOffset;
	}

	public IToken nextToken()
	{
		IToken intToken = pop();
		if (intToken.isEOF())
			return Token.EOF;
		Integer data = (Integer) intToken.getData();

		if (lookForBlock)
		{
			if (!inPipe && data.intValue() != Tokens.tPIPE)
				lookForBlock = false;
		}

		if (nextAreArgs && (isNewline(data) || data.intValue() == RubyTokenScanner.SEMICOLON))
		{
			nextAreArgs = false;
		}

		// Convert the integer tokens into tokens containing color information!
		if (isKeyword(data.intValue()))
		{
			switch (data.intValue())
			{
				case Tokens.k__FILE__:
				case Tokens.k__LINE__:
				case Tokens.kSELF:
					if (nextIsClassName)
					{
						nextIsClassName = false;
						return getToken("entity.name.type.class.ruby"); //$NON-NLS-1$
					}
					return getToken("variable.language.ruby"); //$NON-NLS-1$
				case Tokens.kNIL:
				case Tokens.kTRUE:
				case Tokens.kFALSE:
					return getToken("constant.language.ruby"); //$NON-NLS-1$
				case Tokens.kAND:
				case Tokens.kNOT:
				case Tokens.kOR:
					return getToken("keyword.operator.logical.ruby"); //$NON-NLS-1$
				case Tokens.kDO_BLOCK:
				case Tokens.kDO:
					lookForBlock = true;
					return getToken("keyword.control.start-block.ruby"); //$NON-NLS-1$
				case Tokens.kCLASS:
					nextIsClassName = true;
					return getToken("keyword.control.class.ruby"); //$NON-NLS-1$
				case Tokens.kMODULE:
					nextIsModuleName = true;
					return getToken("keyword.control.module.ruby"); //$NON-NLS-1$
				case Tokens.kDEF:
					nextIsMethodName = true;
					return getToken("keyword.control.def.ruby"); //$NON-NLS-1$
				default:
					if (nextIsMethodName)
					{
						nextIsMethodName = false;
						nextAreArgs = true;
						return getToken("entity.name.function.ruby"); //$NON-NLS-1$
					}
					return getToken("keyword.control.ruby"); //$NON-NLS-1$
			}
		}
		switch (data.intValue())
		{
			case RubyTokenScanner.ASSIGNMENT:
				return getToken("keyword.operator.assignment.ruby"); //$NON-NLS-1$
			case Tokens.tCMP: /* <=> */
			case Tokens.tMATCH: /* =~ */
			case Tokens.tNMATCH: /* !~ */
			case Tokens.tEQ: /* == */
			case Tokens.tEQQ: /* === */
			case Tokens.tNEQ: /* != */
			case Tokens.tGEQ: /* >= */
			case Tokens.tLEQ:
			case Tokens.tLT:
			case Tokens.tGT:
				if (nextIsMethodName)
				{
					nextIsMethodName = false;
					nextAreArgs = true;
					return getToken("entity.name.function.ruby"); //$NON-NLS-1$
				}
				return getToken("keyword.operator.comparison.ruby"); //$NON-NLS-1$
			case Tokens.tSTAR:
				if (nextAreArgs) // could be un-named rest arg
				{
					return getToken("variable.parameter.ruby"); //$NON-NLS-1$
				}
				// intentionally fall-through
			case Tokens.tAMPER:
			case Tokens.tPERCENT:
			case Tokens.tPOW:
			case Tokens.tSTAR2:
			case Tokens.tPLUS:
			case Tokens.tMINUS:
			case Tokens.tDIVIDE:
				if (nextIsMethodName)
				{
					nextIsMethodName = false;
					nextAreArgs = true;
					return getToken("entity.name.function.ruby"); //$NON-NLS-1$
				}
				return getToken("keyword.operator.arithmetic.ruby"); //$NON-NLS-1$			
			case Tokens.tANDOP:
			case Tokens.tAMPER2: // &
			case Tokens.tTILDE:
			case Tokens.tBANG:
			case Tokens.tOROP:
			case Tokens.tCARET:
			case RubyTokenScanner.QUESTION:
				if (nextIsMethodName)
				{
					nextIsMethodName = false;
					nextAreArgs = true;
					return getToken("entity.name.function.ruby"); //$NON-NLS-1$
				}
				return getToken("keyword.operator.logical.ruby"); //$NON-NLS-1$
			case Tokens.tAREF:
			case Tokens.tASET:
			case Tokens.tUPLUS:
			case Tokens.tUMINUS:
			case Tokens.tUMINUS_NUM:
				nextIsMethodName = false;
				nextAreArgs = true;
				return getToken("entity.name.function.ruby"); //$NON-NLS-1$			
			case Tokens.tPIPE:
				if (lookForBlock)
				{
					inPipe = !inPipe;
					if (!inPipe)
						lookForBlock = false;
					return getToken("default.ruby"); //$NON-NLS-1$
				}
				if (nextIsMethodName)
				{
					nextIsMethodName = false;
					nextAreArgs = true;
					return getToken("entity.name.function.ruby"); //$NON-NLS-1$
				}
				return getToken("keyword.operator.logical.ruby"); //$NON-NLS-1$
			case Tokens.tLBRACE:
				lookForBlock = true;
				return getToken("default.ruby"); //$NON-NLS-1$
			case Tokens.tRPAREN:
				nextAreArgs = false;
				return getToken("default.ruby"); //$NON-NLS-1$
			case Tokens.tLSHFT:
				if (nextIsClassName)
				{
					return getToken("entity.name.type.class.ruby"); //$NON-NLS-1$
				}
				if (nextIsMethodName)
				{
					nextIsMethodName = false;
					nextAreArgs = true;
					return getToken("entity.name.function.ruby"); //$NON-NLS-1$
				}
				return getToken("keyword.operator.assignment.augmented.ruby"); //$NON-NLS-1$
			case Tokens.tOP_ASGN:
				return getToken("keyword.operator.assignment.augmented.ruby"); //$NON-NLS-1$
			case Tokens.tASSOC:
				return getToken("punctuation.separator.key-value"); //$NON-NLS-1$
			case RubyTokenScanner.CHARACTER:
				return getToken("character.ruby"); //$NON-NLS-1$
			case Tokens.tCOLON2:
			case Tokens.tCOLON3:
				return getToken("punctuation.separator.inheritance.ruby"); //$NON-NLS-1$
			case Tokens.tFLOAT:
			case Tokens.tINTEGER:
				return getToken("constant.numeric.ruby"); //$NON-NLS-1$
			case Tokens.tSYMBEG:
				return getToken("constant.other.symbol.ruby"); //$NON-NLS-1$
			case Tokens.tGVAR:
				return getToken("variable.other.readwrite.global.ruby"); //$NON-NLS-1$
			case Tokens.tIVAR:
				return getToken("variable.other.readwrite.instance.ruby"); //$NON-NLS-1$
			case Tokens.tCVAR:
				return getToken("variable.other.readwrite.class.ruby"); //$NON-NLS-1$
			case Tokens.tCONSTANT:
				if (nextIsModuleName)
				{
					nextIsModuleName = false;
					return getToken("entity.name.type.module.ruby"); //$NON-NLS-1$
				}
				if (nextIsClassName)
				{
					nextIsClassName = false;
					return getToken("entity.name.type.class.ruby"); //$NON-NLS-1$
				}
				int nextToken = peek();
				if (nextToken == Tokens.tCOLON2 || nextToken == Tokens.tDOT)
				{
					return getToken("support.class.ruby"); //$NON-NLS-1$
				}
				return getToken("variable.other.constant.ruby"); //$NON-NLS-1$
			case Tokens.yyErrorCode:
				return getToken("error.ruby"); //$NON-NLS-1$
			case Tokens.tIDENTIFIER:
			case Tokens.tFID:
				if (nextAreArgs)
				{
					return getToken("variable.parameter.ruby"); //$NON-NLS-1$
				}
				if (nextIsMethodName)
				{
					nextIsMethodName = false;
					nextAreArgs = true;
					return getToken("entity.name.function.ruby"); //$NON-NLS-1$
				}
				if (lookForBlock && inPipe)
					return getToken("variable.other.block.ruby"); //$NON-NLS-1$
				// intentionally fall through
			default:
				return getToken("default.ruby"); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("nls")
	protected boolean isNewline(Integer data)
	{
		if (data.intValue() == RubyTokenScanner.NEWLINE)
			return true;
		if (data.intValue() != Tokens.tWHITESPACE)
			return false;
		// make sure it's actually a newline
		String tokenSrc = fScanner.getSource(fOffset, fLength);
		if (tokenSrc == null)
			return false;
		return tokenSrc.equals("\r\n") || tokenSrc.equals("\n") || tokenSrc.equals("\r");
	}

	protected IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}

	private IToken pop()
	{
		IToken intToken = null;
		if (queue == null || queue.isEmpty())
		{
			intToken = fScanner.nextToken();
			fOffset = fScanner.getTokenOffset();
			fLength = fScanner.getTokenLength();
		}
		else
		{
			QueuedToken queued = queue.remove(0);
			fOffset = queued.getOffset();
			fLength = queued.getLength();
			intToken = queued.getToken();
		}
		if (intToken == null)
			return Token.EOF;
		Integer data = (Integer) intToken.getData();
		if (data == null)
			return Token.EOF;

		return intToken;
	}

	private int peek()
	{
		int oldOffset = getTokenOffset();
		int oldLength = getTokenLength();
		IToken next = pop();
		push(next);
		fOffset = oldOffset;
		fLength = oldLength;
		if (next.isEOF())
		{
			return -1;
		}
		Integer data = (Integer) next.getData();
		return data.intValue();
	}

	private void push(IToken next)
	{
		if (queue == null)
		{
			queue = new Vector<QueuedToken>();
		}
		queue.add(new QueuedToken(next, getTokenOffset(), getTokenLength()));
	}

	public void setRange(IDocument document, int offset, int length)
	{
		fScanner.setRange(document, offset, length);
		reset();
	}

	private void reset()
	{
		nextIsMethodName = false;
		nextIsModuleName = false;
		nextIsClassName = false;
		inPipe = false;
		lookForBlock = false;
		nextAreArgs = false;
		queue = null;
	}

	private boolean isKeyword(int i)
	{
		return (i >= RubyTokenScanner.MIN_KEYWORD && i <= RubyTokenScanner.MAX_KEYWORD);
	}
}
