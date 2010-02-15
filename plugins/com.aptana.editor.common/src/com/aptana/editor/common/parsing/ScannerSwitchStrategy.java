package com.aptana.editor.common.parsing;

public class ScannerSwitchStrategy implements IScannerSwitchStrategy
{

	private String[] fEnterTokens;
	private String[] fExitTokens;

	public ScannerSwitchStrategy(String[] enterTokens, String[] exitTokens)
	{
		fEnterTokens = enterTokens;
		fExitTokens = exitTokens;
	}

	@Override
	public String[] getEnterTokens()
	{
		return fEnterTokens;
	}

	@Override
	public String[] getExitTokens()
	{
		return fExitTokens;
	}
}
