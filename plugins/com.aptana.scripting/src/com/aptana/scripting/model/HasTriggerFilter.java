package com.aptana.scripting.model;

public class HasTriggerFilter implements IModelFilter
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
			String[] triggers = node.getTriggers();
			
			result = (triggers != null && triggers.length > 0);
		}
		
		return result;
	}
}
