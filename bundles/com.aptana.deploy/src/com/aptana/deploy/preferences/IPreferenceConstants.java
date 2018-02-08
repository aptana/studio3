/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.preferences;

public interface IPreferenceConstants
{
	public enum DeployType
	{
		HEROKU, FTP, CAPISTRANO, ENGINEYARD, RED_HAT, NONE;
	}

	public static final String PROJECT_DEPLOY_TYPE = "ProjectDeployType"; //$NON-NLS-1$
	public static final String PROJECT_DEPLOY_ENDPOINT = "ProjectDeployEndpoint"; //$NON-NLS-1$
}
