/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     xored software, Inc. - initial API and Implementation (Yuri Strot) 
 *******************************************************************************/
package com.aptana.formatter.preferences.profile;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user-defined profile. A custom profile can be modified after instantiation.
 */
public class CustomProfile extends Profile implements IProfile.ICustomProfile
{
	String fName;
	private Map<String, String> fSettings;
	private int fVersion;

	public CustomProfile(String name, Map<String, String> settings, int version)
	{
		fName = name;
		fSettings = settings;
		fVersion = version;
	}

	public String getName()
	{
		return fName;
	}

	public Map<String, String> getSettings()
	{
		return new HashMap<String, String>(fSettings);
	}

	public void setSettings(Map<String, String> settings)
	{
		if (settings == null)
		{
			throw new IllegalArgumentException();
		}
		fSettings = settings;
	}

	public String getID()
	{
		return fName;
	}

	public int getVersion()
	{
		return fVersion;
	}

	public void setVersion(int version)
	{
		fVersion = version;
	}

	public int compareTo(IProfile o)
	{
		if (o instanceof CustomProfile)
		{
			return getName().compareToIgnoreCase(o.getName());
		}
		return 1;
	}

	public boolean isProfileToSave()
	{
		return true;
	}
}
