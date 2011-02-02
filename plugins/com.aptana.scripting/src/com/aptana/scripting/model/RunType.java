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

public enum RunType
{
	JOB("job"), //$NON-NLS-1$
	CURRENT_THREAD("current_thread"), //$NON-NLS-1$
	THREAD("thread"); //$NON-NLS-1$

	private static Map<String, RunType> NAME_MAP;
	private String _name;

	static
	{
		NAME_MAP = new HashMap<String, RunType>();

		for (RunType type : EnumSet.allOf(RunType.class))
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
	public static final RunType get(String name)
	{
		return (NAME_MAP.containsKey(name)) ? NAME_MAP.get(name) : JOB;
	}

	/**
	 * RunType
	 * 
	 * @param name
	 */
	private RunType(String name)
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
}
