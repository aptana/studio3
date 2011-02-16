/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSPlugin;

/**
 * JSTypeMapper
 */
public class JSTypeMapper
{
	private static JSTypeMapper INSTANCE;
	private static final String TYPE_MAPS = "typeMaps";
	private static final String TAG_TYPE_MAP = "typeMap";
	private static final String ATTR_SRC_TYPE = "sourceType";
	private static final String ATTR_DST_TYPE = "destinationType";

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static synchronized JSTypeMapper getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new JSTypeMapper();
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
	protected void loadMappings()
	{
		// this.addTypeMapping("jQuery", "Function<jQuery>:jQuery");

		IExtensionRegistry registry = Platform.getExtensionRegistry();

		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(JSPlugin.PLUGIN_ID, TYPE_MAPS);

			if (extensionPoint != null)
			{
				for (IExtension extension : extensionPoint.getExtensions())
				{
					for (IConfigurationElement element : extension.getConfigurationElements())
					{
						if (element.getName().equals(TAG_TYPE_MAP))
						{
							String srcType = element.getAttribute(ATTR_SRC_TYPE);
							String dstType = element.getAttribute(ATTR_DST_TYPE);

							if (StringUtil.isEmpty(srcType) == false && StringUtil.isEmpty(dstType) == false)
							{
								this.addTypeMapping(srcType, dstType);
							}
						}
					}
				}
			}
		}
	}
}
