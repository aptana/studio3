package com.aptana.scripting.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.ParseException;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyProc;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptingEngine;

public class CommandElement extends AbstractBundleElement
{
	private static final InputType[] NO_TYPES = new InputType[0];
	
	private String[] _triggers;
	private String _invoke;
	private RubyProc _invokeBlock;
	private String _keyBinding;
	private InputType[] _inputTypes;
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
		String resultText = ""; //$NON-NLS-1$
		
		if (this.isExecutable())
		{
			if (this.isShellCommand())
			{
				resultText = this.invokeStringCommand(context);
			}
			else if (this.isBlockCommand())
			{
				resultText = this.invokeBlockCommand(context);
			}
		}
		
		return new CommandResult(resultText);
	}

	/**
	 * getElementName
	 */
	protected String getElementName()
	{
		return "command";
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
	public String getKeyBinding()
	{
		return this._keyBinding;
	}
	
	/**
	 * getKeySequence
	 * 
	 * @return
	 */
	public KeySequence getKeySequence()
	{
		KeySequence result = null;
		
		try
		{
			result = KeySequence.getInstance(this._keyBinding);
		}
		catch (ParseException e)
		{
			String message = MessageFormat.format(
				Messages.CommandElement_Invalid_Key_Binding,
				new Object[] { this.getDisplayName(), this.getPath() }
			);
			
			ScriptLogger.logError(message);
		}
		
		return result;
	}

	/**
	 * getOutput
	 * 
	 * @return
	 */
	public String getOutput()
	{
		if (this._outputType == OutputType.OUTPUT_TO_FILE)
		{
			return this._outputPath;
		}
		else
		{
			return this._outputType.getName();
		}
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
	private String invokeBlockCommand(CommandContext context)
	{
		Ruby runtime = ScriptingEngine.getInstance().getScriptingContainer().getRuntime();
		ThreadContext threadContext = runtime.getCurrentContext();
		String resultText = ""; //$NON-NLS-1$
		
		try
		{
			RubyModule radrails = runtime.getModule("RadRails");
			RubyClass rclass = radrails.getClass("Context");
			IRubyObject obj = JavaEmbedUtils.javaToRuby(runtime, context);
			IRubyObject rubyContext = rclass.newInstance(threadContext, new IRubyObject[] { obj }, null);
			
			IRubyObject result = this._invokeBlock.call(
				threadContext,
				new IRubyObject[] { rubyContext }
			);
			
			if (result != null)
			{
				resultText = result.asString().asJavaString();
			}
		}
		catch (Exception e)
		{
			String message = MessageFormat.format(
				Messages.CommandElement_Error_Processing_Command_Block,
				new Object[] { this.getDisplayName(), this.getPath(), e.getMessage() }
			);
			
			ScriptLogger.logError(message);
		}
		
		return resultText;
	}

	/**
	 * invokeStringCommand
	 * 
	 * @return
	 */
	private String invokeStringCommand(CommandContext context)
	{
		// TODO: hardly a robust implementation, but enough to start testing
		// functionality
		
		String OS = Platform.getOS();
		File tempFile = null;
		String result = ""; //$NON-NLS-1$
		
		try
		{
			// create temporary file for execution
			tempFile = File.createTempFile(
				"command_temp_", //$NON-NLS-1$
				(OS.equals(Platform.OS_WIN32) ? ".bat" : ".sh") //$NON-NLS-1$ //$NON-NLS-2$
			);
			
			// dump "invoke" content into temp file
			PrintWriter pw = new PrintWriter(tempFile);
			pw.print(this._invoke);
			pw.close();
			
			List<String> commands = new ArrayList<String>();
			ProcessBuilder builder = new ProcessBuilder();
			
			if (context != null)
			{
				Map<String, Object> contextMap = context.getMap();
				
				if (contextMap != null)
				{
					Map<String, String> environment = builder.environment();
					
					for (Map.Entry<String, Object> entry : context.getMap().entrySet())
					{
						environment.put(entry.getKey().toUpperCase(), entry.getValue().toString());
					}
				}
			}
			
			if (OS.equals(Platform.OS_MACOSX) || OS.equals(Platform.OS_LINUX))
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
			}
			
			result = buffer.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (tempFile != null && tempFile.exists())
			{
				tempFile.delete();
			}
		}
		
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
		
		// output key binding, intput, and output settings
		printer.printWithIndent("keys: ").println(this._keyBinding); //$NON-NLS-1$
		
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
	public void setKeyBinding(String keyBinding)
	{
		this._keyBinding = keyBinding;
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
