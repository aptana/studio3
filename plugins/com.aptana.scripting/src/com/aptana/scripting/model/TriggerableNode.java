package com.aptana.scripting.model;

import org.jruby.anno.JRubyMethod;

public abstract class TriggerableNode extends AbstractNode
{
	protected String _trigger;

	/**
	 * AbstractTriggerNode
	 * 
	 * @param path
	 */
	public TriggerableNode(String path)
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