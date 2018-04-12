/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.List;

import com.aptana.core.util.SourcePrinter;

public class SmartTypingPairsElement extends AbstractBundleElement
{
	private List<Character> _pairs;

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
	public List<Character> getPairs()
	{
		return _pairs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.AbstractElement#printBody(com.aptana.core.util.SourcePrinter)
	 */
	@Override
	protected void printBody(SourcePrinter printer, boolean includeBlocks)
	{
		// output path, scope and pairs
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this.getScope()); //$NON-NLS-1$
		String pairsValue = "null"; //$NON-NLS-1$
		if (this.getPairs() != null)
		{
			pairsValue = this.getPairs().toString();
		}
		printer.printWithIndent("pairs: ").println(pairsValue); //$NON-NLS-1$
	}

	/**
	 * Called by ruble API.
	 * 
	 * @param character
	 */
	public void addPairCharacter(String character)
	{
		if (_pairs == null)
		{
			_pairs = new ArrayList<Character>();
		}
		_pairs.add(Character.valueOf(character.charAt(0)));
	}

	/**
	 * Used for YAML deserialization...
	 * 
	 * @param pairs
	 */
	public void setPairs(List<Character> pairs)
	{
		this._pairs = pairs;
	}
}
