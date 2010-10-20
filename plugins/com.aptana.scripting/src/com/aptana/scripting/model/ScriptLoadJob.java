/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

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
		ScriptingContainer container = ScriptingEngine.getInstance().getScriptingContainer();
		Ruby runtime = container.getProvider().getRuntime();
		Object result = null;

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
				String message = MessageFormat.format(Messages.ScriptingEngine_Execution_Error, new Object[] {
						this._filename, e.getMessage() });

				ScriptLogger.logError(message);
			}

			// register any bundle libraries that were loaded by this script
			this.registerLibraries(runtime, this._filename);

			// unapply load paths
			this.unapplyLoadPaths(runtime);
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
