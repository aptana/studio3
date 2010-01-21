package com.aptana.scripting.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jruby.Ruby;
import org.jruby.RubyHash;
import org.jruby.RubyIO;
import org.jruby.RubyProc;
import org.jruby.RubySystemExit;
import org.jruby.embed.ScriptingContainer;
import org.jruby.exceptions.RaiseException;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.ScriptUtils;
import com.aptana.scripting.ScriptingEngine;

public class ExecuteBlockJob extends AbstractScriptJob
{
	private static final String CONSOLE_CONSTANT = "CONSOLE";
	private static final String CONSOLE_VARIABLE = "$console";
	private static final String CONTEXT_RUBY_CLASS = "Context"; //$NON-NLS-1$
	private static final String OUTPUT_PROPERTY = "output"; //$NON-NLS-1$
	private static final String ENV_PROPERTY = "ENV"; //$NON-NLS-1$

	private CommandElement _command;
	private CommandContext _context;
	private boolean _executedSuccessfully;
	private CommandResult _result;

	/**
	 * ExecuteScriptJob
	 * 
	 * @param command
	 * @param context
	 */
	public ExecuteBlockJob(CommandElement command, CommandContext context)
	{
		this("Execute JRuby Block", command, context, null);
	}

	/**
	 * ExecuteScriptJob
	 * 
	 * @param block
	 * @param loadPaths
	 */
	public ExecuteBlockJob(CommandElement command, CommandContext context, List<String> loadPaths)
	{
		this("Execute JRuby Block", command, context, loadPaths);
	}

	/**
	 * ExecuteScriptJob
	 * 
	 * @param name
	 * @param block
	 */
	public ExecuteBlockJob(String name, CommandElement command, CommandContext context)
	{
		this(name, command, context, null);
	}

	/**
	 * ExecuteScriptJob
	 * 
	 * @param name
	 * @param block
	 * @param loadPaths
	 */
	public ExecuteBlockJob(String name, CommandElement command, CommandContext context, List<String> loadPaths)
	{
		super(name, loadPaths);

		this._command = command;
		this._context = context;
	}

	/**
	 * applyEnvironment
	 * 
	 * @param container
	 */
	protected void applyEnvironment(ScriptingContainer container)
	{
		Ruby runtime = container.getRuntime();
		IRubyObject env = runtime.getObject().getConstant(ENV_PROPERTY);
		
		if (env != null && env instanceof RubyHash)
		{
			Map<String, String> environment = new HashMap<String, String>();
			RubyHash hash = (RubyHash) env;
			
			this._command.populateEnvironment(this._context.getMap(), environment);
			hash.putAll(environment);
		}
	}
	/**
	 * applyStreams
	 * 
	 * @param container
	 */
	protected void applyStreams(ScriptingContainer container)
	{
		InputStream stdin = this._context.getInputStream();
		OutputStream stdout = this._context.getOutputStream();
		OutputStream stderr = this._context.getErrorStream();
		OutputStream console = this._context.getConsoleStream();
		
		Ruby runtime = container.getRuntime();
		
		// turn off verbose mode (and warnings)
		boolean isVerbose = runtime.isVerbose();
		runtime.setVerbose(runtime.getNil());

		// set stdin
		if (stdin != null)
		{
			container.setReader(new InputStreamReader(stdin));
		}

		// set stdout
		if (stdout != null)
		{
			container.setWriter(new OutputStreamWriter(stdout));
		}

		// set stderr
		if (stderr != null)
		{
			container.setErrorWriter(new OutputStreamWriter(stderr));
		}
		
		// set console
		if (console != null)
		{
			RubyIO rubyStream = new RubyIO(runtime, console);
			rubyStream.sync_set(runtime.getTrue());	// force immediate output
			
			// store as a global and a constant
			runtime.getGlobalVariables().set(CONSOLE_VARIABLE, rubyStream);
			runtime.getObject().setConstant(CONSOLE_CONSTANT, rubyStream);
		}

		// restore verbose mode
		runtime.setVerbose((isVerbose) ? runtime.getTrue() : runtime.getFalse());
	}

	/**
	 * getCommandResult
	 * 
	 * @return
	 */
	public CommandResult getCommandResult()
	{
		return this._result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor)
	{
		Thread t = Thread.currentThread();
		System.out.println("+" + t + ": " + t.getId());
		
		ScriptingContainer container = ScriptingEngine.getInstance().getScriptingContainer();

		// apply load paths
		this.applyLoadPaths(container);

		// apply streams
		this.applyStreams(container);
		
		// setup ENV
		this.applyEnvironment(container);
		
		// set default output type, this may be changed by context.exit_with_message
		this._context.setOutputType(this._command.getOutputType());

		// execute block
		String resultText = this.executeBlock(container);
		
		// process result
		CommandResult result = new CommandResult();
		
		result.setExecutedSuccessfully(this._executedSuccessfully);
		result.setCommand(this._command);
		result.setContext(this._context);
		
		if (resultText != null)
		{
			result.setOutputString(resultText);
		}
		else if (this._context.getOutputStream() != null)
		{
			result.setOutputStream(this._context.getOutputStream());
		}
		else
		{
			result.setOutputString("");
		}

		// save result
		this.setCommandResult(result);

		System.out.println("-" + t + ": " + t.getId());
		return Status.OK_STATUS;
	}

	/**
	 * executeBlock
	 * 
	 * @param threadContext
	 * @param rubyContext
	 * @return
	 */
	protected String executeBlock(ScriptingContainer container)
	{
		// create context
		Ruby runtime = container.getRuntime();
		ThreadContext threadContext = runtime.getCurrentContext();
		IRubyObject[] args = new IRubyObject[] { JavaEmbedUtils.javaToRuby(runtime, this._context) };
		IRubyObject rubyContext = ScriptUtils.instantiateClass(ScriptUtils.RADRAILS_MODULE, CONTEXT_RUBY_CLASS, args);
		String resultText = null;
		
		// assume that we've executed successfully
		this._executedSuccessfully = true;
		
		try
		{
			// invoke the block
			RubyProc proc = this._command.getInvokeBlock();
			runtime.incrementConstantGeneration();
			IRubyObject result = proc.call(threadContext, new IRubyObject[] { rubyContext });
			
			// close any streams we have
			//this.closeStreams();
			
			// process return result, if any
			if (result != null && result.isNil() == false)
			{
				resultText = result.asString().asJavaString();
			}
		}
		catch (RaiseException e)
		{
			if ((e.getException() instanceof RubySystemExit) && this._context.isForcedExit())
			{
				// should be from the exit call in exit_with_message
				resultText = this._context.get(OUTPUT_PROPERTY).toString();
			}
			else
			{
				String message = MessageFormat.format( //
					Messages.CommandElement_Error_Processing_Command_Block, //
					new Object[] { this._command.getDisplayName(), this._command.getPath(), e.getMessage() } //
				); //

				ScriptUtils.logErrorWithStackTrace(message, e);
				this._executedSuccessfully = false;
			}
		}
		catch (Exception e)
		{
			String message = MessageFormat.format( //
				Messages.CommandElement_Error_Processing_Command_Block, //
				new Object[] { this._command.getDisplayName(), this._command.getPath(), e.getMessage() } //
			); //

			ScriptUtils.logErrorWithStackTrace(message, e);
			this._executedSuccessfully = false;
		}
		
		return resultText;
	}

	/**
	 * closeStreams
	 */
	protected void closeStreams()
	{
		InputStream stdin = this._context.getInputStream();
		OutputStream stdout = this._context.getOutputStream();
		
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
	 * setCommandResult
	 * 
	 * @param result
	 */
	protected void setCommandResult(CommandResult result)
	{
		this._result = result;
	}
}
