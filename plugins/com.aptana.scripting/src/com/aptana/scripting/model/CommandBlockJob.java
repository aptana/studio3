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
import org.jruby.RubyClass;
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

public class CommandBlockJob extends AbstractScriptJob
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

	private CommandElement _command;
	private CommandContext _context;
	private boolean _executedSuccessfully;
	private CommandResult _result;
	private RubyHash _originalEnvironment;
	private Map<String, String> _contributedEnvironment;
	private IRubyObject _oldReader;
	private IRubyObject _oldWriter;
	private IRubyObject _oldErrorWriter;
	private IRubyObject _oldConsole;

	/**
	 * ExecuteScriptJob
	 * 
	 * @param command
	 * @param context
	 */
	public CommandBlockJob(CommandElement command, CommandContext context)
	{
		this("Execute JRuby Block", command, context, null); //$NON-NLS-1$
	}

	/**
	 * ExecuteScriptJob
	 * 
	 * @param block
	 * @param loadPaths
	 */
	public CommandBlockJob(CommandElement command, CommandContext context, List<String> loadPaths)
	{
		this("Execute JRuby Block", command, context, loadPaths); //$NON-NLS-1$
	}

	/**
	 * ExecuteScriptJob
	 * 
	 * @param name
	 * @param block
	 */
	public CommandBlockJob(String name, CommandElement command, CommandContext context)
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
	public CommandBlockJob(String name, CommandElement command, CommandContext context, List<String> loadPaths)
	{
		super(name, loadPaths);

		this._command = command;
		this._context = context;
		this._contributedEnvironment = new HashMap<String, String>();
		this._command.populateEnvironment(this._context.getMap(), this._contributedEnvironment);
	}

	/**
	 * afterExecute
	 */
	protected void afterExecute()
	{
		Ruby runtime = this.getRuntime();
		
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

			hash.putAll(this._contributedEnvironment);
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
		this._oldReader = this.setReader(this._context.getInputStream());
		this._oldWriter = this.setWriter(this._context.getOutputStream());
		this._oldErrorWriter = this.setErrorWriter(this._context.getErrorStream());
		this._oldConsole = this.setConsole(this._context.getConsoleStream());

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
		this._context.setOutputType(this._command.getOutputType());
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
		String resultText = null;
		
		// execute block
		synchronized (this.getRuntime())
		{
			this.beforeExecute();
			resultText = this.executeBlock();
			this.afterExecute();
		}

		// process result
		CommandResult result = new CommandResult(this._command, this._context);

		result.setExecutedSuccessfully(this._executedSuccessfully);

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
			result.setOutputString(""); //$NON-NLS-1$
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
			setConsole(new RubyIO(runtime, ostream));
		}
		
		return oldValue;
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

			setErrorWriter(io);
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
		
		runtime.defineVariable(new OutputGlobalVariable(runtime, STDERR_GLOBAL, io));
		runtime.defineGlobalConstant(STDERR_CONSTANT, io);
		runtime.getGlobalVariables().alias(DEFERR_GLOBAL, STDERR_GLOBAL);
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
		
		runtime.defineVariable(new InputGlobalVariable(runtime, STDIN_GLOBAL, io));
		runtime.defineGlobalConstant(STDIN_CONSTANT, io);
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
	 * setWriter
	 * 
	 * @param io
	 */
	protected void setWriter(IRubyObject io)
	{
		Ruby runtime = this.getRuntime();
		
		runtime.defineVariable(new OutputGlobalVariable(runtime, STDOUT_GLOBAL, io));
		runtime.defineGlobalConstant(STDOUT_CONSTANT, io);
		runtime.getGlobalVariables().alias(STDOUT_GLOBAL2, STDOUT_GLOBAL);
		runtime.getGlobalVariables().alias(DEFOUT_GLOBAL, STDOUT_GLOBAL);
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
