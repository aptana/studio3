/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum InputType
{
	UNDEFINED("undefined"), //$NON-NLS-1$
	NONE("none"), //$NON-NLS-1$
	SELECTION("selection"), //$NON-NLS-1$
	SELECTED_LINES("selected_lines"), //$NON-NLS-1$
	LINE("line"), //$NON-NLS-1$
	WORD("word"), //$NON-NLS-1$
	LEFT_CHAR("left_character"), //$NON-NLS-1$
	RIGHT_CHAR("right_character"), //$NON-NLS-1$
	DOCUMENT("document"), //$NON-NLS-1$
	CLIPBOARD("clipboard"),  //$NON-NLS-1$
	INPUT_FROM_CONSOLE("input_from_console"), //$NON-NLS-1$
	INPUT_FROM_FILE("input_from_file"), //$NON-NLS-1$
	INPUT_FROM_STREAM("input_from_stream"); //$NON-NLS-1$

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
		if (name == null)
			return UNDEFINED;
		name = name.toLowerCase();
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