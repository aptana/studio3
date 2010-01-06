package com.aptana.scripting.model;

import java.io.OutputStream;

public class CommandResult
{
	private String _outputString;
	private OutputStream _outputStream;
	private String _errorString;
	private OutputStream _errorStream;
	private InputType _inputType;
	private int _returnValue;
	private boolean _executedSuccessfully;

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
	 * executedSuccessfully
	 * 
	 * @return
	 */
	public boolean executedSuccessfully()
	{
		return this._executedSuccessfully;
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
	 * getInputType
	 * 
	 * @return
	 */
	public InputType getInputType()
	{
		return this._inputType;
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

	/**
	 * getReturnValue
	 * 
	 * @return
	 */
	public int getReturnValue()
	{
		return this._returnValue;
	}

	/**
	 * setExecutedSuccessfully
	 * 
	 * @param value
	 */
	void setExecutedSuccessfully(boolean value)
	{
		this._executedSuccessfully = value;
	}

	/**
	 * setInputType
	 * 
	 * @param inputType
	 */
	void setInputType(InputType inputType)
	{
		this._inputType = inputType;
	}
	
	/**
	 * setReturnValue
	 * 
	 * @param value
	 */
	void setReturnValue(int value)
	{
		this._returnValue = value;
	}
}
