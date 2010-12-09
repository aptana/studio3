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
package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Convertible;
import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.StringUtil;

public class ParameterElement implements Convertible
{
	private static final String TYPES_PROPERTY = "types"; //$NON-NLS-1$
	private static final String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
	private static final String USAGE_PROPERTY = "usage"; //$NON-NLS-1$
	private static final String NAME_PROPERTY = "name"; //$NON-NLS-1$

	private String _name;
	private List<String> _types;
	private String _usage;
	private String _description;

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
		if (type != null && type.length() > 0)
		{
			if (this._types == null)
			{
				this._types = new ArrayList<String>();
			}

			this._types.add(type);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.mortbay.util.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		this.setName(object.get(NAME_PROPERTY).toString());
		this.setUsage(object.get(USAGE_PROPERTY).toString());
		this.setDescription(object.get(DESCRIPTION_PROPERTY).toString());

		// types
		Object types = object.get(TYPES_PROPERTY);

		if (types != null && types.getClass().isArray())
		{
			for (Object type : (Object[]) types)
			{
				this.addType(type.toString());
			}
		}
	}

	/**
	 * getDescription
	 */
	public String getDescription()
	{
		return StringUtil.getStringValue(this._description);
	}

	/**
	 * getName
	 */
	public String getName()
	{
		return StringUtil.getStringValue(this._name);
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public List<String> getTypes()
	{
		List<String> result = this._types;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getUsage
	 * 
	 * @return
	 */
	public String getUsage()
	{
		return StringUtil.getStringValue(this._usage);
	}

	/**
	 * setDescription
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setName
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/**
	 * setUsage
	 * 
	 * @param value
	 */
	public void setUsage(String usage)
	{
		this._usage = usage;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mortbay.util.ajax.JSON.Convertible#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		out.add(NAME_PROPERTY, this.getName());
		out.add(USAGE_PROPERTY, this.getUsage());
		out.add(DESCRIPTION_PROPERTY, this.getDescription());
		out.add(TYPES_PROPERTY, this.getTypes());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		if ("optional".equals(this.getUsage())) //$NON-NLS-1$
		{
			return "[" + this.getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			return this.getName();
		}
	}
}
