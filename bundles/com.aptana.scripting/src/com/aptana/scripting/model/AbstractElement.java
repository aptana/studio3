/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.utils.StringPool;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.scripting.IDebugScopes;
import com.aptana.scripting.ScriptingActivator;

public abstract class AbstractElement implements Comparable<AbstractElement>
{
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
	public static List<AbstractElement> getElementsByDirectory(String path)
	{
		List<AbstractElement> result = new ArrayList<AbstractElement>();

		// canonicalize path. This avoids false matches where one path segment name is a subset of another. For example,
		// /a/b is a subset of /a/bc/ and would be erroneously treated as a match without the trailing file separator
		if (path.endsWith(File.separator) == false)
		{
			path += File.separator;
		}

		// make a copy of the current paths we're tracking
		Set<String> keys;

		synchronized (ELEMENTS_BY_PATH)
		{
			keys = ELEMENTS_BY_PATH.keySet();
		}

		// add all elements that start with the specified path
		for (String key : keys)
		{
			if (key.startsWith(path))
			{
				List<AbstractElement> elements = ELEMENTS_BY_PATH.get(key);

				if (elements != null)
				{
					result.addAll(elements);
				}
			}
		}

		return result;
	}

	/**
	 * getRegisteredElements
	 * 
	 * @param path
	 * @return
	 */
	public static List<AbstractElement> getElementsByPath(String path)
	{
		List<AbstractElement> result;

		synchronized (ELEMENTS_BY_PATH)
		{
			List<AbstractElement> elements = ELEMENTS_BY_PATH.get(path);

			if (elements != null)
			{
				result = new ArrayList<AbstractElement>(elements);
			}
			else
			{
				result = Collections.emptyList();
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
					showRegistration("Register", element); //$NON-NLS-1$

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
					showRegistration("Unregister", element); //$NON-NLS-1$

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
	 * Show registration event
	 * 
	 * @param message
	 * @param element
	 */
	private static void showRegistration(String message, AbstractElement element)
	{
		if (IdeLog.isTraceEnabled(ScriptingActivator.getDefault(), IDebugScopes.SHOW_ELEMENT_REGISTRATION))
		{
			String name = element.getDisplayName();
			String path = element.getPath();
			String fullClassName = element.getClass().getName();
			String[] classParts = fullClassName.split("\\."); //$NON-NLS-1$
			String className = classParts[classParts.length - 1];

			IdeLog.logTrace(ScriptingActivator.getDefault(),
					MessageFormat.format("{0}: {1}, {2}, {3}", message, className, name, path), //$NON-NLS-1$
					IDebugScopes.SHOW_ELEMENT_REGISTRATION);
		}
	}

	/**
	 * ModelBase
	 * 
	 * @param path
	 *            The absolute path to the file defining this element
	 */
	public AbstractElement(String path)
	{
		this._path = BundleManager.getInstance().sharedString(path);

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
	public Object get(String property)
	{
		Object result = null;

		synchronized (propertyLock)
		{
			if (this._customProperties != null)
			{
				result = this._customProperties.get(property);
			}
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
	 * Returns the absolute path to the file defining this element
	 * 
	 * @return
	 */
	public String getPath()
	{
		return this._path;
	}

	/**
	 * hasProperty
	 * 
	 * @param property
	 * @return
	 */
	public boolean hasProperty(String property)
	{
		boolean result;

		synchronized (propertyLock)
		{
			result = this._customProperties != null && this._customProperties.containsKey(property);
		}

		return result;
	}

	/**
	 * Prints the body of the element, choosing to include or ignore "block" and "invoke" sub-elements
	 * 
	 * @param printer
	 */
	abstract protected void printBody(SourcePrinter printer, boolean includeBlocks);

	/**
	 * Sets the specified property to the specified value
	 * 
	 * @param property
	 * @param value
	 */
	public void put(String property, Object value)
	{
		if (!StringUtil.isEmpty(property))
		{
			// Pool common keys
			property = BundleManager.getInstance().sharedString(property);
			synchronized (propertyLock)
			{
				if (this._customProperties == null)
				{
					// The overwhelming case is that we just store triggers, so assume a size of 1
					this._customProperties = new HashMap<String, Object>(1);
				}

				this._customProperties.put(property, value);
			}
		}
	}

	/**
	 * Returns a map of all property/value pairs
	 * 
	 * @return
	 */
	public Map<String, Object> getCustomProperties()
	{
		if (this._customProperties == null)
		{
			return null;
		}
		return new HashMap<String, Object>(this._customProperties);
	}

	/**
	 * Sets a group of property/value pairs
	 * 
	 * @param props
	 */
	public void setCustomProperties(Map<String, Object> props)
	{
		synchronized (propertyLock)
		{
			this._customProperties = null;
			if (props != null)
			{
				// Manually copy over properties so we can pool common key strings
				this._customProperties = new HashMap<String, Object>(props.size());
				for (Map.Entry<String, Object> entry : props.entrySet())
				{
					String property = BundleManager.getInstance().sharedString(entry.getKey());
					this._customProperties.put(property, entry.getValue());
				}
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
	 * Sets the absolute path to the file defining this element. Not recommended to be modified manually.
	 * 
	 * @param path
	 */
	public void setPath(String path)
	{
		if (ObjectUtil.areNotEqual(this._path, path))
		{
			unregisterElement(this);

			this._path = BundleManager.getInstance().sharedString(path);

			registerElement(this);
		}
	}

	/**
	 * Prints the source of the element, including "block" and "invoke" sub-elements.
	 * 
	 * @return
	 */
	public String toSource()
	{
		return toSource(true);
	}

	/**
	 * Prints the source of the element, optionally including "block" and "invoke" sub-elements.
	 * 
	 * @return
	 */
	public String toSource(boolean includeBlocks)
	{
		SourcePrinter printer = new SourcePrinter();

		this.toSource(printer, includeBlocks);

		return printer.toString();
	}

	/**
	 * Prints the source of the element, optionally including "block" and "invoke" sub-elements.
	 * 
	 * @param printer
	 */
	protected void toSource(SourcePrinter printer, boolean includeBlocks)
	{
		// open element
		printer.printWithIndent(this.getElementName());
		printer.print(" \"").print(this.getDisplayName()).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$

		// emit body
		this.printBody(printer, includeBlocks);

		// emit custom properties
		if (this._customProperties != null)
		{
			for (Map.Entry<String, Object> entry : this._customProperties.entrySet())
			{
				Object value = entry.getValue();
				String valueAsString = value.toString();
				// If it's an array, turn into a list to make it prettier?
				if (value.getClass().isArray())
				{
					List<Object> list = Arrays.asList((Object[]) value);
					valueAsString = list.toString();
				}
				printer.printWithIndent(entry.getKey()).print(": ").println(valueAsString); //$NON-NLS-1$
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