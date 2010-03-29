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
