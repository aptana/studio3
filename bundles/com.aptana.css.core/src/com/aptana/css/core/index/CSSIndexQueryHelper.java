/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.index;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.css.core.CSSCorePlugin;
import com.aptana.css.core.internal.index.CSSIndexReader;
import com.aptana.css.core.model.ElementElement;
import com.aptana.css.core.model.PropertyElement;
import com.aptana.css.core.model.PseudoClassElement;
import com.aptana.css.core.model.PseudoElementElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;

public class CSSIndexQueryHelper
{
	/**
	 * getIndex
	 * 
	 * @return
	 */
	public static Index getIndex()
	{
		return getIndexManager().getIndex(URI.create(ICSSIndexConstants.METADATA_INDEX_LOCATION));
	}

	protected static IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
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
		return this._reader.getValues(index, ICSSIndexConstants.CLASS);
	}

	/**
	 * getColors - Returns the unique set of colors used within the project.
	 * 
	 * @param index
	 * @return
	 */
	public Set<String> getColors(Index index)
	{
		if (index != null)
		{
			Map<String, String> colorMap = _reader.getValues(index, ICSSIndexConstants.COLOR);

			if (colorMap != null)
			{
				return colorMap.keySet();
			}
		}

		return Collections.emptySet();
	}

	/**
	 * getElement
	 * 
	 * @param name
	 * @return
	 */
	public ElementElement getElement(String name)
	{
		if (StringUtil.isEmpty(name))
		{
			return null;
		}

		try
		{
			List<ElementElement> elements = _reader.getElements(getIndex(), name);
			if (!CollectionsUtil.isEmpty(elements))
			{
				return elements.get(0);
			}
		}
		catch (IOException e)
		{
			IdeLog.logError(CSSCorePlugin.getDefault(), e);
		}

		return null;
	}

	/**
	 * getElements
	 * 
	 * @return
	 */
	public List<ElementElement> getElements()
	{
		try
		{
			return _reader.getElements(getIndex());
		}
		catch (IOException e)
		{
			IdeLog.logError(CSSCorePlugin.getDefault(), e);
		}

		return Collections.emptyList();
	}

	/**
	 * getIDs
	 * 
	 * @param index
	 * @return
	 */
	public Map<String, String> getIDs(Index index)
	{
		return this._reader.getValues(index, ICSSIndexConstants.IDENTIFIER);
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		try
		{
			return _reader.getProperties(getIndex());
		}
		catch (IOException e)
		{
			IdeLog.logError(CSSCorePlugin.getDefault(), e);
		}

		return Collections.emptyList();
	}

	/**
	 * getProperty
	 * 
	 * @return
	 */
	public PropertyElement getProperty(String name)
	{
		if (StringUtil.isEmpty(name))
		{
			return null;
		}

		try
		{
			List<PropertyElement> properties = _reader.getProperties(getIndex(), name);
			if (!CollectionsUtil.isEmpty(properties))
			{
				return properties.get(0);
			}
		}
		catch (IOException e)
		{
			IdeLog.logError(CSSCorePlugin.getDefault(), e);
		}

		return null;
	}

	/**
	 * getPseudoClasses
	 * 
	 * @return
	 */
	public List<PseudoClassElement> getPseudoClasses()
	{
		try
		{
			return _reader.getPseudoClasses(getIndex());
		}
		catch (IOException e)
		{
			IdeLog.logError(CSSCorePlugin.getDefault(), e);
		}

		return Collections.emptyList();
	}

	public PseudoClassElement getPseudoClass(String name)
	{
		for (PseudoClassElement pce : getPseudoClasses())
		{
			if (ObjectUtil.areEqual(name, pce.getName()))
			{
				return pce;
			}
		}
		return null;
	}

	/**
	 * getPseudoElements
	 * 
	 * @return
	 */
	public List<PseudoElementElement> getPseudoElements()
	{
		try
		{
			return _reader.getPseudoElements(getIndex());
		}
		catch (IOException e)
		{
			IdeLog.logError(CSSCorePlugin.getDefault(), e);
		}

		return Collections.emptyList();
	}

	public PseudoElementElement getPseudoElement(String name)
	{
		for (PseudoElementElement pee : getPseudoElements())
		{
			if (ObjectUtil.areEqual(name, pee.getName()))
			{
				return pee;
			}
		}
		return null;
	}
}
