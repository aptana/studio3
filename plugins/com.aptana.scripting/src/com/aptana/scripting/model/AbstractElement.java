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
}