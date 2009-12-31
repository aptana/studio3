package com.aptana.scripting.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum OutputType
{
	UNDEFINED("undefined"), //$NON-NLS-1$
	DISCARD("discard"), //$NON-NLS-1$
	REPLACE_SELECTION("replace_selection"), //$NON-NLS-1$
	REPLACE_SELECTED_LINES("replace_selected_lines"), //$NON-NLS-1$
	REPLACE_LINE("replace_line"), //$NON-NLS-1$
	REPLACE_WORD("replace_word"), //$NON-NLS-1$
	REPLACE_DOCUMENT("replace_document"), //$NON-NLS-1$
	INSERT_AS_TEXT("insert_as_text"), //$NON-NLS-1$
	INSERT_AS_SNIPPET("insert_as_snippet"), //$NON-NLS-1$
	SHOW_AS_HTML("show_as_html"), //$NON-NLS-1$
	SHOW_AS_TOOLTIP("show_as_tooltip"), //$NON-NLS-1$
	CREATE_NEW_DOCUMENT("create_new_document"), //$NON-NLS-1$
	OUTPUT_TO_CONSOLE("output_to_console"), //$NON-NLS-1$
	OUTPUT_TO_STREAM("output_to_stream"), //$NON-NLS-1$
	OUTPUT_TO_FILE("output_to_file"), //$NON-NLS-1$
	COPY_TO_CLIPBOARD("copy_to_clipboard"); //$NON-NLS-1$

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