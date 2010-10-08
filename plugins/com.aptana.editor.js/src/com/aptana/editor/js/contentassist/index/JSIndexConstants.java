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
package com.aptana.editor.js.contentassist.index;

public interface JSIndexConstants
{
	// the content format version of the JS index files
	// 0.1 - Initial version
	// 0.11 - Use UUIDs for foreign keys
	// 0.12 - FunctionElements have types and returnTypes now
	// 0.13 - Fix UserAgent foreign keys
	// 0.14 - Fix to StringUtil.join to not include null values in final string
	// 0.15 - Write user agent list from UserAgentManager to index as well
	// 0.16 - Change field order when writing properties and functions (search optimization)
	public static final double INDEX_VERSION = 0.16;

	// general constants
	static final String PREFIX = "js."; //$NON-NLS-1$
	static final String METADATA_FILE_LOCATION = PREFIX + "metadata"; //$NON-NLS-1$
	static final String METADATA_INDEX_LOCATION = "metadata:/js"; //$NON-NLS-1$
	static final String DELIMITER = "\0"; //$NON-NLS-1$
	static final String SUB_DELIMITER = ","; //$NON-NLS-1$
	static final String CORE = "JS Core"; //$NON-NLS-1$

	// index categories
	static final String TYPE = PREFIX + "type"; //$NON-NLS-1$
	static final String FUNCTION = PREFIX + "function"; //$NON-NLS-1$
	static final String PROPERTY = PREFIX + "property"; //$NON-NLS-1$
	static final String DESCRIPTION = PREFIX + "description"; //$NON-NLS-1$
	static final String PARAMETERS = PREFIX + "parameters"; //$NON-NLS-1$
	static final String RETURN_TYPES = PREFIX + "return_types"; //$NON-NLS-1$
	static final String USER_AGENT = PREFIX + "user_agent"; //$NON-NLS-1$
	static final String SINCE_LIST = PREFIX + "since_list"; //$NON-NLS-1$
	static final String EXAMPLES = PREFIX + "examples"; //$NON-NLS-1$

	static final String[] ALL_CATEGORIES = new String[] { //
		TYPE, //
		FUNCTION, //
		PROPERTY, //
		DESCRIPTION, //
		PARAMETERS, //
		RETURN_TYPES, //
		USER_AGENT, //
		SINCE_LIST, //
		EXAMPLES //
	};

	// special values
	static final String NO_ENTRY = "-1"; //$NON-NLS-1$
}
