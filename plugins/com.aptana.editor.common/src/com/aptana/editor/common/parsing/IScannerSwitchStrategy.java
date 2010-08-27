package com.aptana.editor.common.parsing;

public interface IScannerSwitchStrategy
{
	public String[] getEnterTokens();

	public String[] getExitTokens();
}
