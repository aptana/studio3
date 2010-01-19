package com.aptana.scripting.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.jruby.Ruby;
import org.jruby.RubyHash;
import org.jruby.RubyProc;
import org.jruby.RubySystemExit;
import org.jruby.embed.ScriptingContainer;
import org.jruby.exceptions.RaiseException;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptUtils;
import com.aptana.scripting.ScriptingEngine;

public class CommandElement extends AbstractBundleElement
{
	private static final InputType[] NO_TYPES = new InputType[0];
	private static final String[] NO_KEY_BINDINGS = new String[0];
	
	private static final String CONTEXT_RUBY_CLASS = "Context"; //$NON-NLS-1$
	private static final String ENV_PROPERTY = "ENV"; //$NON-NLS-1$
	private static final String OUTPUT_PROPERTY = "output"; //$NON-NLS-1$
	private static final String TO_ENV_METHOD_NAME = "to_env"; //$NON-NLS-1$

	private static final Pattern CONTROL_PLUS = Pattern.compile("control" + Pattern.quote(KeyStroke.KEY_DELIMITER), Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final String CTRL_PLUS = Matcher.quoteReplacement(IKeyLookup.CTRL_NAME + KeyStroke.KEY_DELIMITER);
	private static final Pattern OPTION_PLUS = Pattern.compile("option" + Pattern.quote(KeyStroke.KEY_DELIMITER), Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final String ALT_PLUS = Matcher.quoteReplacement(IKeyLookup.ALT_NAME + KeyStroke.KEY_DELIMITER);

	/**
	 * Normalize the keyBinding string.
	 * <p>
	 * Convert control+ to CTRL+ Convert option+ to ALT+
	 * 
	 * @param keyBinding
	 * @return
	 */
	static String normalizeKeyBinding(String keyBinding)
	{
		String result = null;

		if (keyBinding != null)
		{
			result = CONTROL_PLUS.matcher(keyBinding).replaceAll(CTRL_PLUS); // Convert control+ to CTRL+
			result = OPTION_PLUS.matcher(result).replaceAll(ALT_PLUS); // Convert option+ to ALT+
		}

		return result;
	}
	private String[] _triggers;
	private String _invoke;
	private RubyProc _invokeBlock;
	private Map<Platform, String[]> _keyBindings;
	private InputType[] _inputTypes;
	private String _inputPath;
	private OutputType _outputType;
	private String _outputPath;

	private String _workingDirectoryPath;

	private WorkingDirectoryType _workingDirectoryType;

	/**
	 * Snippet
	 * 
	 * @param path
	 */
	public CommandElement(String path)
	{
		super(path);

		this._inputTypes = NO_TYPES;
		this._outputType = OutputType.UNDEFINED;
		this._workingDirectoryType = WorkingDirectoryType.UNDEFINED;
	}

	/**
	 * createCommandContext
	 * 
	 * @return
	 */
	public CommandContext createCommandContext()
	{
		return new CommandContext(this);
	}

	/**
	 * execute
	 * 
	 * @return
	 */
	public CommandResult execute()
	{
		return this.execute(this.createCommandContext());
	}

	/**
	 * execute
	 * 
	 * @param map
	 * @return
	 */
	public CommandResult execute(CommandContext context)
	{
		CommandResult result = null;

		if (this.isExecutable())
		{
			if (this.isShellCommand())
			{
				result = this.invokeStringCommand(context);
			}
			else if (this.isBlockCommand())
			{
				result = this.invokeBlockCommand(context);
			}
		}

		if (result != null)
		{
			// grab input type so we can report back which input was used
			String inputTypeString = (String) context.get(CommandContext.INPUT_TYPE);
			InputType inputType = InputType.get(inputTypeString);

			result.setInputType(inputType);
		}

		return result;
	}
	
	/**
	 * getElementName
	 */
	protected String getElementName()
	{
		return "command"; //$NON-NLS-1$
	}
	
	/**
	 * getInputPath
	 * 
	 * @return
	 */
	public String getInputPath()
	{
		return this._inputPath;
	}
	
	/**
	 * getInput
	 * 
	 * @return
	 */
	public InputType[] getInputTypes()
	{
		return this._inputTypes;
	}

	/**
	 * getInvoke
	 * 
	 * @return
	 */
	public String getInvoke()
	{
		return this._invoke;
	}

	/**
	 * getInvokeBlock
	 * 
	 * @return
	 */
	public RubyProc getInvokeBlock()
	{
		return this._invokeBlock;
	}

	/**
	 * getKeyBinding
	 * 
	 * @return
	 */
	public String[] getKeyBindings()
	{
		Platform[] platforms = Platform.getCurrentPlatforms();
		String[] result = null;

		if (this._keyBindings == null)
		{
			return NO_KEY_BINDINGS;
		}

		for (Platform platform : platforms)
		{
			if (platform != Platform.UNDEFINED)
			{
				result = this._keyBindings.get(platform);
				
				if (result != null && result.length > 0)
				{
					break;
				}
			}
		}

		if (result == null)
		{
			result = this._keyBindings.get(Platform.ALL);
		}

		return result;
	}

	/**
	 * getKeySequence
	 * 
	 * @return
	 */
	public KeySequence[] getKeySequences()
	{
		String[] bindings = this.getKeyBindings();
		List<KeySequence> result = new ArrayList<KeySequence>();

		if (bindings != null && bindings.length > 0)
		{
			for (String binding : bindings)
			{
				try
				{
					// Need to convert the format
					String normalizedKeyBinding = normalizeKeyBinding(binding);
					KeySequence sequence = KeySequence.getInstance(normalizedKeyBinding);

					result.add(sequence);
				}
				catch (ParseException e)
				{
					String message = MessageFormat.format(Messages.CommandElement_Invalid_Key_Binding, new Object[] {
							binding, this.getDisplayName(), this.getPath(), e.getMessage() });

					ScriptLogger.logError(message);
				}
			}
		}
		return result.toArray(new KeySequence[result.size()]);
	}

	/**
	 * getOutputPath
	 * 
	 * @return
	 */
	public String getOutputPath()
	{
		return this._outputPath;
	}

	/**
	 * getOutputType
	 * 
	 * @return
	 */
	public String getOutputType()
	{
		return this._outputType.getName();
	}

	/**
	 * getTrigger
	 * 
	 * @return
	 */
	public String[] getTriggers()
	{
		return this._triggers;
	}

	/**
	 * getWorkingDirectory
	 * 
	 * @return
	 */
	public String getWorkingDirectory()
	{
		switch (this._workingDirectoryType)
		{
			case CURRENT_BUNDLE:
				return new File(this.getPath()).getParentFile().toString();

			case PATH:
				return this._workingDirectoryPath;

				// FIXME: implement for story https://www.pivotaltracker.com/story/show/2031417
				// can't implement these yet because they require us to hook into higher level functionality in the
				// editor.common and explorer plugins. AAAARGH.
			case UNDEFINED:
			case CURRENT_PROJECT:
			case CURRENT_FILE:
			default:
				return new File(this.getPath()).getParentFile().toString();
		}
	}

	/**
	 * invokeBlockCommand
	 * 
	 * @param resultText
	 * @return
	 */
	private CommandResult invokeBlockCommand(CommandContext context)
	{
		ScriptingContainer container = ScriptingEngine.getInstance().getScriptingContainer(); 
		Ruby runtime = container.getRuntime();
		Map<String, String> environment = new HashMap<String, String>();
		boolean executedSuccessfully = true;
		String resultText = ""; //$NON-NLS-1$

		try
		{
			ThreadContext threadContext = runtime.getCurrentContext();
			IRubyObject[] args = new IRubyObject[] { JavaEmbedUtils.javaToRuby(runtime, context) };
			IRubyObject rubyContext = ScriptUtils.instantiateClass(ScriptUtils.RADRAILS_MODULE, CONTEXT_RUBY_CLASS, args);

			// TODO: Keep track of any env vars we may have clobbered here and restore back their original values!
			IRubyObject env = runtime.getObject().getConstant(ENV_PROPERTY);
			
			if (env != null && env instanceof RubyHash)
			{
				RubyHash hash = (RubyHash) env;
				populateEnvironment(context.getMap(), environment);
				hash.putAll(environment);
			}

			// set STDOUT
			StringWriter writer = new StringWriter();
			container.setWriter(writer);
			context.put(OUTPUT_PROPERTY, container.getOut());
			
			// do "turn off warnings" hack and set STDIN
			boolean isVerbose = runtime.isVerbose();
			runtime.setVerbose(runtime.getNil());
			container.setReader(new BufferedReader(new InputStreamReader(context.getInputStream())));
			runtime.setVerbose((isVerbose) ? runtime.getTrue() : runtime.getFalse());
			
			// set default output type, this may be changed by context.exit_with_message
			context.setOutputType(this._outputType);
			
			// invoke the block
			IRubyObject result = this._invokeBlock.call(threadContext, new IRubyObject[] { rubyContext });
			String output = writer.toString();

			// process return result, if any
			if (result != null && result.isNil() == false)
			{
				resultText = result.asString().asJavaString();
			}
			else if (output != null && output.length() > 0)
			{
				resultText = output;
			}
		}
		catch (RaiseException e)
		{
			if ((e.getException() instanceof RubySystemExit) && context.isForcedExit())
			{
				// should be from the exit call in exit_with_message
				resultText = context.get(OUTPUT_PROPERTY).toString();
			}
			else
			{
				String message = MessageFormat.format(
					Messages.CommandElement_Error_Processing_Command_Block,
					new Object[] { this.getDisplayName(), this.getPath(), e.getMessage() }
				);
				
				ScriptUtils.logErrorWithStackTrace(message, e);
				executedSuccessfully = false;
			}
		}
		catch (Exception e)
		{
			String message = MessageFormat.format(
				Messages.CommandElement_Error_Processing_Command_Block,
				new Object[] { this.getDisplayName(), this.getPath(), e.getMessage() }
			);
			
			ScriptUtils.logErrorWithStackTrace(message, e);
			executedSuccessfully = false;
		}
		
		// Now clear the environment
		IRubyObject env = runtime.getObject().getConstant(ENV_PROPERTY);
		
		if (env != null && env instanceof RubyHash)
		{
			RubyHash hash = (RubyHash) env;
			
			for (String key : environment.keySet())
			{
				hash.remove(key);
			}
		}

		CommandResult result = new CommandResult(resultText);
		result.setExecutedSuccessfully(executedSuccessfully);
		result.setCommand(this);
		result.setContext(context);

		return result;
	}

	/**
	 * invokeStringCommand
	 * 
	 * @return
	 */
	private CommandResult invokeStringCommand(CommandContext context)
	{
		// TODO: hardly a robust implementation, but enough to start testing
		// functionality

		String OS = org.eclipse.core.runtime.Platform.getOS();
		File tempFile = null;
		String resultText = ""; //$NON-NLS-1$
		boolean executedSuccessfully = true;
		int exitValue = 0;

		try
		{
			// create temporary file for execution
			tempFile = File.createTempFile("command_temp_", //$NON-NLS-1$
					(OS.equals(org.eclipse.core.runtime.Platform.OS_WIN32) ? ".bat" : ".sh") //$NON-NLS-1$ //$NON-NLS-2$
					);

			// dump "invoke" content into temp file
			PrintWriter pw = new PrintWriter(tempFile);
			pw.print(this._invoke);
			pw.close();

			// create process builder
			ProcessBuilder builder = new ProcessBuilder();

			// augment environment with the context map
			if (context != null)
			{
				this.populateEnvironment(context.getMap(), builder.environment());
			}

			// create the command to execute
			List<String> commands = new ArrayList<String>();

			if (OS.equals(org.eclipse.core.runtime.Platform.OS_MACOSX)
					|| OS.equals(org.eclipse.core.runtime.Platform.OS_LINUX))
			{
				// FIXME: should we be using the user's preferred shell instead of hardcoding?
				commands.add("/bin/bash"); //$NON-NLS-1$
			}
			else
			{
				// FIXME: we should allow use of other shells on Windows: PowerShell, cygwin, etc.
				commands.add("cmd"); //$NON-NLS-1$
			}

			commands.add(tempFile.getAbsolutePath());

			// setup command-line
			builder.command(commands);

			// setup working directory
			String path = this.getWorkingDirectory();
			if (path != null && path.length() > 0)
			{
				builder.directory(new File(path));
			}

			// run process and get output
			StringBuffer buffer = new StringBuffer();
			Process process = builder.start();

			InputStream is = process.getInputStream();
			byte[] line = new byte[1024];
			int count;

			try
			{
				while ((count = is.read(line)) != -1)
				{
					buffer.append(new String(line, 0, count));
				}
			}
			catch (IOException e)
			{
				ScriptLogger.logError(e.getMessage());
				executedSuccessfully = false;
			}

			exitValue = process.waitFor();
			resultText = buffer.toString();
			executedSuccessfully = (exitValue == 0);
		}
		catch (IOException e)
		{
			ScriptLogger.logError(e.getMessage());
			executedSuccessfully = false;
		}
		catch (InterruptedException e)
		{
			ScriptLogger.logError(e.getMessage());
			executedSuccessfully = false;
		}
		finally
		{
			if (tempFile != null && tempFile.exists())
			{
				tempFile.delete();
			}
		}

		CommandResult result = new CommandResult(resultText);
		result.setReturnValue(exitValue);
		result.setExecutedSuccessfully(executedSuccessfully);

		return result;
	}

	/**
	 * isBlockCommand
	 * 
	 * @return
	 */
	public boolean isBlockCommand()
	{
		return (this._invokeBlock != null);
	}

	/**
	 * isExecutable
	 * 
	 * @return
	 */
	public boolean isExecutable()
	{
		return ((this._invoke != null && this._invoke.length() > 0) || this._invokeBlock != null);
	}

	/**
	 * isShellCommand
	 * 
	 * @return
	 */
	public boolean isShellCommand()
	{
		return (this._invokeBlock == null && this._invoke != null && this._invoke.length() > 0);
	}

	/**
	 * populateEnvironment
	 * 
	 * @param contextMap
	 * @param environment
	 */
	void populateEnvironment(Map<String, Object> contextMap, Map<String, String> environment)
	{
		for (Map.Entry<String, Object> entry : contextMap.entrySet())
		{
			Object valueObject = entry.getValue();
			String key = entry.getKey().toUpperCase();

			if (valueObject instanceof IRubyObject)
			{
				IRubyObject rubyObject = (IRubyObject) valueObject;

				if (rubyObject.respondsTo(TO_ENV_METHOD_NAME))
				{
					Ruby runtime = ScriptingEngine.getInstance().getScriptingContainer().getRuntime();
					ThreadContext threadContext = runtime.getCurrentContext();
					
					try
					{
						IRubyObject methodResult = rubyObject.callMethod(threadContext, TO_ENV_METHOD_NAME);
	
						if (methodResult instanceof RubyHash)
						{
							RubyHash environmentHash = (RubyHash) methodResult;
	
							for (Object hashKey : environmentHash.keySet())
							{
								environment.put(hashKey.toString(), environmentHash.get(hashKey).toString());
							}
						}
					}
					catch (RaiseException e)
					{
						String message = MessageFormat.format(
							"An error occurred while building environment variables for the ''{0}'' context property in the ''{1}'' command ({2}): {3}",
							new Object[] { entry.getKey(), this.getDisplayName(), this.getPath(), e.getMessage() }
						);
						
						ScriptLogger.logError(message);
						e.printStackTrace();
					}
				}
			}
			else if (valueObject instanceof EnvironmentContributor)
			{
				EnvironmentContributor contributor = (EnvironmentContributor) valueObject;
				Map<String, String> contributedEnvironment = contributor.toEnvironment();

				if (contributedEnvironment != null)
				{
					environment.putAll(contributedEnvironment);
				}
			}
			else if (valueObject != null)
			{
				environment.put(key, valueObject.toString());
			}
		}
	}

	/**
	 * printBody
	 */
	protected void printBody(SourcePrinter printer)
	{
		// output path and scope
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this.getScope()); //$NON-NLS-1$

		// output invoke/expansion, if it is defined
		if (this._invoke != null)
		{
			printer.printWithIndent("invoke: ").println(this._invoke); //$NON-NLS-1$
		}

		// output invoke block, if it is defined
		if (this._invokeBlock != null)
		{
			printer.printWithIndent("block: ").println(this._invokeBlock.to_s().asJavaString()); //$NON-NLS-1$
		}

		// output key bindings, if it is defined
		if (this._keyBindings != null && this._keyBindings.size() > 0)
		{
			printer.printlnWithIndent("keys {").increaseIndent(); //$NON-NLS-1$

			for (Map.Entry<Platform, String[]> entry : this._keyBindings.entrySet())
			{
				printer.printWithIndent(entry.getKey().getName()).print(": "); //$NON-NLS-1$

				boolean first = true;

				for (String binding : entry.getValue())
				{
					if (first == false)
					{
						printer.print(", "); //$NON-NLS-1$
					}

					printer.print(binding);

					first = false;
				}

				printer.println();
			}

			printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
		}

		// output a comma-delimited list of input types, if they are defined
		InputType[] types = this.getInputTypes();

		if (types != null && types.length > 0)
		{
			boolean first = true;

			printer.printWithIndent("input: "); //$NON-NLS-1$

			for (InputType type : types)
			{
				if (first == false)
				{
					printer.print(", "); //$NON-NLS-1$
				}

				printer.print(type.getName());

				first = false;
			}

			printer.println();
		}

		// output output type
		printer.printWithIndent("output: ").println(this._outputType.getName()); //$NON-NLS-1$

		// output a comma-delimited list of triggers, if any are defined
		String[] triggers = this.getTriggers();

		if (triggers != null && triggers.length > 0)
		{
			boolean first = true;

			printer.printWithIndent("triggers: "); //$NON-NLS-1$

			for (String trigger : triggers)
			{
				if (first == false)
				{
					printer.print(", "); //$NON-NLS-1$
				}

				printer.print(trigger);

				first = false;
			}

			printer.println();
		}
	}

	/**
	 * setInputPath
	 * 
	 * @param path
	 */
	public void setInputPath(String path)
	{
		this._inputPath = path;
	}
	
	/**
	 * setInputType
	 * 
	 * @param type
	 */
	public void setInputType(InputType type)
	{
		this.setInputType(new InputType[] { type });
	}

	/**
	 * setInputType
	 * 
	 * @param types
	 */
	public void setInputType(InputType[] types)
	{
		this._inputTypes = (types == null) ? NO_TYPES : types;
	}

	/**
	 * setInputType
	 * 
	 * @param input
	 */
	public void setInputType(String input)
	{
		this.setInputType(InputType.get(input));
	}

	/**
	 * setInputType
	 * 
	 * @param types
	 */
	public void setInputType(String[] types)
	{
		InputType[] result = null;

		if (types != null)
		{
			result = new InputType[types.length];

			for (int i = 0; i < types.length; i++)
			{
				result[i] = InputType.get(types[i]);
			}
		}

		this.setInputType(result);
	}

	/**
	 * setInvoke
	 * 
	 * @param invoke
	 */
	public void setInvoke(String invoke)
	{
		this._invoke = invoke;
	}

	/**
	 * setInvokeBlock
	 * 
	 * @param block
	 */
	public void setInvokeBlock(RubyProc block)
	{
		this._invokeBlock = block;
	}

	/**
	 * setKeyBinding
	 * 
	 * @param keyBinding
	 */
	public void setKeyBinding(String OS, String keyBinding)
	{
		if (keyBinding != null && keyBinding.length() > 0)
		{
			this.setKeyBindings(OS, new String[] { keyBinding });
		}
		else
		{
			String message = MessageFormat.format(Messages.CommandElement_Undefined_Key_Binding, new Object[] { this
					.getPath() });

			ScriptLogger.logWarning(message);
		}
	}

	/**
	 * setKeyBindings
	 * 
	 * @param OS
	 * @param keyBindings
	 */
	public void setKeyBindings(String OS, String[] keyBindings)
	{
		Platform bindingOS = Platform.get(OS);

		if (bindingOS != Platform.UNDEFINED)
		{
			if (this._keyBindings == null)
			{
				this._keyBindings = new HashMap<Platform, String[]>();
			}

			this._keyBindings.put(bindingOS, keyBindings);
		}
		else
		{
			String message = MessageFormat.format(Messages.CommandElement_Unrecognized_OS, new Object[] {
					this.getPath(), OS });

			ScriptLogger.logWarning(message);
		}
	}

	/**
	 * setOutputPath
	 * 
	 * @param path
	 */
	public void setOutputPath(String path)
	{
		this._outputPath = path;
	}

	/**
	 * setOutputType
	 * 
	 * @param type
	 */
	public void setOutputType(OutputType type)
	{
		this._outputType = type;
	}

	/**
	 * setOutput
	 * 
	 * @param output
	 */
	public void setOutputType(String output)
	{
		this._outputType = OutputType.get(output);
	}

	/**
	 * setTrigger
	 * 
	 * @param trigger
	 */
	public void setTrigger(String trigger)
	{
		this._triggers = new String[] { trigger };
	}

	/**
	 * setTrigger
	 * 
	 * @param triggers
	 */
	public void setTrigger(String[] triggers)
	{
		this._triggers = triggers;
	}

	/**
	 * setOutputPath
	 * 
	 * @param path
	 */
	public void setWorkingDirectoryPath(String path)
	{
		this._workingDirectoryPath = path;
	}

	/**
	 * setWorkingDirectoryType
	 * 
	 * @param workingDirectory
	 */
	public void setWorkingDirectoryType(String workingDirectory)
	{
		this._workingDirectoryType = WorkingDirectoryType.get(workingDirectory);
	}

	/**
	 * setWorkingDirectoryType
	 * 
	 * @param type
	 */
	public void setWorkingDirectoryType(WorkingDirectoryType type)
	{
		this._workingDirectoryType = type;
	}
}
