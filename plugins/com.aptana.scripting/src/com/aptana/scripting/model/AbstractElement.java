package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.util.StringUtil;

public abstract class AbstractElement
{
	private static final Map<String, List<AbstractElement>> ELEMENTS_BY_PATH;
	private static final AbstractElement[] NO_ELEMENTS = new AbstractElement[0];
	
	private String _path;
	private String _displayName;
	private Map<String,Object> _customProperties;
	
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
	 * ModelBase
	 * 
	 * @param path
	 */
	public AbstractElement(String path)
	{
		this._path = path;
		
		registerElement(this);
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
	 * put
	 * 
	 * @param property
	 * @param value
	 */
	public synchronized void put(String property, Object value)
	{
		if (property != null && property.length() > 0)
		{
			if (this._customProperties == null)
			{
				this._customProperties = new HashMap<String, Object>();
			}
			
			this._customProperties.put(property, value);
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
	
	/**
	 * printBody
	 * 
	 * @param printer
	 */
	abstract protected void printBody(SourcePrinter printer);
}