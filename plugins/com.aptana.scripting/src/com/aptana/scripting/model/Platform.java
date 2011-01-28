/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Platform
{
	// NOTE: LINUX needs to be defined after UNIX so it will map to the OS_LINUX constant.
	// In other words, it has higher specificity than UNIX

	UNDEFINED("undefined"), //$NON-NLS-1$
	ALL("all"), //$NON-NLS-1$
	LINUX("linux", org.eclipse.core.runtime.Platform.OS_LINUX), //$NON-NLS-1$
	MAC("mac", org.eclipse.core.runtime.Platform.OS_MACOSX), //$NON-NLS-1$
	WIN("windows", org.eclipse.core.runtime.Platform.OS_WIN32), //$NON-NLS-1$
	UNIX("unix", org.eclipse.core.runtime.Platform.OS_AIX, org.eclipse.core.runtime.Platform.OS_HPUX, //$NON-NLS-1$
			org.eclipse.core.runtime.Platform.OS_LINUX, org.eclipse.core.runtime.Platform.OS_SOLARIS);

	private static Platform[] NO_PLATFORMS = new Platform[0];
	private static Map<String, Platform> NAME_MAP;
	private static Map<String, List<Platform>> PLATFORM_MAP;
	private String _name;
	private String[] _platforms;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, Platform>();
		PLATFORM_MAP = new HashMap<String, List<Platform>>();

		for (Platform type : EnumSet.allOf(Platform.class))
		{
			// add entry for name-based lookup
			NAME_MAP.put(type.getName(), type);

			// add entry for OS-based lookup
			String[] oses = type.getPlatforms();

			if (oses != null && oses.length > 0)
			{
				for (String os : oses)
				{
					if (PLATFORM_MAP.containsKey(os) == false)
					{
						PLATFORM_MAP.put(os, new ArrayList<Platform>());
					}
					
					List<Platform> types = PLATFORM_MAP.get(os);
					
					types.add(type);
				}
			}
		}
	}

	/**
	 * getCurrentPlatforms
	 * 
	 * @return
	 */
	static Platform[] getCurrentPlatforms()
	{
		return getPlatformsForEclipsePlatform(org.eclipse.core.runtime.Platform.getOS());
	}
	
	/**
	 * getPlatformsForEclipsePlatform
	 * 
	 * @param eclipsePlatform
	 * @return
	 */
	public static Platform[] getPlatformsForEclipsePlatform(String eclipsePlatform)
	{
		Platform[] result = NO_PLATFORMS;

		if (PLATFORM_MAP.containsKey(eclipsePlatform))
		{
			List<Platform> platforms = PLATFORM_MAP.get(eclipsePlatform);
			
			result = platforms.toArray(new Platform[platforms.size()]);
		}

		return result;
	}

	/**
	 * get
	 * 
	 * @param name
	 * @return
	 */
	public static final Platform get(String name)
	{
		return (NAME_MAP.containsKey(name)) ? NAME_MAP.get(name) : UNDEFINED;
	}

	/**
	 * KeyBindingPlatform
	 * 
	 * @param name
	 */
	private Platform(String name)
	{
		this._name = name;
	}

	/**
	 * KeyBindingPlatform
	 * 
	 * @param name
	 * @param os
	 */
	private Platform(String name, String... os)
	{
		this._name = name;
		this._platforms = os;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * getOSes
	 * 
	 * @return
	 */
	public String[] getPlatforms()
	{
		return this._platforms;
	}
}
