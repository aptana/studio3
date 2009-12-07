package com.aptana.scripting.model;

import java.io.OutputStream;

public class CommandResult
{
	private String _outputString;
	private OutputStream _outputStream;

	/**
	 * CommandResult
	 */
	public CommandResult()
	{
	}
	
	/**
	 * CommandResult
	 * 
	 * @param output
	 */
	public CommandResult(String output)
	{
		this._outputString = output;
	}
	
	/**
	 * CommandResult
	 * 
	 * @param stream
	 */
	public CommandResult(OutputStream stream)
	{
		this._outputStream = stream;
	}

	/**
	 * getOutputStream
	 * 
	 * @return
	 */
	public OutputStream getOutputStream()
	{
		return this._outputStream;
	}

	/**
	 * getOutputString
	 * 
	 * @return
	 */
	public String getOutputString()
	{
		return this._outputString;
	}
}
