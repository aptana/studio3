/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jruby.Ruby;
import org.jruby.embed.EmbedEvalUnit;
import org.jruby.embed.EvalFailedException;
import org.jruby.embed.ParseFailedException;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;

import com.aptana.scripting.Messages;
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptingEngine;

public class ScriptLoadJob extends AbstractScriptRunner
{
	private String _filename;
	private Object _returnValue;

	/**
	 * ExecuteScriptJob
	 * 
	 * @param filename
	 * @param loadPaths
	 */
	public ScriptLoadJob(String filename, List<String> loadPaths)
	{
		this("Execute JRuby File", filename, loadPaths); //$NON-NLS-1$
	}

	/**
	 * ExecuteScriptJob
	 * 
	 * @param name
	 * @param filename
	 * @param loadPaths
	 */
	public ScriptLoadJob(String name, String filename, List<String> loadPaths)
	{
		super(name, loadPaths);

		this._filename = filename;
	}

	/**
	 * getReturnValue
	 * 
	 * @return
	 */
	public Object getReturnValue()
	{
		return this._returnValue;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor)
	{
		ScriptingContainer container = ScriptingEngine.getInstance().getInitializedScriptingContainer();
		Ruby runtime = container.getProvider().getRuntime();
		Object result = null;

		if (this._filename != null && new File(this._filename).canRead())
		{
			synchronized (runtime)
			{
				// apply load paths
				this.applyLoadPaths(runtime);

				// compile
				try
				{
					EmbedEvalUnit unit = container.parse(PathType.ABSOLUTE, this._filename);

					// execute
					result = unit.run();
				}
				catch (ParseFailedException e)
				{
					String message = MessageFormat.format(Messages.ScriptingEngine_Parse_Error, new Object[] {
							this._filename, e.getMessage() });

					ScriptLogger.logError(message);
				}
				catch (EvalFailedException e)
				{
					StringWriter sw = new StringWriter();
					e.getCause().printStackTrace(new PrintWriter(sw));
					String message = MessageFormat.format(Messages.ScriptingEngine_Execution_Error, new Object[] {
							this._filename, e.getMessage(), sw.toString() });

					ScriptLogger.logError(message);
				}

				// register any bundle libraries that were loaded by this script
				this.registerLibraries(runtime, this._filename);

				// unapply load paths
				this.unapplyLoadPaths(runtime);
			}
		}

		// save result
		this.setReturnValue(result);

		// return status
		return Status.OK_STATUS;
	}

	/**
	 * setReturnValue
	 * 
	 * @param value
	 */
	protected void setReturnValue(Object value)
	{
		this._returnValue = value;
	}
}
