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
	INPUT_FROM_CONSOLE("input_from_console"), //$NON-NLS-1$
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