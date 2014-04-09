/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.JSCorePlugin;

/**
 * JSTypeMapper
 */
public class JSTypeMapper
{
	private static JSTypeMapper INSTANCE;
	private static final String TYPE_MAPS = "typeMaps"; //$NON-NLS-1$
	private static final String TAG_TYPE_MAP = "typeMap"; //$NON-NLS-1$
	private static final String ATTR_SRC_TYPE = "sourceType"; //$NON-NLS-1$
	private static final String ATTR_DST_TYPE = "destinationType"; //$NON-NLS-1$

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static JSTypeMapper getInstance()
	{
		if (INSTANCE == null)
		{
			synchronized (JSTypeMapper.class)
			{
				if (INSTANCE == null)
				{
					INSTANCE = new JSTypeMapper();
				}
			}
		}

		return INSTANCE;
	}

	private Map<String, String> _map;

	/**
	 * JSTypeMapper
	 */
	private JSTypeMapper()
	{
		this.loadMappings();
	}

	/**
	 * addTypeMapping
	 * 
	 * @param type
	 * @param mappedType
	 */
	public void addTypeMapping(String type, String mappedType)
	{
		if (this._map == null)
		{
			this._map = new HashMap<String, String>();
		}

		this._map.put(type, mappedType);
	}

	/**
	 * getMappedType
	 * 
	 * @param type
	 * @return
	 */
	public String getMappedType(String type)
	{
		String result = type;

		if (this._map != null && this._map.containsKey(type))
		{
			result = this._map.get(type);
		}

		return result;
	}

	/**
	 * loadMappings
	 */
	private void loadMappings()
	{
		// @formatter:off
		EclipseUtil.processConfigurationElements(
				JSCorePlugin.PLUGIN_ID,
			TYPE_MAPS,
			new IConfigurationElementProcessor()
			{
				public void processElement(IConfigurationElement element)
				{
					String srcType = element.getAttribute(ATTR_SRC_TYPE);
					String dstType = element.getAttribute(ATTR_DST_TYPE);

					if (!StringUtil.isEmpty(srcType) && !StringUtil.isEmpty(dstType))
					{
						addTypeMapping(srcType, dstType);
					}
				}

				public Set<String> getSupportElementNames()
				{
					return CollectionsUtil.newSet(TAG_TYPE_MAP);
				}
			}
		);
		// @formatter:on
	}
}
