/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;

/**
 * @author Max Stepanov
 */
public class CollectingCharacterScanner implements ICharacterScanner
{

	private final ICharacterScanner baseCharacterScanner;
	private final StringBuilder contents;

	/**
	 * 
	 */
	public CollectingCharacterScanner(ICharacterScanner baseCharacterScanner, String contents)
	{
		this.baseCharacterScanner = baseCharacterScanner;
		this.contents = new StringBuilder(contents);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#getLegalLineDelimiters()
	 */
	public char[][] getLegalLineDelimiters()
	{
		return baseCharacterScanner.getLegalLineDelimiters();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#getColumn()
	 */
	public int getColumn()
	{
		return baseCharacterScanner.getColumn();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#read()
	 */
	public int read()
	{
		int c = baseCharacterScanner.read();
		if (c != ICharacterScanner.EOF)
		{
			contents.append((char) c);
		}
		return c;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#unread()
	 */
	public void unread()
	{
		baseCharacterScanner.unread();
		contents.setLength(contents.length() - 1);
	}

	/**
	 * @return the stringBuilder
	 */
	public String getContents()
	{
		return contents.toString();
	}

}
