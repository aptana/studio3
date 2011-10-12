/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.outline;

import com.aptana.parsing.IParseState;

/**
 * A default implementation of IParseListener
 */
public class ParseAdapter implements IParseListener
{
	/* (non-Javadoc)
	 * @see com.aptana.editor.common.outline.IParseListener#afterParse(com.aptana.parsing.IParseState)
	 */
	public void afterParse(IParseState parseState)
	{
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.outline.IParseListener#beforeParse(com.aptana.parsing.IParseState)
	 */
	public void beforeParse(IParseState parseState)
	{
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.outline.IParseListener#parseCompletedSuccessfully()
	 */
	public void parseCompletedSuccessfully()
	{
	}
}
