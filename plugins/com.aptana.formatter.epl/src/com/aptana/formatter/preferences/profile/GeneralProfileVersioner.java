/******************************************************************************* 
 * Copyright (c) 2008 xored software, Inc.  
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 *     xored software, Inc. - initial API and Implementation (Yuri Strot) 
 *******************************************************************************/
package com.aptana.formatter.preferences.profile;

import com.aptana.formatter.IContributedExtension;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;

/**
 * Default implementation of the <code>IProfileVersioner</code>
 */
public class GeneralProfileVersioner implements IProfileVersioner
{

	private static final int FIRST_VERSION = 1;
	private static final int CURRENT_VERSION = 3;
	private String formatter;

	public GeneralProfileVersioner(String formatter)
	{
		this.formatter = formatter;
	}

	public int getCurrentVersion()
	{
		return CURRENT_VERSION;
	}

	public int getFirstVersion()
	{
		return FIRST_VERSION;
	}

	public String getFormatterId()
	{
		return formatter;
	}

	public void update(IProfile profile)
	{
		if (profile instanceof CustomProfile)
		{
			CustomProfile customProfile = (CustomProfile) profile;
			if (customProfile.getVersion() != CURRENT_VERSION)
			{
				// Migrate any tab-spacing setting to the default one.
				IContributedExtension[] extensions = ScriptFormatterManager.getInstance().getAllContributions(true);
				for (IContributedExtension extension : extensions)
				{
					if (extension instanceof IScriptFormatterFactory)
					{
						// Each factory is aware of the internals that needs to be updated, so
						// we let the factory do the update.
						((IScriptFormatterFactory) extension).updateProfile(profile);
					}
				}
				// Update the current profile with the new version.
				customProfile.setVersion(CURRENT_VERSION);
			}

		}
	}
}
