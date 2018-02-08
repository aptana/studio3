/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc.model;

public enum TagType
{
	ADVANCED("@advanced"), //$NON-NLS-1$
	ALIAS("@alias"), //$NON-NLS-1$
	AUTHOR("@author"), //$NON-NLS-1$
	CLASS_DESCRIPTION("@classDescription"), //$NON-NLS-1$
	CONSTRUCTOR("@constructor"), //$NON-NLS-1$
	EXAMPLE("@example"), //$NON-NLS-1$
	EXCEPTION("@exception"), //$NON-NLS-1$
	EXTENDS("@extends"), //$NON-NLS-1$
	INTERNAL("@internal"), //$NON-NLS-1$
	METHOD("@method"), //$NON-NLS-1$
	MODULE("@module"), //$NON-NLS-1$
	NAMESPACE("@namespace"), //$NON-NLS-1$
	OVERVIEW("@overview"), //$NON-NLS-1$
	PARAM("@param"), //$NON-NLS-1$
	PRIVATE("@private"), //$NON-NLS-1$
	PROPERTY("@property"), //$NON-NLS-1$
	RETURN("@return"), //$NON-NLS-1$
	SEE("@see"), //$NON-NLS-1$
	TYPE("@type"), //$NON-NLS-1$
	USER_AGENT("@userAgent"), //$NON-NLS-1$
	UNKNOWN("@<???>"); //$NON-NLS-1$

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
