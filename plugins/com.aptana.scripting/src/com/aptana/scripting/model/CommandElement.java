package com.aptana.scripting.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.ParseException;
import org.jruby.Ruby;
import org.jruby.RubyProc;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.Activator;
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptingEngine;

public class CommandElement extends AbstractBundleElement
{
	private static final String CONTEXT_CONTRIBUTOR_ID = "context_contributor";
	private static final String TAG_CONTRIBUTOR = "contributor";
	private static final String ATTR_CLASS = "class";
	
	private String[] _triggers;
	private String _invoke;
	private RubyProc _invokeBlock;
	private String _keyBinding;
	private InputType _inputType;
	private OutputType _outputType;
	private ContextContributor[] _contextContributors;
	
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
	 * createCommandMap
	 * 
	 * @return
	 */
	public Map<String,Object> createCommandMap()
	{
		// TESTING
		Map<String,Object> map = new HashMap<String,Object>();
		
		map.put("name", "testing");
		
		return map;
	}
	
	/**
	 * execute
	 * 
	 * @return
	 */
	public CommandResult execute()
	{
		return this.execute(this.createCommandMap());
	}
	
	/**
	 * execute
	 * 
	 * @param map
	 * @return
	 */
	public CommandResult execute(Map<String,Object> map)
	{
		String resultText = ""; //$NON-NLS-1$
		
		if (this.isExecutable())
		{
			if (this.isSnippet())
			{
				resultText = this._invoke;
			}
			else if (this.isShellCommand())
			{
				resultText = this.invokeStringCommand(map);
			}
			else if (this.isBlockCommand())
			{
				resultText = invokeBlockCommand(map);
			}
		}
		
		return new CommandResult(resultText);
	}

	/**
	 * getContextContributors
	 * 
	 * @return
	 */
	protected ContextContributor[] getContextContributors()
	{
		if (this._contextContributors == null)
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			List<ContextContributor> contributors = new ArrayList<ContextContributor>();

			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(Activator.PLUGIN_ID, CONTEXT_CONTRIBUTOR_ID);

				if (extensionPoint != null)
				{
					IExtension[] extensions = extensionPoint.getExtensions();

					for (IExtension extension : extensions)
					{
						IConfigurationElement[] elements = extension.getConfigurationElements();

						for (IConfigurationElement element : elements)
						{
							if (element.getName().equals(TAG_CONTRIBUTOR))
							{
								try
								{
									ContextContributor contributor = (ContextContributor) element.createExecutableExtension(ATTR_CLASS);
									
									contributors.add(contributor);
								}
								catch (CoreException e)
								{
									String message = MessageFormat.format(
										"Error creating context contributor: {0}",
										new Object[] { e.getMessage() }
									);
									
									Activator.logError(message, e);
								}
							}
						}
					}
				}
			}

			this._contextContributors = contributors.toArray(new ContextContributor[contributors.size()]);
		}
		
		return this._contextContributors;
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
	public String[] getTriggers()
	{
		return this._triggers;
	}
	
	/**
	 * invokeBlockCommand
	 * 
	 * @param resultText
	 * @return
	 */
	private String invokeBlockCommand(Map<String,Object> map)
	{
		Ruby runtime = ScriptingEngine.getInstance().getScriptingContainer().getRuntime();
		ThreadContext threadContext = runtime.getCurrentContext();
		String resultText = "";
		
		try
		{
			IRubyObject result = this._invokeBlock.call(
				threadContext,
				new IRubyObject[] { JavaEmbedUtils.javaToRuby(runtime, map) }
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
	private String invokeStringCommand(Map<String,Object> map)
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
	 * isSnippet
	 * 
	 * @return
	 */
	public boolean isSnippet()
	{
		return (this._inputType == InputType.NONE && this._outputType == OutputType.INSERT_AS_SNIPPET);
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
	 * setInputType
	 * 
	 * @param input
	 */
	public void setInputType(String input)
	{
		this._inputType = InputType.get(input);
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
	 * toSource
	 */
	protected void toSource(SourcePrinter printer)
	{
		// output command type
		if (this.isSnippet())
		{
			printer.printWithIndent("snippet \"").print(this.getDisplayName()).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			printer.printWithIndent("command \"").print(this.getDisplayName()).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
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
		printer.printWithIndent("input: ").println(this._inputType.getName()); //$NON-NLS-1$
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
					printer.print(", ");
				}
				
				printer.print(trigger);
				
				first = false;
			}
			
			printer.println();
		}
		
		// close the element
		printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
	}
}
