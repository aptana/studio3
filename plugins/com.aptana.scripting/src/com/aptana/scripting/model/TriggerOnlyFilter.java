package com.aptana.scripting.model;

public class TriggerOnlyFilter implements IModelFilter
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IModelFilter#exclude(com.aptana.scripting.model.AbstractNode)
	 */
	public boolean exclude(AbstractNode element)
	{
		return (this.include(element) == false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IModelFilter#include(com.aptana.scripting.model.AbstractModel)
	 */
	public boolean include(AbstractNode element)
	{
		boolean result = false;

		if (element instanceof TriggerableNode)
		{
			TriggerableNode node = (TriggerableNode) element;
			String trigger = node.getTrigger();
			
			result = (trigger != null && trigger.length() > 0);
		}
		
		return result;
	}
}
