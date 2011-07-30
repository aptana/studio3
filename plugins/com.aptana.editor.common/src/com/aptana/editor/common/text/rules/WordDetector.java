/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Uses the Java identifier comparison built in to Character class. This
 * typically means words start with letters,
 * underscore or currency symbols (like $). They can contain teh same characters
 * plus digits. An important character
 * that isn't allowed using this detector is the '-' hyphen.
 * 
 * @author Max Stepanov
 */
public class WordDetector implements IWordDetector {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c) {
		return Character.isJavaIdentifierPart(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c) {
		return Character.isJavaIdentifierStart(c);
	}

}
