/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.sdoc.parsing;

import org.eclipse.jface.text.rules.IWordDetector;

class WhitespaceDetector implements IWordDetector
{
	public boolean isWordPart(char c)
	{
		return c == ' ' || c == '\t';
	}

	public boolean isWordStart(char c)
	{
		return this.isWordPart(c);
	}
}
