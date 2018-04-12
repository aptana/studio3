/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.parsing;

import beaver.Scanner;

/**
 * Exception used to force the token return.
 */
public class ForceReturnException extends Scanner.Exception
{

	private static final long serialVersionUID = 1L;

	/**
	 * May be used to force the returned token type.
	 */
	public final Object type;

	public ForceReturnException(int line, int column, String msg, Object type)
	{
		super(line, column, msg);
		this.type = type;
	}

}
