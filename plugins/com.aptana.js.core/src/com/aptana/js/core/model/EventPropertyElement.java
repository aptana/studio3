/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import java.util.List;
import java.util.Map;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexUtil;
import com.aptana.jetty.util.epl.ajax.JSON.Output;

/**
 * EventProperty
 */
public class EventPropertyElement extends BaseElement implements IHasPredefinedValues
{

	private static final String TYPE_PROPERTY = "type"; //$NON-NLS-1$

	private String _type;
	private List<String> _constants;

	/**
	 * EventProperty
	 */
	public EventPropertyElement()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		this.setType(StringUtil.getStringValue(object.get(TYPE_PROPERTY)));
		this._constants = IndexUtil.createList(object.get(CONSTANTS_PROPERTY));
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this._type;
	}

	/**
	 * getConstants
	 * 
	 * @return
	 */
	public List<String> getConstants()
	{
		return CollectionsUtil.getListValue(this._constants);
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this._type = type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(TYPE_PROPERTY, this.getType());
		out.add(CONSTANTS_PROPERTY, this.getConstants());
	}

	/**
	 * toSource
	 * 
	 * @param printer
	 */
	public void toSource(SourcePrinter printer)
	{
		printer.printIndent();
		printer.print(this.getName());
		printer.print(" : "); //$NON-NLS-1$
		printer.print(this.getType());
	}
}
