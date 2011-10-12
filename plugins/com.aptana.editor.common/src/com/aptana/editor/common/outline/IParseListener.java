/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.outline;

import com.aptana.parsing.IParseState;

public interface IParseListener
{
	/**
	 * An event indicating that parsing has completed. Any setting that may need to be reversed can be done here. This
	 * even will fire if the parse is successful or not
	 * 
	 * @param parseState
	 */
	public void afterParse(IParseState parseState);

	/**
	 * An event indicating that a parse is about to be initiated. This can be used to configure the parse state before a
	 * parse occurs
	 */
	public void beforeParse(IParseState parseState);

	/**
	 * An event indicating that parsing has completed successfully. This event can be used to further process the
	 * results of the parse.
	 */
	public void parseCompletedSuccessfully();
}
