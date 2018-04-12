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

import com.aptana.editor.common.text.rules.QueuedTokenScanner;

/**
 * This always returns a single token spanning the entire range it was set for.
 * 
 * @author cwilliams
 */
public class SingleTokenScanner extends QueuedTokenScanner
{

	private IToken fToken;

	public SingleTokenScanner(IToken token)
	{
		this.fToken = token;
	}

	public void setRange(IDocument document, int offset, int length)
	{
		super.setRange(document, offset, length);
		queueToken(fToken, offset, length);
	}
}
