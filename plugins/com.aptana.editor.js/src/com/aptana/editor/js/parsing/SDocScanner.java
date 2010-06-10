package com.aptana.editor.js.parsing;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.editor.js.parsing.lexer.SDocTokenType;

public class SDocScanner extends Scanner
{
	private SDocTokenScanner fTokenScanner;
	private IDocument fDocument;
	
	/**
	 * SDocScanner
	 */
	public SDocScanner()
	{
		fTokenScanner = new SDocTokenScanner();
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
		
		while (token.isWhitespace())
		{
			token = fTokenScanner.nextToken();
			data = token.getData();
		}
		
		int offset = fTokenScanner.getTokenOffset();
		int length = fTokenScanner.getTokenLength();
		SDocTokenType type = (data == null) ? SDocTokenType.EOF : (SDocTokenType) data;
		
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
	 * setSource
	 * 
	 * @param document
	 */
	public void setSource(IDocument document)
	{
		fDocument = document;
		fTokenScanner.setRange(fDocument, 0, fDocument.getLength());
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
}
