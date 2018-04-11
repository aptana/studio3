/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.Collections;
import java.util.List;

public class BrowserUtilNull implements IBrowserUtil
{

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.util.IBrowserUtil#discoverInstalledBrowsers()
	 */
	public List<BrowserInfo> discoverInstalledBrowsers()
	{
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.util.IBrowserUtil#getBrowserVersion(com.aptana.core.util.IBrowserUtil.BrowserInfo)
	 */
	public String getBrowserVersion(BrowserInfo info)
	{
		return null;
	}

}
