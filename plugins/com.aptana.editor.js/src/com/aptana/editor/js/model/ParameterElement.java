package com.aptana.editor.js.model;

import java.util.LinkedList;
import java.util.List;

public class ParameterElement extends BaseElement
{
	private List<String> _types = new LinkedList<String>();

	/**
	 * ParameterElement
	 */
	public ParameterElement()
	{
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(String type)
	{
		this._types.add(type);
	}
}
