package com.aptana.scripting;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class ScriptUtils
{
	public static final IRubyObject[] NO_ARGS = new IRubyObject[0];
	
	/**
	 * ScriptUtils
	 */
	private ScriptUtils()
	{
	}

	/**
	 * instantiateClass
	 * 
	 * @param fullyQualifiedName
	 * @return
	 */
	public static IRubyObject instantiateClass(String name)
	{
		return instantiateClass(name, NO_ARGS);
	}
	
	/**
	 * instantiateClass
	 * 
	 * @param name
	 * @param args
	 * @return
	 */
	public static IRubyObject instantiateClass(String name, IRubyObject[] args)
	{
		Ruby runtime = ScriptingEngine.getInstance().getScriptingContainer().getRuntime();
		ThreadContext threadContext = runtime.getCurrentContext();
		IRubyObject result = null;
		
		// try to load the class
		RubyClass rubyClass = runtime.getClass(name);

		// instantiate it, if it exists
		if (rubyClass != null)
		{
			result = rubyClass.newInstance(threadContext, args, null);
		}
		
		return result;
	}
	
	/**
	 * instantiateClass
	 * 
	 * @param fullyQualifiedName
	 * @return
	 */
	public static IRubyObject instantiateClass(String module, String name)
	{
		return instantiateClass(module, name, NO_ARGS);
	}
	
	/**
	 * instantiateClass
	 * 
	 * @param module
	 * @param name
	 * @param args
	 * @return
	 */
	public static IRubyObject instantiateClass(String module, String name, IRubyObject[] args)
	{
		Ruby runtime = ScriptingEngine.getInstance().getScriptingContainer().getRuntime();
		ThreadContext threadContext = runtime.getCurrentContext();
		IRubyObject result = null;
		
		// try to load the module
		RubyModule rubyModule = runtime.getModule(module); //$NON-NLS-1$
		
		if (rubyModule != null)
		{
			// now try to load the class
			RubyClass rubyClass = rubyModule.getClass(name);
			
			// instantiate it, if it exists
			if (rubyClass != null)
			{
				result = rubyClass.newInstance(threadContext, args, null);
			}
		}
		
		return result;
	}
}
