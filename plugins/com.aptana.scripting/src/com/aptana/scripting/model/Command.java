package com.aptana.scripting.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jruby.RubyProc;
import org.jruby.anno.JRubyMethod;

public class Command extends TriggerableNode
{
	private String _invoke;
	private RubyProc _invokeBlock;
	private String _keyBinding;
	private String _output;
	/**
	 * Snippet
	 * 
	 * @param path
	 */
	public Command(String path)
	{
		super(path);
	}

	/**
	 * execute
	 * 
	 * @param context
	 * @return
	 */
	public CommandResult execute(CommandContext context)
	{
		String resultText = "";
		
		if (this.isExecutable())
		{
			if (this.isShellCommand())
			{
				resultText = this.invokeStringCommand();
			}
			else if (this.isBlockCommand())
			{
				
			}
		}
		
		return new CommandResult(resultText);
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
		
		List<String> commands = new ArrayList<String>();
		ProcessBuilder builder = new ProcessBuilder();
		String result = "";
		
		commands.add("/bin/bash");
		commands.add("-c");
		commands.add(this._invoke);
		
		// setup command-line
		builder.command(commands);
		
		// setup working directory
		if (this._path != null && this._path.length() > 0)
		{
			builder.directory(new File(this.getPath()));
		}

		// run process and get output
		try
		{
			final Process process = builder.start();
			final StringBuffer buffer = new StringBuffer();
			
			new Thread(new Runnable()
			{
				public void run()
				{
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
				}
			}).start();
			
			result = buffer.toString();
		}
		catch (IOException e)
		{
		}
		
		return result;
	}
	
	/**
	 * getInvoke
	 * 
	 * @return
	 */
	@JRubyMethod(name = "invoke")
	public String getInvoke()
	{
		return this._invoke;
	}
	
	/**
	 * getInvokeBlock
	 * 
	 * @return
	 */
	@JRubyMethod(name = "invoke_block")
	public RubyProc getInvokeBlock()
	{
		return this._invokeBlock;
	}

	/**
	 * getKeyBinding
	 * 
	 * @return
	 */
	@JRubyMethod(name = "key_binding")
	public String getKeyBinding()
	{
		return this._keyBinding;
	}

	/**
	 * getOutput
	 * 
	 * @return
	 */
	@JRubyMethod(name = "output")
	public String getOutput()
	{
		return this._output;
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
	 * isBlockCommand
	 * 
	 * @return
	 */
	public boolean isBlockCommand()
	{
		return (this._invokeBlock != null);
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
	 * setInvoke
	 * 
	 * @param invoke
	 */
	@JRubyMethod(name = "invoke=")
	public void setInvoke(String invoke)
	{
		this._invoke = invoke;
	}
	
	/**
	 * setInvokeBlock
	 * 
	 * @param block
	 */
	@JRubyMethod(name = "invoke_block=")
	public void setInvokeBlock(RubyProc block)
	{
		this._invokeBlock = block;
	}
	
	/**
	 * setKeyBinding
	 * 
	 * @param keyBinding
	 */
	@JRubyMethod(name = "key_binding=")
	public void setKeyBinding(String keyBinding)
	{
		this._keyBinding = keyBinding;
	}

	/**
	 * setOutput
	 * 
	 * @param output
	 */
	@JRubyMethod(name = "output=")
	public void setOutput(String output)
	{
		this._output = output;
	}
	
	/**
	 * toSource
	 */
	protected void toSource(SourcePrinter printer)
	{
		printer.printWithIndent("command \"").print(this._displayName).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$
		
		printer.printWithIndent("path: ").println(this._path); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this._scope); //$NON-NLS-1$
		if (this._invoke != null)
		{
			printer.printWithIndent("invoke: ").println(this._invoke); //$NON-NLS-1$
		}
		if (this._invokeBlock != null)
		{
			printer.printWithIndent("block: ").println(this._invokeBlock.to_s().asJavaString()); //$NON-NLS-1$
		}
		printer.printWithIndent("keys: ").println(this._keyBinding); //$NON-NLS-1$
		printer.printWithIndent("output: ").println(this._output); //$NON-NLS-1$
		printer.printWithIndent("trigger: ").println(this._trigger); //$NON-NLS-1$
		
		printer.decreaseIndent().printlnWithIndent("}");
	}
}
