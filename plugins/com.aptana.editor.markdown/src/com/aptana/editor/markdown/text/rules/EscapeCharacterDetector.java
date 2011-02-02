/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.markdown.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

class EscapeCharacterDetector implements IWordDetector
{
	private boolean toggle = false;

	public boolean isWordStart(char c)
	{
		if (c == '\\')
		{
			toggle = true;
			return true;
		}
		return false;
	}

	public boolean isWordPart(char c)
	{
		if (toggle)
		{
			toggle = false;
			return true;
		}
		return false;
	}
}