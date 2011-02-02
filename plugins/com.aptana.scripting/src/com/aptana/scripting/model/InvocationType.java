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

public enum InvocationType
{
	UNKNOWN("unknown"), //$NON-NLS-1$
	KEY_BINDING("key_binding"), //$NON-NLS-1$
	MENU("menu"), //$NON-NLS-1$
	COMMAND("command"), //$NON-NLS-1$
	TRIGGER("trigger"); //$NON-NLS-1$
	
	private static Map<String, InvocationType> NAME_MAP;
	private String _name;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String,InvocationType>();
		
		for (InvocationType type : EnumSet.allOf(InvocationType.class))
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
	public static final InvocationType get(String name)
	{
		return (NAME_MAP.containsKey(name)) ? NAME_MAP.get(name) : UNKNOWN;
	}
	
	/**
	 * InvocationType
	 * 
	 * @param name
	 */
	private InvocationType(String name)
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
