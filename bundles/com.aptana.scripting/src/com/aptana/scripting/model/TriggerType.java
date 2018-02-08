/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * EventTriggerType
 */
public enum TriggerType
{
	UNDEFINED("undefined"), // $NON-NLS-1$ //$NON-NLS-1$
	PREFIX("prefix"), // $NON-NLS-1$ //$NON-NLS-1$
	FILE_WATCHER("file_watcher"), // $NON-NLS-1$ //$NON-NLS-1$
	EXECUTION_LISTENER("execution_listener"); // $NON-NLS-1$ //$NON-NLS-1$

	private static Map<String, TriggerType> NAME_MAP;
	private String _name;

	/**
	 * static initializer
	 */
	static
	{
		NAME_MAP = new HashMap<String, TriggerType>();

		for (TriggerType type : EnumSet.allOf(TriggerType.class))
		{
			NAME_MAP.put(type.getName(), type);
		}
	}

	/**
	 * get
	 * 
	 * @param name
	 * @return
	 */
	public static final TriggerType get(String name)
	{
		return (NAME_MAP.containsKey(name)) ? NAME_MAP.get(name) : UNDEFINED;
	}

	/**
	 * EventTriggerType
	 * 
	 * @param name
	 */
	private TriggerType(String name)
	{
		this._name = name;
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
	 * getPropertyName
	 * 
	 * @return
	 */
	public String getPropertyName()
	{
		return BundleManager.getInstance().sharedString(this._name.concat("_values")); //$NON-NLS-1$
	}
}
