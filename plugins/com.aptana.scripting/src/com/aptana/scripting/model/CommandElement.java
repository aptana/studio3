package com.aptana.scripting.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.ParseException;
import org.jruby.Ruby;
import org.jruby.RubyHash;
import org.jruby.RubyProc;
import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.Activator;
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptUtils;
import com.aptana.scripting.ScriptingEngine;

public class CommandElement extends AbstractBundleElement
{
	private static final InputType[] NO_TYPES = new InputType[0];
	private static final String[] NO_KEY_BINDINGS = new String[0];
	private static final String TO_ENV_METHOD_NAME = "to_env"; //$NON-NLS-1$

	private String[] _triggers;
	private String _invoke;
	private RubyProc _invokeBlock;
	private Map<Platform, String[]> _keyBindings;
	private InputType[] _inputTypes;
	private String _inputPath;
	private OutputType _outputType;
	private String _outputPath;
	private boolean _async;
	private RunType _runType;
	private Ruby _runtime;

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
		this._runType = Activator.getDefaultRunType();
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
			AbstractCommandJob job = null;
			
			// determine if we are running asynchronously taking the output type into account
			boolean async = (this._async && this._outputType.allowAsync());

			// set default output type, this may be changed by context.exit_with_message
			context.setOutputType(this._outputType);

			// create job based on invocation type
			if (this.isShellCommand())
			{
				job = new CommandScriptJob(this, context);
			}
			else if (this.isBlockCommand())
			{
				// create output stream and attach to context
				context.setOutputStream(new ByteArrayOutputStream());

				job = new CommandBlockJob(this, context, this.getOwningBundle().getLoadPaths());
			}

			// run the job, if we have one
			if (job != null)
			{
				try
				{
					job.run("Execute '" + this.getDisplayName() + "'", this._runType, async);
				}
				catch (InterruptedException e)
				{
					String message = MessageFormat.format(Messages.CommandElement_Error_Executing_Command,
							new Object[] { this.getDisplayName(), this.getPath() });

					ScriptUtils.logErrorWithStackTrace(message, e);
				}

				// get result, using a default shell if we're running async
				result = (async && this._runType != RunType.CURRENT_THREAD) ? new CommandResult(this, context) : job.getCommandResult();
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
					String normalizedKeyBinding = ScriptUtils.normalizeKeyBinding(binding);
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
	 * getRuntime
	 * 
	 * @return
	 */
	public Ruby getRuntime()
	{
		return this._runtime;
	}

	/**
	 * getRunType
	 * 
	 * @return
	 */
	public String getRunType()
	{
		return this._runType.getName();
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
	 * isAsync
	 * 
	 * @return
	 */
	public boolean isAsync()
	{
		return this._async;
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
						String message = MessageFormat.format(Messages.CommandElement_Error_Building_Env_Variables,
								new Object[] { entry.getKey(), this.getDisplayName(), this.getPath(), e.getMessage() });

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
	 * setAsync
	 * 
	 * @param value
	 */
	public void setAsync(boolean value)
	{
		this._async = value;
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
		this.setRuntime((block != null) ? block.getRuntime() : null);
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
	 * setRunType
	 * 
	 * @param type
	 */
	public void setRunType(String type)
	{
		this._runType = RunType.get(type);
	}

	/**
	 * setRuntime
	 * 
	 * @param object
	 */
	public void setRuntime(IRubyObject object)
	{
		this.setRuntime((object != null) ? object.getRuntime() : null);
	}

	/**
	 * setRuntime
	 * 
	 * @param object
	 */
	public void setRuntime(Ruby runtime)
	{
		this._runtime = runtime;
	}

	/**
	 * setRunType
	 * 
	 * @param type
	 */
	public void setRunType(RunType type)
	{
		this._runType = type;
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
