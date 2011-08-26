/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.outline;

/**
 * This class is a simple little stub to be inserted into the outline for status updates, placeholders, or
 * errors/warnings. Uses IStatus integer values for status severity to determine what icon to show.
 * 
 * @author Chris Williams
 */
class OutlinePlaceholderItem
{

	private String message;
	private int status;

	/**
	 * @param message
	 */
	OutlinePlaceholderItem(int status, String message)
	{
		super();
		this.status = status;
		this.message = message;
	}

	/**
	 * @return
	 */
	public int status()
	{
		return status;
	}

	@Override
	public String toString()
	{
		return message;
	}
}
