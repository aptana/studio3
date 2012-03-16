/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model.filters;

import com.aptana.core.IFilter;
import com.aptana.scripting.model.AbstractElement;

public interface IModelFilter extends IFilter<AbstractElement>
{
	/**
	 * include
	 * 
	 * @param element
	 * @return
	 */
	boolean include(AbstractElement element);
}
