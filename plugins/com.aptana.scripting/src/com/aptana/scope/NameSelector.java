package com.aptana.scope;

public class NameSelector implements ISelectorNode
{
	private String _name;
	
	/**
	 * NameSelector
	 * 
	 * @param name
	 */
	public NameSelector(String name)
	{
		this._name = name;
	}
	
	/**
	 * matches
	 */
	public boolean matches(String scope)
	{
		boolean result = false;
		
		if (this._name != null)
		{
			if (scope.startsWith(this._name))
			{
				int nameLength = this._name.length();
				int scopeLength = scope.length();
				
				result = (scopeLength == nameLength || scope.charAt(nameLength) == '.');
			}
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this._name;
	}
}
