package com.aptana.scripting.model;

public class CommandResult
{
	private String _resultText;
	
	/**
	 * CommandResult
	 * 
	 * @param resultText
	 */
	public CommandResult(String resultText)
	{
		this._resultText = resultText;
	}
	
	/**
	 * getResultText
	 * 
	 * @return
	 */
	public String getResultText()
	{
		return this._resultText;
	}
}
