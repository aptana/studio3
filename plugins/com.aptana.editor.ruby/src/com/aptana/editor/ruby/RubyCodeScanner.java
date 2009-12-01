package com.aptana.radrails.editor.ruby;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jrubyparser.parser.Tokens;

import com.aptana.radrails.editor.common.theme.ThemeUtil;

public class RubyCodeScanner implements ITokenScanner
{

	private ITokenScanner fScanner;
	private boolean nextIsMethodName;
	private boolean nextIsModuleName;
	private boolean nextIsClassName;
	private boolean inPipe;
	private boolean lookForBlock;

	public RubyCodeScanner()
	{
		fScanner = new RubyTokenScanner();
	}

	public int getTokenLength()
	{
		return fScanner.getTokenLength();
	}

	public int getTokenOffset()
	{
		return fScanner.getTokenOffset();
	}

	public IToken nextToken()
	{
		IToken intToken = fScanner.nextToken();
		if (intToken == null || intToken.isEOF())
		{
			return Token.EOF;
		}
		Integer data = (Integer) intToken.getData();
		if (data == null)
		{
			return Token.EOF;
		}

		if (lookForBlock)
		{
			if (!inPipe && data.intValue() != Tokens.tPIPE)
				lookForBlock = false;
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
						return ThemeUtil.getToken("entity.name.type.class.ruby"); //$NON-NLS-1$
					}
					return ThemeUtil.getToken("variable.language.ruby"); //$NON-NLS-1$
				case Tokens.kNIL:
				case Tokens.kTRUE:
				case Tokens.kFALSE:
					return ThemeUtil.getToken("constant.language.ruby"); //$NON-NLS-1$
				case Tokens.kAND:
				case Tokens.kNOT:
				case Tokens.kOR:
					return ThemeUtil.getToken("keyword.operator.logical.ruby"); //$NON-NLS-1$
				case Tokens.kDO_BLOCK:
				case Tokens.kDO:
					lookForBlock = true;
					return ThemeUtil.getToken("keyword.control.start-block.ruby"); //$NON-NLS-1$
				case Tokens.kCLASS:
					nextIsClassName = true;
					return ThemeUtil.getToken("keyword.control.class.ruby"); //$NON-NLS-1$
				case Tokens.kMODULE:
					nextIsModuleName = true;
					return ThemeUtil.getToken("keyword.control.module.ruby"); //$NON-NLS-1$
				case Tokens.kDEF:
					nextIsMethodName = true;
					return ThemeUtil.getToken("keyword.control.def.ruby"); //$NON-NLS-1$
				default:
					return ThemeUtil.getToken("keyword.control.ruby"); //$NON-NLS-1$
			}
		}
		switch (data.intValue())
		{
			case RubyTokenScanner.ASSIGNMENT:
				return ThemeUtil.getToken("keyword.operator.assignment.ruby"); //$NON-NLS-1$
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
				return ThemeUtil.getToken("keyword.operator.comparison.ruby"); //$NON-NLS-1$				
			case Tokens.tAMPER:
			case Tokens.tPERCENT:
			case Tokens.tPOW:
			case Tokens.tSTAR:
			case Tokens.tPLUS:
			case Tokens.tMINUS:
			case Tokens.tDIVIDE:
				return ThemeUtil.getToken("keyword.operator.arithmetic.ruby"); //$NON-NLS-1$			
			case Tokens.tANDOP:
			case Tokens.tBANG:
			case Tokens.tOROP:
			case Tokens.tCARET:
			case RubyTokenScanner.QUESTION:
				return ThemeUtil.getToken("keyword.operator.logical.ruby"); //$NON-NLS-1$
			case Tokens.tPIPE:
				if (lookForBlock)
				{
					inPipe = !inPipe;
					if (!inPipe)
						lookForBlock = false;
					return ThemeUtil.getToken("default.ruby"); //$NON-NLS-1$
				}
				return ThemeUtil.getToken("keyword.operator.logical.ruby"); //$NON-NLS-1$
			case Tokens.tLBRACE:
				lookForBlock = true;
				return ThemeUtil.getToken("default.ruby"); //$NON-NLS-1$
			case Tokens.tLSHFT:
				if (nextIsClassName)
				{
					return ThemeUtil.getToken("entity.name.type.class.ruby"); //$NON-NLS-1$
				}
				return ThemeUtil.getToken("keyword.operator.assignment.augmented.ruby"); //$NON-NLS-1$
			case Tokens.tOP_ASGN:
				return ThemeUtil.getToken("keyword.operator.assignment.augmented.ruby"); //$NON-NLS-1$
			case Tokens.tASSOC:
				return ThemeUtil.getToken("punctuation.separator.key-value"); //$NON-NLS-1$
			case RubyTokenScanner.CHARACTER:
				return ThemeUtil.getToken("character.ruby"); //$NON-NLS-1$
			case Tokens.tCOLON2:
			case Tokens.tCOLON3:
				return ThemeUtil.getToken("punctuation.separator.inheritance.ruby"); //$NON-NLS-1$
			case Tokens.tFLOAT:
			case Tokens.tINTEGER:
				return ThemeUtil.getToken("constant.numeric.ruby"); //$NON-NLS-1$
			case Tokens.tSYMBEG:
				return ThemeUtil.getToken("constant.other.symbol.ruby"); //$NON-NLS-1$
			case Tokens.tGVAR:
				return ThemeUtil.getToken("variable.other.readwrite.global.ruby"); //$NON-NLS-1$
			case Tokens.tIVAR:
				return ThemeUtil.getToken("variable.other.readwrite.instance.ruby"); //$NON-NLS-1$
			case Tokens.tCVAR:
				return ThemeUtil.getToken("variable.other.readwrite.class.ruby"); //$NON-NLS-1$
			case Tokens.tCONSTANT:
				if (nextIsModuleName)
				{
					nextIsModuleName = false;
					return ThemeUtil.getToken("entity.name.type.module.ruby"); //$NON-NLS-1$
				}
				if (nextIsClassName)
				{
					nextIsClassName = false;
					return ThemeUtil.getToken("entity.name.type.class.ruby"); //$NON-NLS-1$
				}
				// FIXME Need to return "support.class.ruby" if it ends in "::" or "\.*"
				return ThemeUtil.getToken("variable.other.constant.ruby"); //$NON-NLS-1$
			case Tokens.yyErrorCode:
				return ThemeUtil.getToken("error.ruby"); //$NON-NLS-1$
			case Tokens.tIDENTIFIER:
			case Tokens.tFID:
				if (nextIsMethodName)
				{
					nextIsMethodName = false;
					return ThemeUtil.getToken("entity.name.function.ruby"); //$NON-NLS-1$
				}
				if (lookForBlock && inPipe)
					return ThemeUtil.getToken("variable.other.block.ruby"); //$NON-NLS-1$
				// intentionally fall through
			default:
				return ThemeUtil.getToken("default.ruby"); //$NON-NLS-1$
		}
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
	}

	private boolean isKeyword(int i)
	{
		return (i >= RubyTokenScanner.MIN_KEYWORD && i <= RubyTokenScanner.MAX_KEYWORD);
	}
}
