package com.aptana.scripting.model;

public class TriggerOnlyFilter implements IModelFilter
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IModelFilter#exclude(com.aptana.scripting.model.AbstractNode)
	 */
	public boolean exclude(AbstractElement element)
	{
		return (this.include(element) == false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IModelFilter#include(com.aptana.scripting.model.AbstractModel)
	 */
	public boolean include(AbstractElement element)
	{
		boolean result = false;

		if (element instanceof TriggerableElement)
		{
			TriggerableElement node = (TriggerableElement) element;
			String trigger = node.getTrigger();
			
			result = (trigger != null && trigger.length() > 0);
		}
		
		return result;
	}
}
