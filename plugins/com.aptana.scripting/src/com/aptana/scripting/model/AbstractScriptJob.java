package com.aptana.scripting.model;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyString;
import org.jruby.runtime.builtin.IRubyObject;

public abstract class AbstractScriptJob extends Job implements Runnable
{
	private List<String> _loadPaths;
	
	/**
	 * AbstractScriptJob
	 * 
	 * @param name
	 */
	public AbstractScriptJob(String name)
	{
		this(name, null);
	}

	/**
	 * AbstractScriptJob
	 * 
	 * @param name
	 * @param loadPaths
	 */
	public AbstractScriptJob(String name, List<String> loadPaths)
	{
		super(name);
		
		this._loadPaths = loadPaths;
	}

	/**
	 * applyLoadPaths
	 * 
	 * @param container
	 */
	protected void applyLoadPaths(Ruby runtime)
	{
		if (this._loadPaths != null && this._loadPaths.size() > 0)
		{
			// NOTE: the following works only when using THREADSAFE containers
			// container.getProvider().setLoadPaths(this._loadPaths);

			IRubyObject object = runtime.getLoadService().getLoadPath();
			RubyArray loadpathArray = (RubyArray) object;
			
			// Add our custom loadpaths for this execution
			for (String loadPath : this._loadPaths)
			{
				RubyString toAdd = runtime.newString(loadPath.replace('\\', '/'));
				
				loadpathArray.append(toAdd);
			}
		}
	}

	/**
	 * run
	 */
	public void run()
	{
		this.run(new NullProgressMonitor());
	}
	
	/**
	 * unapplyLoadPaths
	 * 
	 * @param runtime
	 */
	protected void unapplyLoadPaths(Ruby runtime)
	{
		if (this._loadPaths != null && this._loadPaths.size() > 0)
		{
			IRubyObject object = runtime.getLoadService().getLoadPath();
			RubyArray loadpathArray = (RubyArray) object;
			
			// Remove our custom loadpaths from this execution
			for (String loadPath : this._loadPaths)
			{
				RubyString toAdd = runtime.newString(loadPath.replace('\\', '/'));
				
				loadpathArray.remove(toAdd);
			}
		}
	}
}
