package com.aptana.scripting.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.ParseException;
import org.jruby.Ruby;
import org.jruby.RubyProc;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptingEngine;

public class CommandElement extends AbstractBundleElement
{
	private String _trigger;
	private String _invoke;
	private RubyProc _invokeBlock;
	private String _keyBinding;
	private InputType _inputType;
	private OutputType _outputType;
	
	/**
	 * Snippet
	 * 
	 * @param path
	 */
	public CommandElement(String path)
	{
		super(path);
		
		this._inputType = InputType.UNDEFINED;
		this._outputType = OutputType.UNDEFINED;
	}

	/**
	 * execute
	 * 
	 * @param context
	 * @return
	 */
	public CommandResult execute(CommandContext context)
	{
		String resultText = ""; //$NON-NLS-1$
		
		if (this.isExecutable())
		{
			if (this.isShellCommand())
			{
				resultText = this.invokeStringCommand();
			}
			else if (this.isBlockCommand())
			{
				Ruby runtime = ScriptingEngine.getInstance().getScriptingContainer().getRuntime();
				ThreadContext threadContext = runtime.getCurrentContext();
				
				try
				{
					IRubyObject result = this._invokeBlock.call(threadContext, new IRubyObject[0]);
					
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
			}
		}
		
		return new CommandResult(resultText);
	}

	/**
	 * getInput
	 * 
	 * @return
	 */
	public String getInputType()
	{
		return this._inputType.getName();
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
				"Unable to convert {0} to an Eclipse key sequence in {0}: {1}",
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
	public String getOutputType()
	{
		return this._outputType.getName();
	}
	
	/**
	 * getTrigger
	 * 
	 * @return
	 */
	public String getTrigger()
	{
		return this._trigger;
	}

	/**
	 * invokeStringCommand
	 * 
	 * @return
	 */
	private String invokeStringCommand()
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
			
			if (OS.equals(Platform.OS_MACOSX) || OS.equals(Platform.OS_LINUX))
			{
				commands.add("/bin/bash"); //$NON-NLS-1$
			}
			else
			{
				commands.add("cmd"); //$NON-NLS-1$
			}
			commands.add(tempFile.getAbsolutePath());
			
			// setup command-line
			builder.command(commands);
			
			// setup working directory
			String path = this.getPath();
			
			if (path != null && path.length() > 0)
			{
				builder.directory(new File(this.getPath()).getParentFile());
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
	 * setInputType
	 * 
	 * @param input
	 */
	public void setInputType(String input)
	{
		this._inputType = InputType.get(input);
	}
	
	/**
	 * setInputType
	 * 
	 * @param type
	 */
	public void setInputType(InputType type)
	{
		this._inputType = type;
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
	 * setOutput
	 * 
	 * @param output
	 */
	public void setOutputType(String output)
	{
		this._outputType = OutputType.get(output);
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
	 * setTrigger
	 * 
	 * @param trigger
	 */
	public void setTrigger(String trigger)
	{
		this._trigger = trigger;
	}
	
	/**
	 * toSource
	 */
	protected void toSource(SourcePrinter printer)
	{
		printer.printWithIndent("command \"").print(this.getDisplayName()).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$
		
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this.getScope()); //$NON-NLS-1$
		if (this._invoke != null)
		{
			printer.printWithIndent("invoke: ").println(this._invoke); //$NON-NLS-1$
		}
		if (this._invokeBlock != null)
		{
			printer.printWithIndent("block: ").println(this._invokeBlock.to_s().asJavaString()); //$NON-NLS-1$
		}
		printer.printWithIndent("keys: ").println(this._keyBinding); //$NON-NLS-1$
		printer.printWithIndent("output: ").println(this._outputType.getName()); //$NON-NLS-1$
		printer.printWithIndent("trigger: ").println(this.getTrigger()); //$NON-NLS-1$
		
		printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
	}
}
