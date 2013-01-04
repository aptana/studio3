/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.github;

import org.eclipse.core.runtime.IStatus;

public interface IGithubManager
{

	public IGithubUser getUser();

	public IStatus login(String username, String password);

	public IStatus logout();

}
