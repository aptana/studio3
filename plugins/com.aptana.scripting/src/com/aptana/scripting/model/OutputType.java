package com.aptana.scripting.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum OutputType
{
	UNDEFINED("undefined"),
	DISCARD("discard"),
	REPLACE_SELECTION("replace_selection"),
	REPLACE_SELECTED_LINES("replace_selected_lines"),
	REPLACE_LINE("replace_line"),
	REPLACE_WORD("replace_word"),
	REPLACE_DOCUMENT("replace_document"),
	INSERT_AS_TEXT("insert_as_text"),
	INSERT_AS_SNIPPET("insert_as_snippet"),
	SHOW_AS_HTML("show_as_html"),
	SHOW_AS_TOOLTIP("show_as_tooltip"),
	CREATE_NEW_DOCUMENT("create_new_document"),
	OUTPUT_TO_CONSOLE("output_to_console");

	private static Map<String, OutputType> NAME_MAP;
	private String _name;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String,OutputType>();
		
		for (OutputType type : EnumSet.allOf(OutputType.class))
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
	public static final OutputType get(String name)
	{
		return (NAME_MAP.containsKey(name)) ? NAME_MAP.get(name) : UNDEFINED;
	}
	
	/**
	 * OutputType
	 * 
	 * @param name
	 */
	private OutputType(String name)
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