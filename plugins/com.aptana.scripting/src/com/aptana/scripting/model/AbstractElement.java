package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractElement
{
	private static final Map<String, List<AbstractElement>> ELEMENTS_BY_PATH;
	private static final AbstractElement[] NO_ELEMENTS = new AbstractElement[0];
	
	protected String _path;
	protected String _displayName;
	
	/**
	 * static constructor
	 */
	static
	{
		ELEMENTS_BY_PATH = new HashMap<String, List<AbstractElement>>();
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
	}
	
	/**
	 * getRegisteredElements
	 * 
	 * @param path
	 * @return
	 */
	public static AbstractElement[] getRegisteredElements(String path)
	{
		AbstractElement[] result = NO_ELEMENTS;
		List<AbstractElement> elements = ELEMENTS_BY_PATH.get(path);
		
		if (elements != null)
		{
			result = elements.toArray(new AbstractElement[elements.size()]);
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
			List<AbstractElement> elements = ELEMENTS_BY_PATH.get(path);
			
			if (elements == null)
			{
				elements = new ArrayList<AbstractElement>();
				ELEMENTS_BY_PATH.put(path, elements);
			}
			
			elements.add(element);
		}
	}
	
	/**
	 * ModelBase
	 * 
	 * @param path
	 */
	public AbstractElement(String path)
	{
		if (path == null || path.length() == 0)
		{
			throw new IllegalArgumentException("path must be defined");
		}
		
		this._path = path;
		
		registerElement(this);
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
	 * getPath
	 * 
	 * @return
	 */
	public String getPath()
	{
		return this._path;
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
		this._path = path;
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
	abstract protected void toSource(SourcePrinter printer);
}