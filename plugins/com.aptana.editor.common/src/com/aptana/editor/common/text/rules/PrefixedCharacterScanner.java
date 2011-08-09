/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;

/**
 * @author Max Stepanov
 *
 */
public class PrefixedCharacterScanner implements ICharacterScanner {

	private final char[] prefix;
	private final ICharacterScanner baseCharacterScanner;
	private int index;
	
	/**
	 * 
	 */
	public PrefixedCharacterScanner(String prefix, ICharacterScanner baseCharacterScanner) {
		this.prefix = prefix.toCharArray();
		this.baseCharacterScanner = baseCharacterScanner;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#getLegalLineDelimiters()
	 */
	public char[][] getLegalLineDelimiters() {
		return baseCharacterScanner.getLegalLineDelimiters();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#getColumn()
	 */
	public int getColumn() {
		throw new UnsupportedOperationException("unsupporthed method"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#read()
	 */
	public int read() {
		if (index < prefix.length) {
			return prefix[index++];
		}
		++index;
		return baseCharacterScanner.read();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#unread()
	 */
	public void unread() {
		if (--index > prefix.length) {
			baseCharacterScanner.unread();
		}
	}

}
