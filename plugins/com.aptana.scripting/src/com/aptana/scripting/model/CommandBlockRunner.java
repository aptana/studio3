/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyClass;
import org.jruby.RubyGlobal.InputGlobalVariable;
import org.jruby.RubyGlobal.OutputGlobalVariable;
import org.jruby.RubyHash;
import org.jruby.RubyIO;
import org.jruby.RubyProc;
import org.jruby.RubySystemExit;
import org.jruby.exceptions.RaiseException;
import org.jruby.internal.runtime.GlobalVariable.Scope;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.core.util.IOUtil;
import com.aptana.scope.ScopeSelector;
import com.aptana.scripting.ScriptUtils;
import com.aptana.scripting.model.filters.IModelFilter;
import com.aptana.scripting.model.filters.ScopeFilter;

public class CommandBlockRunner extends AbstractCommandRunner
{
	private static final String STDIN_GLOBAL = "$stdin"; //$NON-NLS-1$
	private static final String STDIN_CONSTANT = "STDIN"; //$NON-NLS-1$
	private static final String DEFERR_GLOBAL = "$deferr"; //$NON-NLS-1$
	private static final String STDERR_GLOBAL = "$stderr"; //$NON-NLS-1$
	private static final String DEFOUT_GLOBAL = "$defout"; //$NON-NLS-1$
	private static final String STDOUT_GLOBAL2 = "$>"; //$NON-NLS-1$
	private static final String STDOUT_CONSTANT = "STDOUT"; //$NON-NLS-1$
	private static final String STDOUT_GLOBAL = "$stdout"; //$NON-NLS-1$
	private static final String STDERR_CONSTANT = "STDERR"; //$NON-NLS-1$
	private static final String CONSOLE_CONSTANT = "CONSOLE"; //$NON-NLS-1$
	private static final String CONSOLE_VARIABLE = "$console"; //$NON-NLS-1$
	private static final String CONTEXT_RUBY_CLASS = "Context"; //$NON-NLS-1$
	private static final String OUTPUT_PROPERTY = "output"; //$NON-NLS-1$
	private static final String ENV_PROPERTY = "ENV"; //$NON-NLS-1$

	private RubyHash _originalEnvironment;
	private IRubyObject _oldReader;
	private IRubyObject _oldWriter;
	private IRubyObject _oldErrorWriter;
	private IRubyObject _oldConsole;

	/**
	 * ExecuteScriptJob
	 * 
	 * @param block
	 * @param loadPaths
	 */
	public CommandBlockRunner(CommandElement command, CommandContext context, List<String> loadPaths)
	{
		super("Execute JRuby Block", command, context, loadPaths); //$NON-NLS-1$
	}

	/**
	 * afterExecute
	 */
	protected void afterExecute()
	{
		Ruby runtime = this.getRuntime();

		// register any bundle libraries that were loaded by this script
		this.registerLibraries(runtime, this.getCommand().getPath());

		// unapply load paths
		this.unapplyLoadPaths(runtime);

		// unapply streams
		this.unapplyStreams();

		// unapply environment
		this.unapplyEnvironment();
	}

	/**
	 * applyEnvironment
	 * 
	 * @param container
	 */
	protected void applyEnvironment()
	{
		Ruby runtime = this.getRuntime();
		IRubyObject env = runtime.getObject().getConstant(ENV_PROPERTY);

		if (env != null && env instanceof RubyHash)
		{
			RubyHash hash = (RubyHash) env;

			// save copy for later
			this._originalEnvironment = (RubyHash) hash.dup();

			hash.putAll(this.getContributedEnvironment());

			// Grab all the matching env objects contributed via bundles that have scope matching!
			IModelFilter filter = new ScopeFilter((String) hash.get("TM_CURRENT_SCOPE")); //$NON-NLS-1$
			List<EnvironmentElement> envs = BundleManager.getInstance().getEnvs(filter);
			ScopeSelector.sort(envs);
			for (EnvironmentElement e : envs)
			{
				RubyProc invoke = e.getInvokeBlock();
				if (invoke != null)
				{
					invoke.call(runtime.getCurrentContext(), new IRubyObject[] { hash });
				}
			}
		}
	}

	/**
	 * applyStreams
	 * 
	 * @param container
	 */
	protected void applyStreams()
	{
		Ruby runtime = this.getRuntime();
		CommandContext context = this.getContext();

		// turn off verbose mode (and warnings)
		boolean isVerbose = runtime.isVerbose();
		runtime.setVerbose(runtime.getNil());

		// set stdin/out/err and console
		this._oldReader = this.setReader(context.getInputStream());
		this._oldWriter = this.setWriter(context.getOutputStream());
		this._oldErrorWriter = this.setErrorWriter(context.getErrorStream());
		this._oldConsole = this.setConsole(context.getConsoleStream());

		// restore verbose mode
		runtime.setVerbose((isVerbose) ? runtime.getTrue() : runtime.getFalse());
	}

	/**
	 * beforeExecute
	 * 
	 * @return
	 */
	protected void beforeExecute()
	{
		Ruby runtime = this.getRuntime();

		// apply load paths
		this.applyLoadPaths(runtime);

		// apply streams
		this.applyStreams();

		// setup ENV
		this.applyEnvironment();

		// set default output type, this may be changed by context.exit_with_message
		this.getContext().setOutputType(this.getCommand().getOutputType());
	}

	/**
	 * closeStreams
	 */
	protected void closeStreams()
	{
		CommandContext context = this.getContext();
		InputStream stdin = context.getInputStream();
		OutputStream stdout = context.getOutputStream();

		if (stdin != null)
		{
			try
			{
				stdin.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if (stdout != null)
		{
			try
			{
				stdout.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * executeBlock
	 * 
	 * @param threadContext
	 * @param rubyContext
	 * @return
	 */
	protected String executeBlock()
	{
		CommandElement command = this.getCommand();
		CommandContext context = this.getContext();

		// create context
		Ruby runtime = this.getRuntime();
		ThreadContext threadContext = runtime.getCurrentContext();
		IRubyObject rubyContext = ScriptUtils.instantiateClass(runtime, ScriptUtils.RUBLE_MODULE, CONTEXT_RUBY_CLASS,
				JavaEmbedUtils.javaToRuby(runtime, context));
		String resultText = null;

		// assume that we've executed successfully
		this.setExecutedSuccessfully(true);

		try
		{
			// invoke the block
			IRubyObject result = this.getCommand().getInvokeBlock()
					.call(threadContext, new IRubyObject[] { rubyContext });

			// TODO: not sure if we need to perform the closing here or not. This will be
			// resolved once we rework CommandExecutionUtils to support async calls. That's
			// when the streams will come into play

			// close any streams we have
			// this.closeStreams();

			// process return result, if any
			if (result != null && result.isNil() == false)
			{
				// to_s for array and hash doesn't do what we want/many people expect.
				// Inspect spits out values in a way that can then be eval'd back as ruby code.
				if ((result instanceof RubyArray) || (result instanceof RubyHash))
				{
					result = result.inspect();
				}
				// Fix for RR3-677 - Incorrect transformation for non-latin characters after #rrinclude HTML
				// We take the raw bytes returned and force to a UTF-8 String, vs ASCII default.
				resultText = new String(result.asString().getByteList().bytes(), IOUtil.UTF_8);
			}
		}
		catch (RaiseException e)
		{
			if (e.getException() instanceof RubySystemExit)
			{
				RubySystemExit exit = (RubySystemExit) e.getException();
				if (context.isForcedExit())
				{
					// should be from the exit call in exit_with_message
					resultText = context.get(OUTPUT_PROPERTY).toString();
				}
				else if (exit.success_p().isTrue()) // command did an exit with exitcode of zero
				{
					// exited OK, just assume we're fine!
				}
				else
				{
					executionFailed(command, e);
				}
			}
			else
			{
				executionFailed(command, e);
			}
		}
		catch (Exception e)
		{
			executionFailed(command, e);
		}

		return resultText;
	}

	private void executionFailed(CommandElement command, Exception e)
	{
		String message = MessageFormat.format( //
				Messages.CommandElement_Error_Processing_Command_Block, //
				new Object[] { command.getDisplayName(), command.getPath(), e.getMessage() } //
				); //

		ScriptUtils.logErrorWithStackTrace(message, e);
		this.setExecutedSuccessfully(false);
	}

	/**
	 * getRuntime
	 * 
	 * @return
	 */
	protected Ruby getRuntime()
	{
		return this.getCommand().getRuntime();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor)
	{
		CommandContext context = this.getContext();
		String resultText = null;

		// execute block
		synchronized (this.getRuntime())
		{
			this.beforeExecute();
			resultText = this.executeBlock();
			this.afterExecute();
		}

		// process result
		CommandResult result = new CommandResult(this.getCommand(), context);

		result.setExecutedSuccessfully(this.getExecutedSuccessfully());

		if (resultText != null)
		{
			result.setOutputString(resultText);
		}
		else if (context.getOutputStream() != null)
		{
			result.setOutputStream(context.getOutputStream());
		}
		else
		{
			result.setOutputString(""); //$NON-NLS-1$
		}

		// save result
		this.setCommandResult(result);

		return Status.OK_STATUS;
	}

	/**
	 * setConsole
	 * 
	 * @param io
	 */
	protected void setConsole(IRubyObject io)
	{
		Ruby runtime = getRuntime();

		runtime.getGlobalVariables().set(CONSOLE_VARIABLE, io);
		runtime.getObject().setConstant(CONSOLE_CONSTANT, io);
	}

	/**
	 * setConsole
	 * 
	 * @param ostream
	 */
	protected IRubyObject setConsole(OutputStream ostream)
	{
		Ruby runtime = getRuntime();
		RubyClass object = runtime.getObject();
		IRubyObject oldValue = null;

		// CONSOLE will only exist already if one command invokes another
		if (object.hasConstant(CONSOLE_CONSTANT))
		{
			oldValue = object.getConstant(CONSOLE_CONSTANT);
		}

		if (ostream != null)
		{
			RubyIO io = new RubyIO(runtime, ostream);

			io.getOpenFile().getMainStream().setSync(true);
			setConsole(io);
		}

		return oldValue;
	}

	/**
	 * setErrorWriter
	 * 
	 * @param io
	 */
	protected void setErrorWriter(IRubyObject io)
	{
		Ruby runtime = getRuntime();

		runtime.defineVariable(new OutputGlobalVariable(runtime, STDERR_GLOBAL, io), Scope.GLOBAL);
		runtime.defineGlobalConstant(STDERR_CONSTANT, io);
		runtime.getGlobalVariables().alias(DEFERR_GLOBAL, STDERR_GLOBAL);
	}

	/**
	 * setErrorWriter - based on ScriptContainer#setErrorWriter
	 * 
	 * @param ostream
	 */
	protected IRubyObject setErrorWriter(OutputStream ostream)
	{
		Ruby runtime = getRuntime();
		IRubyObject oldValue = runtime.getObject().getConstant(STDERR_CONSTANT);

		if (ostream != null)
		{
			PrintStream pstream = new PrintStream(ostream);
			RubyIO io = new RubyIO(runtime, pstream);

			io.getOpenFile().getMainStream().setSync(true);
			setErrorWriter(io);
		}

		return oldValue;
	}

	/**
	 * setReader - based on ScriptContainer#setReader
	 * 
	 * @param istream
	 */
	protected IRubyObject setReader(InputStream istream)
	{
		Ruby runtime = this.getRuntime();
		IRubyObject oldValue = runtime.getObject().getConstant(STDIN_CONSTANT);

		if (istream != null)
		{
			setReader(new RubyIO(runtime, istream));
		}

		return oldValue;
	}

	/**
	 * setReader
	 * 
	 * @param io
	 */
	protected void setReader(IRubyObject io)
	{
		Ruby runtime = this.getRuntime();

		runtime.defineVariable(new InputGlobalVariable(runtime, STDIN_GLOBAL, io), Scope.GLOBAL);
		runtime.defineGlobalConstant(STDIN_CONSTANT, io);
	}

	/**
	 * setWriter
	 * 
	 * @param io
	 */
	protected void setWriter(IRubyObject io)
	{
		Ruby runtime = this.getRuntime();

		runtime.defineVariable(new OutputGlobalVariable(runtime, STDOUT_GLOBAL, io), Scope.GLOBAL);
		runtime.defineGlobalConstant(STDOUT_CONSTANT, io);
		runtime.getGlobalVariables().alias(STDOUT_GLOBAL2, STDOUT_GLOBAL);
		runtime.getGlobalVariables().alias(DEFOUT_GLOBAL, STDOUT_GLOBAL);
	}

	/**
	 * setWriter - based on ScriptContainer#setWriter
	 * 
	 * @param ostream
	 */
	protected IRubyObject setWriter(OutputStream ostream)
	{
		Ruby runtime = this.getRuntime();
		IRubyObject oldValue = runtime.getObject().getConstant(STDOUT_CONSTANT);

		if (ostream != null)
		{
			PrintStream pstream = new PrintStream(ostream);
			RubyIO io = new RubyIO(runtime, pstream);

			io.getOpenFile().getMainStream().setSync(true);
			setWriter(io);
		}

		return oldValue;
	}

	/**
	 * unapplyEnvironment
	 */
	protected void unapplyEnvironment()
	{
		Ruby runtime = this.getRuntime();
		IRubyObject env = runtime.getObject().getConstant(ENV_PROPERTY);

		if (env != null && env instanceof RubyHash)
		{
			RubyHash hash = (RubyHash) env;

			// restore original content
			hash.replace(runtime.getCurrentContext(), this._originalEnvironment);

			// lose reference
			this._originalEnvironment = null;
		}
	}

	/**
	 * unapplyStreams
	 */
	protected void unapplyStreams()
	{
		Ruby runtime = this.getRuntime();

		// turn off verbose mode (and warnings)
		boolean isVerbose = runtime.isVerbose();
		runtime.setVerbose(runtime.getNil());

		// restore original values for STDIN/OUT/ERR
		this.setReader(this._oldReader);
		this.setWriter(this._oldWriter);
		this.setErrorWriter(this._oldErrorWriter);

		// remove CONSOLE/$console
		if (this._oldConsole == null)
		{
			runtime.getObject().remove_const(runtime.getCurrentContext(), runtime.newString(CONSOLE_CONSTANT));
		}
		else
		{
			this.setConsole(this._oldConsole);
		}

		// restore verbose mode
		runtime.setVerbose((isVerbose) ? runtime.getTrue() : runtime.getFalse());
	}
}
