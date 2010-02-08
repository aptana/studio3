package com.aptana.editor.common.parsing;

public class ScannerSwitchStrategy implements IScannerSwitchStrategy
{

	private String[] fEnterTokens;
	private String[] fExitSequences;

	public ScannerSwitchStrategy(String[] enterTokens, String[] exitSequences)
	{
		fEnterTokens = enterTokens;
		fExitSequences = exitSequences;
	}

	@Override
	public String[] getEnterTokens()
	{
		return fEnterTokens;
	}

	@Override
	public String[] getExitSequences()
	{
		return fExitSequences;
	}
}
