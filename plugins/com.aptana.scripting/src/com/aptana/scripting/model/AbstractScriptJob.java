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
	private RubyArray _originalLoadPaths;

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
			IRubyObject object = runtime.getLoadService().getLoadPath();

			if (object != null && object instanceof RubyArray)
			{
				RubyArray loadPathArray = (RubyArray) object;

				// save copy for later
				this._originalLoadPaths = (RubyArray) loadPathArray.dup();

				// Add our custom load paths
				for (String loadPath : this._loadPaths)
				{
					RubyString toAdd = runtime.newString(loadPath.replace('\\', '/'));

					loadPathArray.append(toAdd);
				}
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
	 * run
	 * 
	 * @param name
	 * @param runType
	 * @throws InterruptedException
	 */
	public void run(String name, RunType runType, boolean async) throws InterruptedException
	{
		switch (runType)
		{
			case JOB:
				this.setPriority(async ? Job.SHORT : Job.INTERACTIVE);
				this.schedule();

				if (async == false)
				{
					this.join();
				}
				break;

			case THREAD:
				Thread thread = new Thread(this, name);
				thread.start();

				if (async == false)
				{
					thread.join();
				}
				break;

			case CURRENT_THREAD:
			default:
				this.run();
		}
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

			if (object != null && object instanceof RubyArray)
			{
				RubyArray loadPathArray = (RubyArray) object;

				// Restore original content
				loadPathArray.replace(this._originalLoadPaths);

				// lose reference
				this._originalLoadPaths = null;
			}
		}
	}
}
