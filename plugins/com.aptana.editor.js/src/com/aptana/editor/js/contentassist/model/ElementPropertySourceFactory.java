/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import com.aptana.js.core.model.ClassElement;
import com.aptana.js.core.model.ClassGroupElement;
import com.aptana.js.core.model.EventElement;
import com.aptana.js.core.model.EventPropertyElement;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.JSElement;
import com.aptana.js.core.model.PropertyElement;

public class ElementPropertySourceFactory implements IAdapterFactory
{

	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adapterType.equals(IPropertySource.class))
		{
			if (adaptableObject instanceof ClassElement)
			{
				return new ClassElementPropertySource((ClassElement) adaptableObject);
			}
			if (adaptableObject instanceof FunctionElement)
			{
				return new FunctionElementPropertySource((FunctionElement) adaptableObject);
			}
			if (adaptableObject instanceof PropertyElement)
			{
				return new PropertyElementPropertySource((PropertyElement) adaptableObject);
			}
			if (adaptableObject instanceof JSElement)
			{
				return new JSElementPropertySource((JSElement) adaptableObject);
			}
			if (adaptableObject instanceof EventPropertyElement)
			{
				return new EventPropertyElementPropertySource((EventPropertyElement) adaptableObject);
			}
			if (adaptableObject instanceof EventElement)
			{
				return new EventElementPropertySource((EventElement) adaptableObject);
			}
			if (adaptableObject instanceof ClassGroupElement)
			{
				return new ClassGroupElementPropertySource((ClassGroupElement) adaptableObject);
			}
		}
		return null;
	}

	public Class[] getAdapterList()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
