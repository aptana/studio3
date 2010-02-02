package com.aptana.editor.html.parsing;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.editor.html.parsing.lexer.HTMLTokens;

public class HTMLParserScanner extends Scanner
{

	private ITokenScanner fTokenScanner;
	private IDocument fDocument;

	private int fScannerIndex;

	public HTMLParserScanner()
	{
		fTokenScanner = new HTMLTokenScanner();
	}

	public IDocument getSource()
	{
		return fDocument;
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

		short type = HTMLTokens.EOF;
		if (data != null)
		{
			type = HTMLTokens.getToken(data.toString());
		}

		try
		{
			String text = fDocument.get(offset, length);
			if (fScannerIndex == 1)
			{
				type = HTMLTokens.STYLE;
				if (text.equals("</style>"))
				{
					fScannerIndex = 0;
					return nextToken();
				}
			}
			else if (fScannerIndex == 2)
			{
				type = HTMLTokens.SCRIPT;
				if (text.equals("</script>"))
				{
					fScannerIndex = 0;
					return nextToken();
				}
			}
			else if (type == HTMLTokens.STYLE)
			{
				fScannerIndex = 1;
				return nextToken();
			}
			else if (type == HTMLTokens.SCRIPT)
			{
				fScannerIndex = 2;
				return nextToken();
			}
			else if (type == HTMLTokens.START_TAG && text.endsWith("/>")) //$NON-NLS-1$
			{
				// self closing
				type = HTMLTokens.SELF_CLOSING;
			}

			return new Symbol(type, offset, offset + length - 1, text);
		}
		catch (BadLocationException e)
		{
			throw new Scanner.Exception(e.getLocalizedMessage());
		}
	}

	protected boolean isIgnored(IToken token)
	{
		// ignores whitespace
		if (token.isWhitespace())
		{
			return true;
		}
		Object data = token.getData();
		if (data == null)
		{
			return false;
		}
		// ignores comments and doctype declaration
		return data.equals(HTMLTokens.getTokenName(HTMLTokens.COMMENT))
				|| data.equals(HTMLTokens.getTokenName(HTMLTokens.DOCTYPE));
	}
}
