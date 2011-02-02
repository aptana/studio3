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

public enum WorkingDirectoryType
{
	UNDEFINED("undefined"), //$NON-NLS-1$
	PATH("path"), //$NON-NLS-1$
	CURRENT_FILE("current_file"), //$NON-NLS-1$
	CURRENT_PROJECT("current_project"), //$NON-NLS-1$
	CURRENT_BUNDLE("current_bundle"); //$NON-NLS-1$

	private static Map<String, WorkingDirectoryType> NAME_MAP;
	private String _name;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String,WorkingDirectoryType>();

		for (WorkingDirectoryType type : EnumSet.allOf(WorkingDirectoryType.class))
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
	public static final WorkingDirectoryType get(String name)
	{
		return (NAME_MAP.containsKey(name)) ? NAME_MAP.get(name) : UNDEFINED;
	}

	/**
	 * WorkingDirectoryType
	 *
	 * @param name
	 */
	private WorkingDirectoryType(String name)
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
