package com.aptana.scripting.model;

import java.util.Map;

public interface ContextContributor
{
	/**
	 * modifyContextMap
	 * 
	 * @param command
	 * @param map
	 */
	void modifyContextMap(CommandElement command, Map<String,Object> map);
}
