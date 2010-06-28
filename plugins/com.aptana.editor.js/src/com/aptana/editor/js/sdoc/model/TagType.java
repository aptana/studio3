package com.aptana.editor.js.sdoc.model;

public enum TagType
{
	ADVANCED("@advanced"),
	ALIAS("@alias"),
	AUTHOR("@author"),
	CLASS_DESCRIPTION("@classDescription"),
	CONSTRUCTOR("@constructor"),
	EXAMPLE("@example"),
	EXCEPTION("@exception"),
	EXTENDS("@extends"),
	INTERNAL("@internal"),
	METHOD("@method"),
	NAMESPACE("@namespace"),
	OVERVIEW("@overview"),
	PARAM("@param"),
	PRIVATE("@private"),
	PROPERTY("@property"),
	RETURN("@return"),
	SEE("@see"),
	TYPE("@type"),
	UNKNOWN("@<???>");
	
	private String _name;
	
	/**
	 * TagType
	 * 
	 * @param name
	 */
	private TagType(String name)
	{
		this._name = name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString()
	{
		return this._name;
	}
}
