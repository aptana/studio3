/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.preferences;

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.deploy.Activator;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;

public class DeployPreferenceUtil
{

	public static DeployType getDeployType(IProject project)
	{
		String type = Platform.getPreferencesService().getString(Activator.getPluginIdentifier(),
				MessageFormat.format("{0}:{1}", IPreferenceConstants.PROJECT_DEPLOY_TYPE, project.getName()), null, //$NON-NLS-1$
				null);
		if (type != null)
		{
			if (type.equals(DeployType.HEROKU.toString()))
			{
				return DeployType.HEROKU;
			}
			if (type.equals(DeployType.FTP.toString()))
			{
				return DeployType.FTP;
			}
			if (type.equals(DeployType.CAPISTRANO.toString()))
			{
				return DeployType.CAPISTRANO;
			}
			if (type.equals(DeployType.ENGINEYARD.toString()))
			{
				return DeployType.ENGINEYARD;
			}
		}
		return null;
	}

	public static String getDeployEndpoint(IProject project)
	{
		return Platform.getPreferencesService().getString(Activator.getPluginIdentifier(),
				MessageFormat.format("{0}:{1}", //$NON-NLS-1$
						com.aptana.deploy.preferences.IPreferenceConstants.PROJECT_DEPLOY_ENDPOINT, project.getName()),
				null, null);
	}

	public static void setDeployType(IProject project, DeployType type)
	{
		IEclipsePreferences prefs = (new InstanceScope()).getNode(Activator.getPluginIdentifier());
		prefs.put(MessageFormat.format("{0}:{1}", IPreferenceConstants.PROJECT_DEPLOY_TYPE, project.getName()), //$NON-NLS-1$
				type.toString());
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
	}

	public static void setDeployEndpoint(IProject project, String endpoint)
	{
		IEclipsePreferences prefs = (new InstanceScope()).getNode(Activator.getPluginIdentifier());
		prefs.put(MessageFormat.format("{0}:{1}", IPreferenceConstants.PROJECT_DEPLOY_ENDPOINT, project.getName()), //$NON-NLS-1$
				endpoint);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
	}
}
