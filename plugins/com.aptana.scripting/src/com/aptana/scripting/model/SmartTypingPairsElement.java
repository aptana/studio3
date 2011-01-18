/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import com.aptana.core.util.SourcePrinter;

public class SmartTypingPairsElement extends AbstractBundleElement
{
	private static final char[] NO_CHARS = new char[0];
	
	private char[] _pairs;

	/**
	 * SmartTypingPairsElement
	 * 
	 * @param path
	 */
	public SmartTypingPairsElement(String path)
	{
		super(path);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.AbstractElement#getElementName()
	 */
	@Override
	protected String getElementName()
	{
		return "smart_typing_pairs"; //$NON-NLS-1$
	}

	/**
	 * getPairs
	 * 
	 * @return
	 */
	public char[] getPairs()
	{
		return _pairs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.AbstractElement#printBody(com.aptana.core.util.SourcePrinter)
	 */
	@Override
	protected void printBody(SourcePrinter printer)
	{
		// output path, scope and pairs
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this.getScope()); //$NON-NLS-1$
		printer.printWithIndent("pairs: ").println(this.getPairs().toString()); //$NON-NLS-1$
	}

	/**
	 * setPairs
	 * 
	 * @param pairs
	 */
	public void setPairs(String[] pairs)
	{
		if (pairs != null && pairs.length > 0)
		{
			this._pairs = new char[pairs.length];
			
			for (int i = 0; i < pairs.length; i++)
			{
				this._pairs[i] = pairs[i].charAt(0);
			}
		}
		else
		{
			this._pairs = NO_CHARS;
		}
	}
}
