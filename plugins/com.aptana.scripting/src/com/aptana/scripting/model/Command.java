package com.aptana.scripting.model;

import org.jruby.anno.JRubyMethod;

public class Command extends AbstractModel
{
	private String _invoke;
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
		return new CommandResult();
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
		
		printer.decreaseIndent().printlnWithIndent("}");
	}
}
