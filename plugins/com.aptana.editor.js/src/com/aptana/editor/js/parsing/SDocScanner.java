package com.aptana.editor.js.parsing;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.editor.js.parsing.lexer.SDocTokenType;

public class SDocScanner extends Scanner
{
	private SDocTokenScanner fTokenScanner;
	private SDocTypeTokenScanner fTypeTokenScanner;
	private IDocument fDocument;
	private List<Symbol> fQueue;

	/**
	 * SDocScanner
	 */
	public SDocScanner()
	{
		fTokenScanner = new SDocTokenScanner();
		fQueue = new LinkedList<Symbol>();
	}

	/*
	 * (non-Javadoc)
	 * @see beaver.Scanner#nextToken()
	 */
	@Override
	public Symbol nextToken() throws IOException, Exception
	{
		Symbol result;
		
		if (fQueue.size() > 0)
		{
			result = fQueue.remove(0);
		}
		else
		{
			IToken token = fTokenScanner.nextToken();
			Object data = token.getData();
	
			while (data == SDocTokenType.WHITESPACE)
			{
				token = fTokenScanner.nextToken();
				data = token.getData();
			}
	
			int offset = fTokenScanner.getTokenOffset();
			int length = fTokenScanner.getTokenLength();
			SDocTokenType type = (data == null) ? SDocTokenType.EOF : (SDocTokenType) data;
	
			if (type == SDocTokenType.TYPES)
			{
				this.queueTypeTokens(offset, length);
	
				result = fQueue.remove(0);
			}
			else
			{
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
	
					result = new Symbol(type.getIndex(), offset, offset + length - 1, fDocument.get(offset, length));
				}
				catch (BadLocationException e)
				{
					throw new Scanner.Exception(e.getLocalizedMessage());
				}
			}
		}
		
		return result;
	}

	/**
	 * queueTypeTokens
	 * 
	 * @param typesOffset
	 * @param typesLength
	 * @throws Exception
	 */
	protected void queueTypeTokens(int typesOffset, int typesLength) throws Exception
	{
		fTypeTokenScanner.setRange(fDocument, typesOffset, typesLength);
		IToken token = fTypeTokenScanner.nextToken();

		while (token != Token.EOF)
		{
			int offset = fTypeTokenScanner.getTokenOffset();
			int length = fTypeTokenScanner.getTokenLength();
			Object data = token.getData();
			SDocTokenType type = (data == null) ? SDocTokenType.EOF : (SDocTokenType) data;
			
			try
			{
				Symbol symbol = new Symbol(type.getIndex(), offset, offset + length - 1, fDocument.get(offset, length));

				fQueue.add(symbol);
			}
			catch (BadLocationException e)
			{
				throw new Scanner.Exception(e.getLocalizedMessage());
			}
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
