package com.aptana.scripting.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum InputType
{
	UNDEFINED("undefined"),
	NONE("none"),
	SELECTION("selection"),
	SELECTED_LINES("selected_lines"),
	LINE("line"),
	WORD("word"),
	LEFT_CHAR("left_character"),
	RIGHT_CHAR("right_character"),
	DOCUMENT("document"),
	INPUT_FROM_CONSOLE("input_from_console");

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