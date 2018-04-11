/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.sax;

import java.lang.reflect.Method;

import org.xml.sax.Attributes;

/**
 * @author klindsey
 */
public class SchemaFreeformElement implements ISchemaElement
{
	private Schema _owningSchema;

	/**
	 * SchemaFreeformElement
	 * 
	 * @param owningSchema
	 */
	public SchemaFreeformElement(Schema owningSchema)
	{
		this._owningSchema = owningSchema;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#addAttribute(java.lang.String, java.lang.String)
	 */
	public void addAttribute(String name, String usage)
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#addTransition(com.aptana.sax.ISchemaElement)
	 */
	public void addTransition(ISchemaElement node)
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#allowFreeformMarkup()
	 */
	public boolean allowFreeformMarkup()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#getName()
	 */
	public String getName()
	{
		return "*"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#getOnEnterMethod()
	 */
	public Method getOnEnterMethod()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#getOnExitMethod()
	 */
	public Method getOnExitMethod()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#getOwningSchema()
	 */
	public Schema getOwningSchema()
	{
		return this._owningSchema;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#getTransitionElements()
	 */
	public ISchemaElement[] getTransitionElements()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#hasAttribute(java.lang.String)
	 */
	public boolean hasAttribute(String name)
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#hasOnEnterMethod()
	 */
	public boolean hasOnEnterMethod()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#hasOnExitMethod()
	 */
	public boolean hasOnExitMethod()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#hasText()
	 */
	public boolean hasText()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#hasTransitions()
	 */
	public boolean hasTransitions()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#isDeprecatedAttribute(java.lang.String)
	 */
	public boolean isDeprecatedAttribute(String name)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#isOptionalAttribute(java.lang.String)
	 */
	public boolean isOptionalAttribute(String name)
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#isRequiredAttribute(java.lang.String)
	 */
	public boolean isRequiredAttribute(String name)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#isValidAttribute(java.lang.String)
	 */
	public boolean isValidAttribute(String name)
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#isValidTransition(java.lang.String)
	 */
	public boolean isValidTransition(String name)
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#moveTo(java.lang.String)
	 */
	public ISchemaElement moveTo(String name)
	{
		return new SchemaFreeformElement(this.getOwningSchema());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#setAllowFreeformMarkup(boolean)
	 */
	public void setAllowFreeformMarkup(boolean value)
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#setHasText(boolean)
	 */
	public void setHasText(boolean value)
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#setOnEnter(java.lang.String)
	 */
	public void setOnEnter(String onEnterMethod) throws SecurityException
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#setOnExit(java.lang.String)
	 */
	public void setOnExit(String onExitMethod) throws SecurityException
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#validateAttributes(org.xml.sax.Attributes)
	 */
	public void validateAttributes(Attributes attributes)
	{
		// do nothing
	}
}
