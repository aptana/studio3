package com.aptana.scripting.model;

import java.io.InputStream;
import java.util.Map;

public class CommandContext
{
	private String _input;
	private InputStream _inputStream;
	private Map<String,String> _environment;

	/**
	 * CommandContext
	 * 
	 * @param input
	 */
	public CommandContext(String input)
	{
		this(input, null);
	}
	
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
	 */
	public CommandContext(InputStream stream)
	{
		this(stream, null);
	}
	
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
	public Map<String,String> getEnviroment()
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
}
