/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

/**
 * @author Kevin Lindsey
 */
public class ScriptDocInitializationException extends Exception
{
	private static final long serialVersionUID = 8223887793849150985L; // $codepro.audit.disable hidingInheritedFields

	/**
	 * Create a new instance of DocumentationInitializationException
	 * 
	 * @param string
	 *            The message associated with this exception
	 * @param e
	 *            The inner exception that caused this exception
	 */
	public ScriptDocInitializationException(String string, Exception e)
	{
		super(string, e);
	}
}
