package com.aptana.editor.js.parsing.lexer;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;

import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;

public class JSLexemeProvider extends LexemeProvider<JSTokenType>
{
	/**
	 * Convert the partition that contains the given offset into a list of lexemes.
	 * 
	 * @param document
	 * @param offset
	 * @param scanner
	 */
	public JSLexemeProvider(IDocument document, int offset, ITokenScanner scanner)
	{
		super(document, offset, scanner);
	}

	/**
	 * Convert the specified range of text into a list of lexemes
	 * 
	 * @param document
	 * @param range
	 * @param scanner
	 */
	public JSLexemeProvider(IDocument document, IRange range, ITokenScanner scanner)
	{
		super(document, range, scanner);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.LexemeProvider#addLexeme(com.aptana.parsing.lexer.Lexeme)
	 */
	@Override
	protected void addLexeme(Lexeme<JSTokenType> lexeme)
	{
		// don't add comments
		switch (lexeme.getType())
		{
			case SINGLELINE_COMMENT:
			case MULTILINE_COMMENT:
			case SDOC:
			case VSDOC:
				break;

			default:
				super.addLexeme(lexeme);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.LexemeProvider#getTypeFromData(java.lang.Object)
	 */
	@Override
	protected JSTokenType getTypeFromData(Object data)
	{
		return (data == null) ? JSTokenType.UNDEFINED : (JSTokenType) data;
	}
}
