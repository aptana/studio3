/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.contentassist.index.CSSIndexReader;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.PseudoClassElement;
import com.aptana.editor.css.contentassist.model.PseudoElementElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class CSSIndexQueryHelper
{
	/**
	 * getIndex
	 * 
	 * @return
	 */
	public static Index getIndex()
	{
		return IndexManager.getInstance().getIndex(URI.create(CSSIndexConstants.METADATA_INDEX_LOCATION));
	}

	private CSSIndexReader _reader;

	/**
	 * CSSIndexQueryHelper
	 */
	public CSSIndexQueryHelper()
	{
		this._reader = new CSSIndexReader();
	}

	/**
	 * getClasses
	 * 
	 * @return
	 */
	public Map<String, String> getClasses(Index index)
	{
		return this._reader.getValues(index, CSSIndexConstants.CLASS);
	}

	/**
	 * getColors - Returns the unique set of colors used within the project.
	 * 
	 * @param index
	 * @return
	 */
	public Set<String> getColors(Index index)
	{
		Set<String> result = Collections.emptySet();

		if (index != null)
		{
			Map<String, String> colorMap = this._reader.getValues(index, CSSIndexConstants.COLOR);

			if (colorMap != null)
			{
				result = colorMap.keySet();
			}
		}

		return result;
	}

	/**
	 * getElement
	 * 
	 * @param name
	 * @return
	 */
	public ElementElement getElement(String name)
	{
		ElementElement result = null;

		if (name != null && name.length() > 0)
		{
			try
			{
				List<ElementElement> elements = this._reader.getElements(getIndex(), name);

				if (elements.isEmpty() == false)
				{
					result = elements.get(0);
				}
			}
			catch (IOException e)
			{
				CSSPlugin.logError(e.getMessage(), e);
			}
		}

		return result;
	}

	/**
	 * getElements
	 * 
	 * @return
	 */
	public List<ElementElement> getElements()
	{
		List<ElementElement> result = Collections.emptyList();

		try
		{
			result = this._reader.getElements(getIndex());
		}
		catch (IOException e)
		{
			CSSPlugin.logError(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * getIDs
	 * 
	 * @param index
	 * @return
	 */
	public Map<String, String> getIDs(Index index)
	{
		return this._reader.getValues(index, CSSIndexConstants.IDENTIFIER);
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		List<PropertyElement> result = Collections.emptyList();

		try
		{
			result = this._reader.getProperties(getIndex());
		}
		catch (IOException e)
		{
			CSSPlugin.logError(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * getProperty
	 * 
	 * @return
	 */
	public PropertyElement getProperty(String name)
	{
		PropertyElement result = null;

		if (name != null && name.length() > 0)
		{
			try
			{
				List<PropertyElement> properties = this._reader.getProperties(getIndex(), name);

				if (properties.isEmpty() == false)
				{
					result = properties.get(0);
				}
			}
			catch (IOException e)
			{
				CSSPlugin.logError(e.getMessage(), e);
			}
		}

		return result;
	}

	/**
	 * getPseudoClasses
	 * 
	 * @return
	 */
	public List<PseudoClassElement> getPseudoClasses()
	{
		List<PseudoClassElement> result = Collections.emptyList();

		try
		{
			result = this._reader.getPseudoClasses(getIndex());
		}
		catch (IOException e)
		{
			CSSPlugin.logError(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * getPseudoElements
	 * 
	 * @return
	 */
	public List<PseudoElementElement> getPseudoElements()
	{
		List<PseudoElementElement> result = Collections.emptyList();

		try
		{
			result = this._reader.getPseudoElements(getIndex());
		}
		catch (IOException e)
		{
			CSSPlugin.logError(e.getMessage(), e);
		}

		return result;
	}
}
