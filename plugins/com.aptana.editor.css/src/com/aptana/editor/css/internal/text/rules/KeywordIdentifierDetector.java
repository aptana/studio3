/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.internal.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Detects words consisting only of letters, digits, '-', and '_'. Must start with letter
 * 
 * @author Chris Williams
 */
public class KeywordIdentifierDetector implements IWordDetector
{

	public boolean isWordPart(char c)
	{
		return Character.isLetterOrDigit(c) || c == '-' || c == '_';
	}

	public boolean isWordStart(char c)
	{
		return isWordPart(c);
	}
}
