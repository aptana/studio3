/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;


public class AnalyticsInfo
{

	private final String appName;
	private final String appId;
	private final String appGuid;
	private final String versionPluginId;
	private final String userAgent;
	private final IAnalyticsUserManager userManager;

	public AnalyticsInfo(String appId, String appName, String appGuid, String versionPluginId, String userAgent,
			IAnalyticsUserManager userManager)
	{
		this.appId = appId;
		this.appName = appName;
		this.appGuid = appGuid;
		this.versionPluginId = versionPluginId;
		this.userAgent = userAgent;
		this.userManager = userManager;
	}

	/**
	 * @return the name of the app used for sending analytics
	 */
	public String getAppName()
	{
		return appName;
	}

	/**
	 * @return the id of the app used for sending analytics
	 */
	public String getAppId()
	{
		return appId;
	}

	/**
	 * @return the guid associated with the app
	 */
	public String getAppGuid()
	{
		return appGuid;
	}

	/**
	 * @return the id of the plugin we use to retrieve the product version
	 */
	public String getVersionPluginId()
	{
		return versionPluginId;
	}

	public String getUserAgent()
	{
		return userAgent;
	}

	public IAnalyticsUserManager getUserManager()
	{
		return userManager;
	}
}
