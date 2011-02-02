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

public enum OutputType
{
	UNDEFINED("undefined", true), //$NON-NLS-1$
	DISCARD("discard", true), //$NON-NLS-1$
	REPLACE_SELECTION("replace_selection", false), //$NON-NLS-1$
	REPLACE_SELECTED_LINES("replace_selected_lines", false), //$NON-NLS-1$
	REPLACE_LINE("replace_line", false), //$NON-NLS-1$
	REPLACE_WORD("replace_word", false), //$NON-NLS-1$
	REPLACE_DOCUMENT("replace_document", false), //$NON-NLS-1$
	INSERT_AS_TEXT("insert_as_text", false), //$NON-NLS-1$
	INSERT_AS_SNIPPET("insert_as_snippet", false), //$NON-NLS-1$
	SHOW_AS_HTML("show_as_html", true), //$NON-NLS-1$
	SHOW_AS_TOOLTIP("show_as_tooltip", false), //$NON-NLS-1$
	CREATE_NEW_DOCUMENT("create_new_document", false), //$NON-NLS-1$
	OUTPUT_TO_CONSOLE("output_to_console", true), //$NON-NLS-1$
	OUTPUT_TO_STREAM("output_to_stream", true), //$NON-NLS-1$
	OUTPUT_TO_FILE("output_to_file", true), //$NON-NLS-1$
	COPY_TO_CLIPBOARD("copy_to_clipboard", false); //$NON-NLS-1$

	private static Map<String, OutputType> NAME_MAP;
	private String _name;
	private boolean _allowAsync;

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
	private OutputType(String name, boolean allowAsync)
	{
		this._name = name;
		this._allowAsync = allowAsync;
	}
	
	/**
	 * allowAsync
	 * 
	 * @return
	 */
	public boolean allowAsync()
	{
		return this._allowAsync;
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