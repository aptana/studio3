/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io;

public class PermissionDeniedException extends Exception {

	private static final long serialVersionUID = -443040597160397837L; // $codepro.audit.disable hidingInheritedFields

	/**
	 * Create a new instance of PermissionDeniedException
	 * 
	 * @param message
	 *            The message associated with this exception
	 * @param e
	 *            The inner exception that caused this exception
	 */
	public PermissionDeniedException(String message, Exception e) {
		super(message, e);
	}

}
