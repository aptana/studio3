/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

public interface IAnalyticsUser
{

	public String getUsername();

	public String getSessionID();

	public String getCookie();

	public boolean isOnline();

	public String getGUID();
}
