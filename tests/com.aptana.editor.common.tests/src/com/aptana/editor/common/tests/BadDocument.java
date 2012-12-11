/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.tests;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

/**
 * No matter what, throws a bad location when trying to get specific content
 * 
 * @author ingo
 */
public class BadDocument extends Document
{

	private boolean throwBadLocation = false;

	/**
	 * @return the throwBadLocation
	 */
	public boolean isThrowBadLocation()
	{
		return throwBadLocation;
	}

	/**
	 * @param throwBadLocation
	 *            the throwBadLocation to set
	 */
	public void setThrowBadLocation(boolean throwBadLocation)
	{
		this.throwBadLocation = throwBadLocation;
	}

	public BadDocument()
	{
		super();
	}

	public BadDocument(String initialContent)
	{
		super(initialContent);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.AbstractDocument#get(int, int)
	 */
	@Override
	public String get(int pos, int length) throws BadLocationException
	{
		if (throwBadLocation)
		{
			throw new BadLocationException();
		}
		return super.get(pos, length);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.AbstractDocument#getChar(int)
	 */
	@Override
	public char getChar(int pos) throws BadLocationException
	{
		if (throwBadLocation)
		{
			throw new BadLocationException();
		}
		return super.getChar(pos);
	}

}
