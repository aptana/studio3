/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

/**
 * This always returns a single token spanning the entire range it was set for.
 * 
 * @author cwilliams
 */
public class SingleTokenScanner implements ITokenScanner
{

	private IToken fToken;
	private int fOffset;
	private int fLength;

	public SingleTokenScanner(IToken token)
	{
		this.fToken = token;
	}

	public void setRange(IDocument document, int offset, int length)
	{
		this.fOffset = offset;
		this.fLength = length;
	}

	public IToken nextToken()
	{
		return fToken;
	}

	public int getTokenOffset()
	{
		return fOffset;
	}

	public int getTokenLength()
	{
		return fLength;
	}

}
