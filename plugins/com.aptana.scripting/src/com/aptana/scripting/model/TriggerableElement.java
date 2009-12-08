package com.aptana.scripting.model;

import org.jruby.anno.JRubyMethod;

public abstract class TriggerableElement extends AbstractBundleElement
{
	protected String _trigger;
	protected BundleElement _owningBundle;
	protected String _scope;

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

	/**
	 * getOwningBundle
	 * 
	 * @return
	 */
	public BundleElement getOwningBundle()
	{
		return this._owningBundle;
	}

	/**
	 * getScope
	 * 
	 * @return
	 */
	@JRubyMethod(name = "scope")
	public String getScope()
	{
		return this._scope;
	}

	/**
	 * setOwningBundle
	 * 
	 * @param bundle
	 */
	void setOwningBundle(BundleElement bundle)
	{
		this._owningBundle = bundle;
	}

	/**
	 * setScope
	 * 
	 * @param scope
	 */
	@JRubyMethod(name = "scope=")
	public void setScope(String scope)
	{
		this._scope = scope;
	}
}