/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * A key word detector.
 */
public class TagNameWordDetector implements IWordDetector
{

	/*
	 * (non-Javadoc) Method declared on IWordDetector
	 */
	public boolean isWordPart(char c)
	{
		return Character.isLetterOrDigit(c);
	}

	/*
	 * (non-Javadoc) Method declared on IWordDetector
	 */
	public boolean isWordStart(char c)
	{
		return Character.isLetter(c);
	}
}