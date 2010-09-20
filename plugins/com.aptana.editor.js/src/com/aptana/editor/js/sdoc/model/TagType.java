/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.sdoc.model;

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
	NAMESPACE("@namespace"), //$NON-NLS-1$
	OVERVIEW("@overview"), //$NON-NLS-1$
	PARAM("@param"), //$NON-NLS-1$
	PRIVATE("@private"), //$NON-NLS-1$
	PROPERTY("@property"), //$NON-NLS-1$
	RETURN("@return"), //$NON-NLS-1$
	SEE("@see"), //$NON-NLS-1$
	TYPE("@type"), //$NON-NLS-1$
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
