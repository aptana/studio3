package com.aptana.editor.js.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;

import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.parsing.lexer.IRange;

public class JSLexemeProvider extends LexemeProvider<JSTokenType>
{
	/**
	 * Convert the partition that contains the given offset into a list of
	 * lexemes.
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
	 * @see com.aptana.editor.common.contentassist.LexemeProvider#getTypeFromName(java.lang.String)
	 */
	@Override
	protected JSTokenType getTypeFromName(String name)
	{
		return JSTokenType.get(name);
	}
}
