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

public enum InputType
{
	UNDEFINED("undefined"), //$NON-NLS-1$
	NONE("none"), //$NON-NLS-1$
	SELECTION("selection"), //$NON-NLS-1$
	SELECTED_LINES("selected_lines"), //$NON-NLS-1$
	LINE("line"), //$NON-NLS-1$
	WORD("word"), //$NON-NLS-1$
	LEFT_CHAR("left_character"), //$NON-NLS-1$
	RIGHT_CHAR("right_character"), //$NON-NLS-1$
	DOCUMENT("document"), //$NON-NLS-1$
	CLIPBOARD("clipboard"),  //$NON-NLS-1$
	INPUT_FROM_CONSOLE("input_from_console"), //$NON-NLS-1$
	INPUT_FROM_FILE("input_from_file"), //$NON-NLS-1$
	INPUT_FROM_STREAM("input_from_stream"); //$NON-NLS-1$

	private static final Map<String, InputType> NAME_MAP;
	private String _name;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String,InputType>();
		
		for (InputType type : EnumSet.allOf(InputType.class))
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
	public static final InputType get(String name)
	{
		if (name == null)
			return UNDEFINED;
		name = name.toLowerCase();
		return (NAME_MAP.containsKey(name)) ? NAME_MAP.get(name) : UNDEFINED;
	}
	
	/**
	 * InputType
	 * 
	 * @param name
	 */
	private InputType(String name)
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