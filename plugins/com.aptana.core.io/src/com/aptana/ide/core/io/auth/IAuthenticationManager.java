/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io.auth;

/**
 * @author Max Stepanov
 */
public interface IAuthenticationManager
{

	public void setPassword(String authId, char[] password, boolean persistent);

	public boolean hasPersistent(String authId);

	public char[] getPassword(String authId);

	public char[] promptPassword(String authId, String login, String title, String message);

}
