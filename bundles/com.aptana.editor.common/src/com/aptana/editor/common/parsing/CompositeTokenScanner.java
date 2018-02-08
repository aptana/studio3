/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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

	public ITokenScanner getPrimaryTokenScanner()
	{
		return fPrimaryTokenScanner;
	}

	public int getTokenLength()
	{
		return fPrimaryTokenScanner.getTokenLength();
	}

	public int getTokenOffset()
	{
		return fPrimaryTokenScanner.getTokenOffset();
	}

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
					resetIndex();
				}
			}
		}
		return token;
	}

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

	private void resetIndex()
	{
		fCurrentIndex = DEFAULT_INDEX;
	}

	protected void reset()
	{
		resetIndex();
	}
}
