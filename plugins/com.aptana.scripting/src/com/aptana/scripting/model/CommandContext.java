package com.aptana.scripting.model;

import java.io.InputStream;

public class CommandContext
{
	private String _input;
	private InputStream _inputStream;

	/**
	 * CommandContext
	 */
	public CommandContext()
	{
	}

	/**
	 * CommandContext
	 * 
	 * @param input
	 */
	public CommandContext(String input)
	{
		this._input = input;
	}
	
	/**
	 * CommandContext
	 * 
	 * @param stream
	 */
	public CommandContext(InputStream stream)
	{
		this._inputStream = stream;
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
