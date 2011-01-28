/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.util.HashMap;
import java.util.Map;

public class TestEnvironmentContributor implements EnvironmentContributor
{
	public static final String[] TEST_VARIABLES = new String[] { "test1", "test2", "test3" };
	public static final String[] TEST_VALUES = new String[] { "value1", "value2", "value3" };
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.EnvironmentContributor#toEnvironment()
	 */
	public Map<String, String> toEnvironment()
	{
		Map<String, String> result = new HashMap<String, String>();
		
		for (int i = 0; i < TEST_VARIABLES.length; i++)
		{
			result.put(TEST_VARIABLES[i], TEST_VALUES[i]);
		}
		
		return result;
	}
}
