/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.lexer;

import beaver.Scanner;

public class SyntaxError extends Scanner.Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4767964438540532379L;

	public SyntaxError(String msg)
	{
		super(msg);
	}
}
