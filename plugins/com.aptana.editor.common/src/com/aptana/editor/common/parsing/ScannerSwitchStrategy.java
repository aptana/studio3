/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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

	public String[] getEnterTokens()
	{
		return fEnterTokens;
	}

	public String[] getExitTokens()
	{
		return fExitTokens;
	}
}
