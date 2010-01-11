package com.aptana.scripting.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum InvocationType
{
	UNKNOWN("unknown"),
	KEY_BINDING("key_binding"),
	MENU("menu"),
	COMMAND("command"),
	TRIGGER("trigger");
	
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
