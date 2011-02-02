/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.sax;

/**
 * @author Kevin Lindsey
 */
public class InvalidTransitionException extends Exception
{

	/*
	 * Fields
	 */
	private static final long serialVersionUID = 549557395315065491L;

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of InvalidTransitionException
	 * 
	 * @param message
	 *            The error message associated with this exception
	 */
	public InvalidTransitionException(String message)
	{
		super(message);
	}
}
