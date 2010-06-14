package com.aptana.editor.js.parsing;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
	private List<Symbol> fDocComments;
	
	/**
	 * JSScanner
	 */
	public JSScanner()
	{
		fTokenScanner = new JSTokenScanner();
		fDocComments = new LinkedList<Symbol>();
	}

	/**
	 * createSymbol
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	protected Symbol createSymbol(Object data) throws Exception
	{
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
	 * getDocComments
	 * 
	 * @return
	 */
	public List<Symbol> getDocComments()
	{
		return fDocComments;
	}
	
	/**
	 * isComment
	 * 
	 * @param data
	 * @return
	 */
	private boolean isComment(Object data)
	{
		boolean result = false;
		
		if (data != null)
		{
			JSTokenType type = (JSTokenType) data;
			
			switch (type)
			{
				case SINGLELINE_COMMENT:
				case MULTILINE_COMMENT:
				case DOC:
					result = true;
					break;
			}
		}
		
		return result;
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
		
		while (token.isWhitespace() || isComment(data))
		{
			// save jsdoc comments for later processing
			if (data != null && ((JSTokenType) data) == JSTokenType.DOC)
			{
				fDocComments.add(createSymbol(data));
			}
			
			// ignores whitespace and comments
			token = fTokenScanner.nextToken();
			data = token.getData();
		}

		return createSymbol(data);
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
