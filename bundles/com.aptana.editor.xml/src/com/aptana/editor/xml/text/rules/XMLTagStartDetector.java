/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author klindsey
 *
 */
public class XMLTagStartDetector implements IWordDetector
{
	private int _index;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c)
	{
		this._index = 0;

		return c == '<';
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c)
	{
		this._index++;

		return (this._index == 1 && c == '/');
	}

}
