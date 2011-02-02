/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model.filters;

import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.CommandElement;

public class IsExecutableCommandFilter implements IModelFilter
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IModelFilter#include(com.aptana.scripting.model.AbstractModel)
	 */
	public boolean include(AbstractElement element)
	{
		boolean result = false;

		if (element instanceof CommandElement)
		{
			CommandElement node = (CommandElement) element;
			result = node.isExecutable();
		}

		return result;
	}
}
