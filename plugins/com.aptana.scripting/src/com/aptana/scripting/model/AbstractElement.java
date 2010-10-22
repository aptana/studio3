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
package com.aptana.scripting.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.StringUtil;
import com.aptana.parsing.io.SourcePrinter;

public abstract class AbstractElement implements Comparable<AbstractElement>
{
	private static final AbstractElement[] NO_ELEMENTS = new AbstractElement[0];
	private static final Map<String, List<AbstractElement>> ELEMENTS_BY_PATH;

	private String _path;
	private String _displayName;
	private Map<String, Object> _customProperties;

	private Object propertyLock = new Object();

	/**
	 * static constructor
	 */
	static
	{
		ELEMENTS_BY_PATH = new HashMap<String, List<AbstractElement>>();
	}
	
	/**
	 * getElementsByDirectory
	 * 
	 * @param path
	 * @return
	 */
	public static AbstractElement[] getElementsByDirectory(String path)
	{
		List<AbstractElement> result = new LinkedList<AbstractElement>();
		
		if (path.endsWith(File.separator) == false)
		{
			path += File.separator;
		}
		
		synchronized (ELEMENTS_BY_PATH)
		{
			 for (String key : ELEMENTS_BY_PATH.keySet())
			 {
				 if (key.startsWith(path))
				 {
					 result.addAll(ELEMENTS_BY_PATH.get(key));
				 }
			 }
		}
		
		return result.toArray(new AbstractElement[result.size()]);
	}

	/**
	 * getRegisteredElements
	 * 
	 * @param path
	 * @return
	 */
	public static AbstractElement[] getElementsByPath(String path)
	{
		AbstractElement[] result = NO_ELEMENTS;

		synchronized (ELEMENTS_BY_PATH)
		{
			List<AbstractElement> elements = ELEMENTS_BY_PATH.get(path);

			if (elements != null)
			{
				result = elements.toArray(new AbstractElement[elements.size()]);
			}
		}

		return result;
	}

	/**
	 * registerElement
	 * 
	 * @param element
	 */
	public static void registerElement(AbstractElement element)
	{
		if (element != null)
		{
			String path = element.getPath();

			if (path != null && path.length() > 0)
			{
				synchronized (ELEMENTS_BY_PATH)
				{
					List<AbstractElement> elements = ELEMENTS_BY_PATH.get(path);

					if (elements == null)
					{
						elements = new ArrayList<AbstractElement>();
						ELEMENTS_BY_PATH.put(path, elements);
					}

					elements.add(element);
				}
			}
		}
	}
	
	/**
	 * unregisterElement
	 * 
	 * @param element
	 */
	public static void unregisterElement(AbstractElement element)
	{
		if (element != null)
		{
			String path = element.getPath();

			if (path != null && path.length() > 0)
			{
				synchronized (ELEMENTS_BY_PATH)
				{
					List<AbstractElement> elements = ELEMENTS_BY_PATH.get(path);

					if (elements != null)
					{
						elements.remove(element);

						if (elements.size() == 0)
						{
							ELEMENTS_BY_PATH.remove(path);
						}
					}
				}
				
				LibraryCrossReference.getInstance().unregisterPath(path);
			}
		}
	}

	/**
	 * ModelBase
	 * 
	 * @param path
	 */
	public AbstractElement(String path)
	{
		this._path = path;

		registerElement(this);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AbstractElement o)
	{
		return this._displayName.compareToIgnoreCase(o._displayName);
	}

	/**
	 * get
	 * 
	 * @param property
	 * @return
	 */
	public synchronized Object get(String property)
	{
		Object result = null;

		if (this._customProperties != null)
		{
			result = this._customProperties.get(property);
		}

		return result;
	}

	/**
	 * getDisplayName
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		return this._displayName;
	}

	/**
	 * getElementName
	 * 
	 * @return
	 */
	protected abstract String getElementName();

	/**
	 * getPath
	 * 
	 * @return
	 */
	public String getPath()
	{
		return this._path;
	}

	/**
	 * printBody
	 * 
	 * @param printer
	 */
	abstract protected void printBody(SourcePrinter printer);

	/**
	 * put
	 * 
	 * @param property
	 * @param value
	 */
	public synchronized void put(String property, Object value)
	{
		if (property != null && property.length() > 0)
		{
			synchronized (propertyLock)
			{
				if (this._customProperties == null)
				{
					this._customProperties = new HashMap<String, Object>();
				}

				this._customProperties.put(property, value);
			}
		}
	}

	/**
	 * setDisplayName
	 * 
	 * @param displayName
	 */
	public void setDisplayName(String displayName)
	{
		this._displayName = displayName;
	}

	/**
	 * setPath
	 * 
	 * @param path
	 */
	void setPath(String path)
	{
		if (StringUtil.areNotEqual(this._path, path))
		{
			unregisterElement(this);

			this._path = path;

			registerElement(this);
		}
	}

	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourcePrinter printer = new SourcePrinter();

		this.toSource(printer);

		return printer.toString();
	}

	/**
	 * toSource
	 * 
	 * @param printer
	 */
	protected void toSource(SourcePrinter printer)
	{
		// open element
		printer.printWithIndent(this.getElementName());
		printer.print(" \"").print(this.getDisplayName()).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$

		// emit body
		this.printBody(printer);

		// emit custom properties
		if (this._customProperties != null)
		{
			for (Map.Entry<String, Object> entry : this._customProperties.entrySet())
			{
				printer.printWithIndent(entry.getKey()).print(": ").println(entry.getValue().toString()); //$NON-NLS-1$
			}
		}

		// close element
		printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
	}
	
	@Override
	public String toString()
	{
		return toSource();
	}
}