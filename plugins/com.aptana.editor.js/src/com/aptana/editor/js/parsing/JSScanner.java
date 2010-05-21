package com.aptana.editor.js.parsing;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.editor.js.parsing.lexer.JSTokenType;

public class JSScanner extends Scanner
{
	private JSTokenScanner fTokenScanner;
	private IDocument fDocument;

	/**
	 * JSScanner
	 */
	public JSScanner()
	{
		fTokenScanner = new JSTokenScanner();
	}

	/**
	 * setSource
	 * 
	 * @param text
	 */
	public void setSource(String text)
	{
		setSource(new Document(text));
	}

	/**
	 * setSource
	 * 
	 * @param document
	 */
	public void setSource(IDocument document)
	{
		fDocument = document;
		fTokenScanner.setRange(fDocument, 0, fDocument.getLength());
	}

	/*
	 * (non-Javadoc)
	 * @see beaver.Scanner#nextToken()
	 */
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
		JSTokenType type = (data == null) ? JSTokenType.EOF : (JSTokenType) data;
		
		try
		{
			int totalLength = fDocument.getLength();
			
			if (offset > totalLength)
			{
				offset = totalLength;
			}
			if (length == -1)
			{
				length = 0;
			}
			
			return new Symbol(type.getIndex(), offset, offset + length - 1, fDocument.get(offset, length));
		}
		catch (BadLocationException e)
		{
			throw new Scanner.Exception(e.getLocalizedMessage());
		}
	}

	/**
	 * isComment
	 * 
	 * @param data
	 * @return
	 */
	private static boolean isComment(Object data)
	{
		JSTokenType type = (JSTokenType) data;
		boolean result = false;
		
		switch (type)
		{
			case SINGLELINE_COMMENT:
			case MULTILINE_COMMENT:
			case DOC:
				result = true;
				break;
		}
		
		return result;
	}
}
