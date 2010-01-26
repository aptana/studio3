package com.aptana.editor.js.parsing;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.editor.js.parsing.lexer.JSTokens;

public class JSScanner extends Scanner
{

	private JSTokenScanner fTokenScanner;
	private IDocument fDocument;

	public JSScanner()
	{
		fTokenScanner = new JSTokenScanner();
	}

	public void setSource(String text)
	{
		setSource(new Document(text));
	}

	public void setSource(IDocument document)
	{
		fDocument = document;
		fTokenScanner.setRange(fDocument, 0, fDocument.getLength());
	}

	@Override
	public Symbol nextToken() throws IOException, Exception
	{
		IToken token = fTokenScanner.nextToken();
		Object data = token.getData();
		while (token.isWhitespace() || (data != null && isComment(data)))
		{
			// ignores whitespace and comments
			token = fTokenScanner.nextToken();
			data = token.getData();
		}

		int offset = fTokenScanner.getTokenOffset();
		int length = fTokenScanner.getTokenLength();

		short type = JSTokens.EOF;
		if (data != null)
		{
			type = JSTokens.getToken(data.toString());
		}
		try
		{
			return new Symbol(type, offset, offset + length - 1, fDocument.get(offset, length));
		}
		catch (BadLocationException e)
		{
			throw new Scanner.Exception(e.getLocalizedMessage());
		}
	}

	private static boolean isComment(Object data)
	{
		return data.equals(JSTokens.getTokenName(JSTokens.SINGLELINE_COMMENT))
				|| data.equals(JSTokens.getTokenName(JSTokens.MULTILINE_COMMENT))
				|| data.equals(JSTokens.getTokenName(JSTokens.DOC));
	}
}
