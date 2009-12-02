package com.aptana.scripting.model;

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
