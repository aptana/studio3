package com.aptana.scripting.model;

import java.io.InputStream;
import java.util.Map;

import org.jruby.Ruby;
import org.jruby.RubyIO;

public class CommandContext
{
	private String _input;
	private InputStream _inputStream;
	private Map<String,String> _environment;
	private Ruby runtime;
	
	/**
	 * CommandContext
	 * 
	 * @param input
	 * @param environment
	 */
	public CommandContext(String input, Map<String,String> environment)
	{
		this._input = input;
		this._environment = environment;
	}
	
	/**
	 * CommandContext
	 * 
	 * @param stream
	 * @param environment
	 */
	public CommandContext(InputStream stream, Map<String,String> environment)
	{
		this._inputStream = stream;
		this._environment = environment;
	}
	
	/**
	 * getEnvironment
	 * 
	 * @return
	 */
	public Map<String,String> getEnvironment()
	{
		return this._environment;
	}
	
	/**
	 * getInputString
	 * 
	 * @return
	 */
	public String getInputString()
	{
		return this._input;
	}

	/**
	 * getInputStream
	 * 
	 * @return
	 */
	public InputStream getInputStream()
	{
		return this._inputStream;
	}

	void setRuntime(Ruby runtime)
	{
		this.runtime = runtime;		
	}
	
	public RubyIO in()
	{
		return new RubyIO(runtime, getInputStream());
	}
}
