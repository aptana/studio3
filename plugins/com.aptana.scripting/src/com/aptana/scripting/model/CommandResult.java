package com.aptana.scripting.model;

import java.io.OutputStream;

public class CommandResult
{
	private String _outputString;
	private OutputStream _outputStream;
	private String _errorString;
	private OutputStream _errorStream;
	
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
	 * @param output
	 * @param errors
	 */
	public CommandResult(String output, String errors)
	{
		this._outputString = output;
		this._errorString = errors;
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
	 * CommandResult
	 * 
	 * @param outputStream
	 * @param errorStream
	 */
	public CommandResult(OutputStream outputStream, OutputStream errorStream)
	{
		this._outputStream = outputStream;
		this._errorStream = errorStream;
	}

	/**
	 * getOutputStream
	 * 
	 * @return
	 */
	public OutputStream getErrorStream()
	{
		return this._errorStream;
	}
	
	/**
	 * getErrorString
	 * 
	 * @return
	 */
	public String getErrorString()
	{
		return this._errorString;
	}
	
	/**
	 * getReturnValue
	 * 
	 * @return
	 */
	public int getReturnValue()
	{
		return 0;
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
