package com.aptana.scripting.model;

public abstract class TriggerableElement extends AbstractBundleElement
{
	private String _trigger;

	/**
	 * AbstractTriggerNode
	 * 
	 * @param path
	 */
	public TriggerableElement(String path)
	{
		super(path);
	}

	/**
	 * getTrigger
	 * 
	 * @return
	 */
	public String getTrigger()
	{
		return this._trigger;
	}

	/**
	 * setTrigger
	 * 
	 * @param trigger
	 */
	public void setTrigger(String trigger)
	{
		this._trigger = trigger;
	}
}