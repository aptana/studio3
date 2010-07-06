package com.aptana.editor.js.sdoc.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Usage
{
	UNKNOWN("unknown"),
	REQUIRED("required"),
	OPTIONAL("optional"),
	ZERO_OR_MORE("zero-or-more"),
	ONE_OR_MORE("one-or-more");
	
	private static final Map<String, Usage> NAME_MAP;
	private String _name;
	
	/**
	 * static initializer
	 */
	static
	{
		NAME_MAP = new HashMap<String, Usage>();
		
		for (Usage usage : EnumSet.allOf(Usage.class))
		{
			NAME_MAP.put(usage.getName(), usage);
		}
	}
	
	/**
	 * Usage
	 * 
	 * @param name
	 */
	private Usage(String name)
	{
		this._name = name;
	}

	/**
	 * fromName
	 * 
	 * @param name
	 * @return
	 */
	public static Usage fromName(String name)
	{
		Usage result = NAME_MAP.get(name);

		return (result != null) ? result : UNKNOWN;
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
