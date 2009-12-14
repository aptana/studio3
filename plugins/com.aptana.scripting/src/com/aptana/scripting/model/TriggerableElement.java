package com.aptana.scripting.model;

import org.jruby.anno.JRubyMethod;

public abstract class TriggerableElement extends AbstractBundleElement
{
	protected String _trigger;

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
	@JRubyMethod(name = "trigger")
	public String getTrigger()
	{
		return this._trigger;
	}

	/**
	 * setTrigger
	 * 
	 * @param trigger
	 */
	@JRubyMethod(name = "trigger=")
	public void setTrigger(String trigger)
	{
		this._trigger = trigger;
	}
}