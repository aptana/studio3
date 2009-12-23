package com.aptana.scripting;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum LogLevel
{
	NONE(0, "none"), //$NON-NLS-1$
	TRACE(1, "trace"), //$NON-NLS-1$
	INFO(2, "info"), //$NON-NLS-1$
	WARNING(3, "warning"), //$NON-NLS-1$
	ERROR(4, "error"); //$NON-NLS-1$
	
	private static Map<String, LogLevel> NAME_MAP;
	private int _index;
	private String _name;
	
	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String,LogLevel>();
		
		for (LogLevel level : EnumSet.allOf(LogLevel.class))
		{
			NAME_MAP.put(level.getName(), level);
		}
	}
	
	/**
	 * get
	 * 
	 * @param name
	 * @return
	 */
	public static final LogLevel get(String name)
	{
		return (NAME_MAP.containsKey(name)) ? NAME_MAP.get(name) : NONE;
	}
	
	/**
	 * LogLevel
	 * 
	 * @param value
	 */
	private LogLevel(int index, String name)
	{
		this._index = index;
		this._name = name;
	}
	
	/**
	 * getIndex
	 * 
	 * @return
	 */
	public int getIndex()
	{
		return this._index;
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
