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

import java.util.Collections;
import java.util.Map;


/**
 * Represents a built-in profile. The state of a built-in profile cannot be changed after instantiation.
 */
public class BuiltInProfile extends Profile
{

	public BuiltInProfile(String ID, String name, Map<String, String> settings, int order, String formatter,
			int currentVersion)
	{
		fName = name;
		fID = ID;
		fSettings = Collections.unmodifiableMap(settings);
		fOrder = order;
		fFormatter = formatter;
		fCurrentVersion = currentVersion;
	}

	public String getName()
	{
		return fName;
	}

	public Map<String, String> getSettings()
	{
		return fSettings;
	}

	public void setSettings(Map<String, String> settings)
	{
	}

	public String getID()
	{
		return fID;
	}

	public final int compareTo(IProfile o)
	{
		if (o instanceof BuiltInProfile)
		{
			return fOrder - ((BuiltInProfile) o).fOrder;
		}
		return -1;
	}

	public boolean isProfileToSave()
	{
		return false;
	}

	@Override
	public boolean isBuiltInProfile()
	{
		return true;
	}

	public int getVersion()
	{
		return fCurrentVersion;
	}

	public String getFormatterId()
	{
		return fFormatter;
	}

	private final String fName;
	private final String fID;
	private final Map<String, String> fSettings;
	private final int fOrder;
	private final int fCurrentVersion;
	private final String fFormatter;

}
