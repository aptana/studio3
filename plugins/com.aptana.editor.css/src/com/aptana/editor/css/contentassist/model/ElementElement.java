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
package com.aptana.editor.css.contentassist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.StringUtil;

public class ElementElement extends AbstractCSSMetadataElement
{
	private static final String PROPERTIES_PROPERTY = "properties"; //$NON-NLS-1$
	private static final String REMARK_PROPERTY = "remark"; //$NON-NLS-1$
	private static final String DISPLAY_NAME_PROPERTY = "displayName"; //$NON-NLS-1$

	private String _displayName;
	private List<String> _properties = new ArrayList<String>();
	private String _remark;

	/**
	 * ElementElement
	 */
	public ElementElement()
	{
		super();
	}

	/**
	 * addProperty
	 * 
	 * @param name
	 */
	public void addProperty(String name)
	{
		this._properties.add(name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.contentassist.model.AbstractCSSMetadataElement#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		this.setDisplayName(object.get(DISPLAY_NAME_PROPERTY).toString());
		this.setRemark(object.get(REMARK_PROPERTY).toString());

		// properties
		Object properties = object.get(PROPERTIES_PROPERTY);
		
		if (properties != null && properties.getClass().isArray())
		{
			for (Object property : (Object[]) properties)
			{
				this.addProperty(property.toString());
			}
		}
	}

	/**
	 * getFullName
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		return StringUtil.getStringValue(this._displayName);
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<String> getProperties()
	{
		return this._properties;
	}

	/**
	 * getRemark
	 * 
	 * @return
	 */
	public String getRemark()
	{
		return StringUtil.getStringValue(this._remark);
	}

	/**
	 * setFullName
	 * 
	 * @param name
	 */
	public void setDisplayName(String name)
	{
		this._displayName = name;
	}

	/**
	 * setRemark
	 * 
	 * @param remark
	 */
	public void setRemark(String remark)
	{
		this._remark = remark;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.css.contentassist.model.AbstractCSSMetadataElement#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(DISPLAY_NAME_PROPERTY, this.getDisplayName());
		out.add(REMARK_PROPERTY, this.getRemark());
		out.add(PROPERTIES_PROPERTY, this.getProperties());
	}
}
