package com.aptana.scripting.model;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.jruby.embed.ScriptingContainer;

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
	protected void applyLoadPaths(ScriptingContainer container)
	{
		if (this._loadPaths != null && this._loadPaths.size() > 0)
		{
			// NOTE: the following works only when using THREADSAFE containers
			container.getProvider().setLoadPaths(this._loadPaths);
		}
		
		/*
		if (loadPaths != null && loadPaths.size() > 0)
		{
			LocalContextProvider provider = container.getProvider();
	
			if (provider != null)
			{
				Ruby runtime = provider.getRuntime();	    
				IRubyObject object = runtime.getLoadService().getLoadPath();
				RubyArray loadpathArray = (RubyArray) object;
				// wipe whatever we added before
				for (RubyString added : _addedLoadPaths)
				{
					loadpathArray.remove(added);
				}
				_addedLoadPaths.clear();
				// Now add our custom loadpath for this execution
				for (String loadPath : loadPaths)
				{
					RubyString toAdd = runtime.newString(loadPath.replace('\\', '/'));
					loadpathArray.append(toAdd);
					_addedLoadPaths.add(toAdd); 
				}
			}
		}
		*/
	}

	/**
	 * run
	 */
	public void run()
	{
		this.run(new NullProgressMonitor());
	}
}
