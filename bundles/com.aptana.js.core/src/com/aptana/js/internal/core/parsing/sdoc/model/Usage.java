/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Usage
{
	UNKNOWN("unknown"), //$NON-NLS-1$
	REQUIRED("required"), //$NON-NLS-1$
	OPTIONAL("optional"), //$NON-NLS-1$
	ZERO_OR_MORE("zero-or-more"), //$NON-NLS-1$
	ONE_OR_MORE("one-or-more"); //$NON-NLS-1$

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
