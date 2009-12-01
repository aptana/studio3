package com.aptana.scripting.model;

import java.io.PrintWriter;
import java.io.StringWriter;

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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		
		// open snippet
		writer.append("  command \"").append(this._displayName).println("\" {"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// show body
		writer.append("    path:  ").println(this._path); //$NON-NLS-1$
		writer.append("    scope: ").println(this._scope); //$NON-NLS-1$
		
		// close snippet
		writer.println("  }"); //$NON-NLS-1$
		
		return sw.toString();
	}
}
