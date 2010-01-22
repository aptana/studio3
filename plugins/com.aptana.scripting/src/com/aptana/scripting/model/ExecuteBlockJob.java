package com.aptana.scripting.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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
import org.jruby.RubyGlobal.InputGlobalVariable;
import org.jruby.RubyGlobal.OutputGlobalVariable;
import org.jruby.exceptions.RaiseException;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.ScriptUtils;

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
	private RubyHash _originalEnvironment;

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
	protected void applyEnvironment()
	{
		Ruby runtime = this.getRuntime();
		IRubyObject env = runtime.getObject().getConstant(ENV_PROPERTY);

		if (env != null && env instanceof RubyHash)
		{
			RubyHash hash = (RubyHash) env;
			Map<String, String> environment = new HashMap<String, String>();
			
			// save copy for later
			this._originalEnvironment = (RubyHash) hash.dup();

			this._command.populateEnvironment(this._context.getMap(), environment);
			hash.putAll(environment);
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

		// turn off verbose mode (and warnings)
		boolean isVerbose = runtime.isVerbose();
		runtime.setVerbose(runtime.getNil());

		// set stdin/out/err and console
		this.setReader(this._context.getInputStream());
		this.setWriter(this._context.getOutputStream());
		this.setErrorWriter(this._context.getErrorStream());
		this.setConsole(this._context.getConsoleStream());

		// restore verbose mode
		runtime.setVerbose((isVerbose) ? runtime.getTrue() : runtime.getFalse());
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
	 * executeBlock
	 * 
	 * @param threadContext
	 * @param rubyContext
	 * @return
	 */
	protected String executeBlock()
	{
		// create context
		Ruby runtime = this.getRuntime();
		ThreadContext threadContext = runtime.getCurrentContext();
		IRubyObject rubyContext = ScriptUtils.instantiateClass(runtime, ScriptUtils.RADRAILS_MODULE,
				CONTEXT_RUBY_CLASS, JavaEmbedUtils.javaToRuby(runtime, this._context));
		String resultText = null;

		// assume that we've executed successfully
		this._executedSuccessfully = true;

		try
		{
			// invoke the block
			runtime.incrementConstantGeneration();
			IRubyObject result = this._command.getInvokeBlock().call(threadContext, new IRubyObject[] { rubyContext });

			// close any streams we have
			// this.closeStreams();

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
	 * getCommandResult
	 * 
	 * @return
	 */
	public CommandResult getCommandResult()
	{
		return this._result;
	}

	/**
	 * getRuntime
	 * 
	 * @return
	 */
	protected Ruby getRuntime()
	{
		Ruby result = null;

		if (this._command != null)
		{
			RubyProc proc = this._command.getInvokeBlock();

			if (proc != null)
			{
				result = proc.getRuntime();
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor)
	{
		Ruby runtime = this.getRuntime();

		// apply load paths
		this.applyLoadPaths(runtime);

		// apply streams
		this.applyStreams();

		// setup ENV
		this.applyEnvironment();

		// set default output type, this may be changed by context.exit_with_message
		this._context.setOutputType(this._command.getOutputType());

		// execute block
		String resultText = this.executeBlock();
		
		// unapply load paths
		this.unapplyLoadPaths(runtime);
		
		// unapply environment
		this.unapplyEnvironment();

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

		return Status.OK_STATUS;
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

	/**
	 * setConsole
	 * 
	 * @param ostream
	 */
	protected void setConsole(OutputStream ostream)
	{
		if (ostream != null)
		{
			Ruby runtime = getRuntime();
			RubyIO io = new RubyIO(runtime, ostream);

			io.getOpenFile().getMainStream().setSync(true);
			runtime.getGlobalVariables().set(CONSOLE_VARIABLE, io);
			runtime.getObject().setConstant(CONSOLE_CONSTANT, io);
		}
	}

	/**
	 * setErrorWriter - based on ScriptContainer#setErrorWriter
	 * 
	 * @param ostream
	 */
	protected void setErrorWriter(OutputStream ostream)
	{
		if (ostream != null)
		{
			PrintStream pstream = new PrintStream(ostream);
			Ruby runtime = getRuntime();
			RubyIO io = new RubyIO(runtime, pstream);

			io.getOpenFile().getMainStream().setSync(true);
			runtime.defineVariable(new OutputGlobalVariable(runtime, "$stderr", io));
			runtime.defineGlobalConstant("STDERR", io);
			runtime.getGlobalVariables().alias("$deferr", "$stderr");
		}
	}

	/**
	 * setReader - based on ScriptContainer#setReader
	 * 
	 * @param istream
	 */
	protected void setReader(InputStream istream)
	{
		if (istream != null)
		{
			Ruby runtime = this.getRuntime();
			RubyIO io = new RubyIO(runtime, istream);

			io.getOpenFile().getMainStream().setSync(true);
			runtime.defineVariable(new InputGlobalVariable(runtime, "$stdin", io));
			runtime.defineGlobalConstant("STDIN", io);
		}
	}

	/**
	 * setWriter - based on ScriptContainer#setWriter
	 * 
	 * @param ostream
	 */
	protected void setWriter(OutputStream ostream)
	{
		if (ostream != null)
		{
			PrintStream pstream = new PrintStream(ostream);
			Ruby runtime = this.getRuntime();
			RubyIO io = new RubyIO(runtime, pstream);

			io.getOpenFile().getMainStream().setSync(true);
			runtime.defineVariable(new OutputGlobalVariable(runtime, "$stdout", io));
			runtime.defineGlobalConstant("STDOUT", io);
			runtime.getGlobalVariables().alias("$>", "$stdout");
			runtime.getGlobalVariables().alias("$defout", "$stdout");
		}
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
}
