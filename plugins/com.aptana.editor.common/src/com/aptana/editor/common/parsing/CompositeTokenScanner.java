package com.aptana.editor.common.parsing;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

public class CompositeTokenScanner implements ITokenScanner
{

	private static final int DEFAULT_INDEX = -1;

	private ITokenScanner fPrimaryTokenScanner;
	private IScannerSwitchStrategy[] fSwitchStrategies;

	private int fCurrentIndex;

	public CompositeTokenScanner(ITokenScanner primaryTokenScanner, IScannerSwitchStrategy[] switchStrategies)
	{
		fPrimaryTokenScanner = primaryTokenScanner;
		fSwitchStrategies = switchStrategies;
		fCurrentIndex = DEFAULT_INDEX;
	}

	public IScannerSwitchStrategy getCurrentSwitchStrategy()
	{
		if (fCurrentIndex == DEFAULT_INDEX)
		{
			return null;
		}
		return fSwitchStrategies[fCurrentIndex];
	}

	@Override
	public int getTokenLength()
	{
		return fPrimaryTokenScanner.getTokenLength();
	}

	@Override
	public int getTokenOffset()
	{
		return fPrimaryTokenScanner.getTokenOffset();
	}

	@Override
	public IToken nextToken()
	{
		IToken token = fPrimaryTokenScanner.nextToken();
		Object data = token.getData();

		if (data != null)
		{
			if (fCurrentIndex == DEFAULT_INDEX)
			{
				fCurrentIndex = getScannerIndex(data.toString());
			}
			else
			{
				if (hasExitToken(data.toString()))
				{
					// exits out to the top level
					reset();
				}
			}
		}
		return token;
	}

	@Override
	public void setRange(IDocument document, int offset, int length)
	{
		fPrimaryTokenScanner.setRange(document, offset, length);
	}

	protected IScannerSwitchStrategy[] getSwitchStrategies()
	{
		return fSwitchStrategies;
	}

	protected void setSwitchStrategies(IScannerSwitchStrategy[] strategies)
	{
		fSwitchStrategies = strategies;
	}

	protected void setPrimaryTokenScanner(ITokenScanner scanner)
	{
		fPrimaryTokenScanner = scanner;
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

	private boolean hasExitToken(String data)
	{
		String[] tokens = fSwitchStrategies[fCurrentIndex].getExitTokens();
		for (String token : tokens)
		{
			if (data.equals(token))
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
