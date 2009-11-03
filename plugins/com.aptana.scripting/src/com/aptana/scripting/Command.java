package com.aptana.scripting;

public class Command
{
	private String _name;

	/**
	 * Snippet
	 * 
	 * @param name
	 */
	public Command(String name)
	{
		this._name = name;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}
}
