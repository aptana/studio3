/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import java.util.List;

import com.aptana.js.core.model.AliasElement;
import com.aptana.js.core.model.TypeElement;

/**
 * @author cwilliams
 */
public interface IJSCAModel
{
	/**
	 * Returns the list of aliases defined in the wrapped JSCA file.
	 * 
	 * @return
	 */
	public List<AliasElement> getAliases();

	/**
	 * Returns the list of types defined in the wrapped JSCA file. This will contain an entire hierarchy of objects
	 * hanging from the types(properties, events, user agents, etc).
	 * 
	 * @return
	 */
	public List<TypeElement> getTypes();
}
