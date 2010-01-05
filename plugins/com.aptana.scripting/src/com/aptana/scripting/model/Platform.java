package com.aptana.scripting.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Platform
{
	// NOTE: LINUX needs to be defined after UNIX so it will map to the OS_LINUX constant.
	// In other words, it has higher specificity than UNIX

	UNDEFINED("undefined"), //$NON-NLS-1$
	ALL("all"), //$NON-NLS-1$
	MAC("mac", org.eclipse.core.runtime.Platform.OS_MACOSX), //$NON-NLS-1$
	WIN("windows", org.eclipse.core.runtime.Platform.OS_WIN32), //$NON-NLS-1$
	UNIX("unix", org.eclipse.core.runtime.Platform.OS_AIX, org.eclipse.core.runtime.Platform.OS_HPUX, //$NON-NLS-1$
			org.eclipse.core.runtime.Platform.OS_LINUX, org.eclipse.core.runtime.Platform.OS_SOLARIS),
	LINUX("linux", org.eclipse.core.runtime.Platform.OS_LINUX); //$NON-NLS-1$

	private static Map<String, Platform> NAME_MAP;
	private static Map<String, Platform> PLATFORM_MAP;
	private String _name;
	private String[] _platforms;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, Platform>();
		PLATFORM_MAP = new HashMap<String, Platform>();

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
					PLATFORM_MAP.put(os, type);
				}
			}
		}
	}

	/**
	 * getPlatform
	 * 
	 * @return
	 */
	static Platform getPlatform()
	{
		String os = org.eclipse.core.runtime.Platform.getOS();
		Platform result = UNDEFINED;

		if (PLATFORM_MAP.containsKey(os))
		{
			result = PLATFORM_MAP.get(os);
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
