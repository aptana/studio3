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