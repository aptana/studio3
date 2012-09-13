/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Pattern;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSPlugin;
import com.aptana.jetty.util.epl.ajax.JSON.Convertible;
import com.aptana.jetty.util.epl.ajax.JSON.Output;

public class ReturnTypeElement implements Convertible
{
	/**
	 * try to do some validation on type name, because somehow we're getting very whacked-out type names in our index
	 * strings for some users. See https://jira.appcelerator.org/browse/APSTUD-7366
	 */
	private static final Pattern TYPE_NAME_PATTERN = Pattern.compile("[\\$a-zA-Z\\-_]+[\\.\\w\\$\\-/<>]*"); //$NON-NLS-1$

	private static final String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
	private static final String TYPE_PROPERTY = "type"; //$NON-NLS-1$

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
			result = ObjectUtil.areEqual(this.getType(), that.getType());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		this.setType(StringUtil.getStringValue(object.get(TYPE_PROPERTY)));
		this.setDescription(StringUtil.getStringValue(object.get(DESCRIPTION_PROPERTY)));
	}

	/**
	 * getDescription
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return StringUtil.getStringValue(this._description);
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public String getType()
	{
		return StringUtil.getStringValue(this._type);
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
		this._type = validateTypeName(type);
	}

	String validateTypeName(String type)
	{
		if (StringUtil.isEmpty(type))
		{
			IdeLog.logError(JSPlugin.getDefault(), new IllegalArgumentException(
					"Null or Empty type name attempting to be recorded for a return type.")); //$NON-NLS-1$
			return StringUtil.EMPTY;
		}

		if (!TYPE_NAME_PATTERN.matcher(type).matches())
		{
			IdeLog.logError(
					JSPlugin.getDefault(),
					new IllegalArgumentException(MessageFormat.format(
							"Bad type name being set, something is going haywire: ''{0}''", type))); //$NON-NLS-1$

			int index = type.indexOf(',');
			if (index != -1)
			{
				return type.substring(0, index);
			}
			return StringUtil.EMPTY;
		}
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		out.add(TYPE_PROPERTY, this.getType());
		// TODO To shrink string size, don't write out empty descriptions?
		out.add(DESCRIPTION_PROPERTY, this.getDescription());
	}

	public String toString()
	{
		return MessageFormat.format("Type: ''{0}'', description: ''{1}''", _type, _description); //$NON-NLS-1$
	}
}
