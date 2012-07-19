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
