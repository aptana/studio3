/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.util;

import org.eclipse.core.resources.IContainer;

import com.aptana.deploy.IDeployProvider;
import com.aptana.deploy.internal.DeployProviderRegistry;

public class DeployProviderUtil
{

	public static IDeployProvider getDeployProvider(IContainer container)
	{
		return DeployProviderRegistry.getInstance().getProvider(container);
	}

	public static String getIdForProvider(IDeployProvider provider)
	{
		return DeployProviderRegistry.getInstance().getIdForProvider(provider);
	}
}
