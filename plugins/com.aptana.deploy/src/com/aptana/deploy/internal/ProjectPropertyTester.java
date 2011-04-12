/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.aptana.deploy.IDeployProvider;
import com.aptana.deploy.preferences.DeployPreferenceUtil;

public class ProjectPropertyTester extends PropertyTester
{

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		if (receiver instanceof IResource)
		{
			IProject project = ((IResource) receiver).getProject();
			if (!project.isAccessible())
			{
				return false;
			}
			if ("isDeployable".equals(property)) //$NON-NLS-1$
			{
				// Check if we have an explicitly set deployment provider
				String id = DeployPreferenceUtil.getDeployProviderId(project);
				if (id != null)
				{
					return true;
				}
				return DeployProviderRegistry.getInstance().getProvider(project) != null;
			}
			else if ("isDeployType".equals(property)) //$NON-NLS-1$
			{
				String id = DeployPreferenceUtil.getDeployProviderId(project);
				String arg = (String) expectedValue;
				if (id != null)
				{
					return arg.equals(id);
				}
				// Instantiate provider with id, then call handles and check that!
				IDeployProvider provider = DeployProviderRegistry.getInstance().getProviderById(arg);
				return provider != null && provider.handles(project);
			}
		}
		return false;
	}
}
