package com.aptana.editor.xml.parsing;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.editor.xml.parsing.lexer.XMLToken;

public class XMLParserScanner extends Scanner
{

	private XMLTokenScanner fTokenScanner;
	private IDocument fDocument;

	public XMLParserScanner()
	{
		fTokenScanner = new XMLTokenScanner();
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
		while (isIgnored(token))
		{
			token = fTokenScanner.nextToken();
			data = token.getData();
		}

		int offset = fTokenScanner.getTokenOffset();
		int length = fTokenScanner.getTokenLength();

		short type = XMLToken.EOF.getIndex();
		if (data instanceof XMLToken)
		{
			type = ((XMLToken) data).getIndex();
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

	private boolean isIgnored(IToken token)
	{
		// ignores the whitespace and comments by default
		if (token.isWhitespace())
		{
			return true;
		}
		Object data = token.getData();
		if (data == null)
		{
			return false;
		}
		return data.equals(XMLToken.COMMENT);
	}
}
