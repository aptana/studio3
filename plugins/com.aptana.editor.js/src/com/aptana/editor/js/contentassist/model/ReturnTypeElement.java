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

import com.aptana.core.util.StringUtil;

public class ReturnTypeElement
{
	private String _description;
	private String _type;

	/**
	 * ReturnTypeElement
	 */
	public ReturnTypeElement()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean result = false;

		if (this == obj)
		{
			result = true;
		}
		else if (obj instanceof ReturnTypeElement)
		{
			ReturnTypeElement that = (ReturnTypeElement) obj;

			// NOTE: we only care if the types match, not the descriptions
			result = StringUtil.areEqual(this.getType(), that.getType());
		}

		return result;
	}

	/**
	 * getDescription
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public String getType()
	{
		return this._type;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int h = 0;

		if (this._type != null)
		{
			h = this._type.hashCode();
		}

		return h;
	}

	/**
	 * setDescription
	 * 
	 * @param description
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setType
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		this._type = type;
	}
}
