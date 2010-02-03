package com.aptana.editor.common.parsing;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

import beaver.Scanner;
import beaver.Symbol;

public class CompositeParserScanner extends Scanner
{

	protected static int DEFAULT_INDEX = -1;

	private ITokenScanner fPrimaryTokenScanner;
	private IDocument fDocument;
	private IScannerSwitchStrategy[] fSwitchStrategies;

	private int fCurrentIndex;

	public CompositeParserScanner(ITokenScanner primaryTokenScanner, IScannerSwitchStrategy[] switchStrategies)
	{
		fPrimaryTokenScanner = primaryTokenScanner;
		fSwitchStrategies = switchStrategies;
		fCurrentIndex = DEFAULT_INDEX;
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
		fCurrentIndex = DEFAULT_INDEX;
		fPrimaryTokenScanner.setRange(fDocument, 0, fDocument.getLength());
	}

	@Override
	public Symbol nextToken() throws IOException, Exception
	{
		IToken token = fPrimaryTokenScanner.nextToken();
		Object data = token.getData();
		while (isIgnored(token))
		{
			token = fPrimaryTokenScanner.nextToken();
			data = token.getData();
		}

		int offset = fPrimaryTokenScanner.getTokenOffset();
		int length = fPrimaryTokenScanner.getTokenLength();

		try
		{
			String text = fDocument.get(offset, length);
			if (data != null)
			{
				if (fCurrentIndex == DEFAULT_INDEX)
				{
					fCurrentIndex = getScannerIndex(data.toString());
					if (fCurrentIndex != DEFAULT_INDEX)
					{
						// has switched to a nested language
						return nextToken();
					}
				}
				else
				{
					if (hasExitSequence(text))
					{
						// exits out to the top level
						reset();
						return nextToken();
					}
				}
			}

			return createSymbol(offset, offset + length - 1, text, token);
		}
		catch (BadLocationException e)
		{
			throw new Scanner.Exception(e.getLocalizedMessage());
		}
	}

	protected Symbol createSymbol(int start, int end, String text, IToken token)
	{
		return new Symbol((short) 0, start, end, text);
	}

	protected boolean isIgnored(IToken token)
	{
		// by default ignores whitespace
		return token.isWhitespace();
	}

	protected int getCurrentStrategyIndex()
	{
		return fCurrentIndex;
	}

	private int getScannerIndex(String data)
	{
		String[] tokens;
		for (int i = 0; i < fSwitchStrategies.length; ++i)
		{
			tokens = fSwitchStrategies[i].getEnterTokens();
			for (String token : tokens)
			{
				if (data.equals(token))
				{
					return i;
				}
			}
		}
		return DEFAULT_INDEX;
	}

	private boolean hasExitSequence(String text)
	{
		String[] sequences = fSwitchStrategies[fCurrentIndex].getExitSequences();
		for (String sequence : sequences)
		{
			if (text.equals(sequence))
			{
				return true;
			}
		}
		return false;
	}

	private void reset()
	{
		fCurrentIndex = DEFAULT_INDEX;
	}
}
