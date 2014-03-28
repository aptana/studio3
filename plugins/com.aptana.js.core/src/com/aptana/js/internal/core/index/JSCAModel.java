/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.json.simple.JSONObject;

import com.aptana.core.IMap;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.model.AliasElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.model.UserAgentElement;

/**
 * @author cwilliams
 */
class JSCAModel implements IJSCAModel
{
	private static final Pattern DOT_PATTERN = Pattern.compile("\\."); //$NON-NLS-1$

	/**
	 * An intermediate map used to hold the types.
	 */
	private Map<String, TypeElement> typesByName;

	/**
	 * The JSON we've parsed. This is what we traverse to generate our model.
	 */
	private JSONObject root;

	/**
	 * Cache the list of aliases plucked from the JSON.
	 */
	private List<AliasElement> aliases;

	/**
	 * Cache the list of aliases plucked from the JSON.
	 */
	private List<TypeElement> types;

	public JSCAModel(JSONObject json)
	{
		this.root = json;
	}

	@SuppressWarnings("unchecked")
	public synchronized List<AliasElement> getAliases()
	{
		if (aliases == null)
		{
			List<Object> array = (List<Object>) root.get("aliases"); //$NON-NLS-1$
			aliases = Collections.unmodifiableList(CollectionsUtil.map(array, new IMap<Object, AliasElement>()
			{
				public AliasElement map(Object item)
				{
					JSONObject alias = (JSONObject) item;
					AliasElement ae = new AliasElement();
					ae.setDescription((String) alias.get("description")); //$NON-NLS-1$
					ae.setName((String) alias.get("name")); //$NON-NLS-1$
					ae.setType((String) alias.get("type")); //$NON-NLS-1$
					return ae;
				}
			}));
		}
		cleanup();
		return aliases;
	}

	/**
	 * Once we've successfully pulled the aliases and types out of the JSON, get rid of the JSON and intermediate
	 * fields.
	 */
	private void cleanup()
	{
		if (aliases != null && types != null)
		{
			typesByName = null;
			root = null;
		}
	}

	public synchronized List<TypeElement> getTypes()
	{
		if (types == null)
		{
			types = Collections.unmodifiableList(doGetTypes());
		}
		cleanup();
		return types;
	}

	@SuppressWarnings("unchecked")
	private List<TypeElement> doGetTypes()
	{
		List<Object> array = (List<Object>) root.get("types"); //$NON-NLS-1$
		if (CollectionsUtil.isEmpty(array))
		{
			return Collections.emptyList();
		}

		int length = array.size();
		typesByName = new HashMap<String, TypeElement>(length);
		for (int i = 0; i < length; i++)
		{
			TypeElement currentType = createType((JSONObject) array.get(i));

			// grab namespace
			String typeName = currentType.getName();
			String namespace = getNamespace(typeName);

			// hide property
			setIsInternal(typeName, currentType.isInternal());

			// potentially hide all segments up to this one
			hideNamespace(namespace);

			// transfer user agents
			TypeElement namespaceType = getType(namespace);

			if (namespaceType != null)
			{
				String propertyName = typeName.substring(namespace.length() + 1);
				PropertyElement property = namespaceType.getProperty(propertyName);

				if (property != null)
				{
					List<UserAgentElement> userAgents = currentType.getUserAgents();

					if (!CollectionsUtil.isEmpty(userAgents))
					{
						for (UserAgentElement userAgent : userAgents)
						{
							property.addUserAgent(userAgent);
						}
					}
				}
			}
		}
		return new ArrayList<TypeElement>(typesByName.values());
	}

	private TypeElement createType(JSONObject json)
	{
		String typeName = (String) json.get("name"); //$NON-NLS-1$
		// If type is a property explicitly hanging off Global, strip the name down
		String globalPrefix = JSTypeConstants.GLOBAL_TYPE + "."; //$NON-NLS-1$
		if (typeName.startsWith(globalPrefix))
		{
			typeName = typeName.substring(globalPrefix.length());
		}
		TypeElement currentType;
		if (typesByName.containsKey(typeName))
		{
			// Use existing type if we've already created one for the current name
			currentType = typesByName.get(typeName);
		}
		else
		{
			// Otherwise, use the current empty type, set its name, and store it in the type map
			currentType = new TypeElement();
			currentType.setName(typeName);
		}
		// let's merge in the data from JSON/JSCA into the type here
		currentType.fromJSON(json);
		typesByName.put(typeName, currentType);

		// Build up namespace (so if we define "a.b.c" only, this will add "a", "a.b", etc)
		String[] parts = DOT_PATTERN.split(typeName);
		if (parts.length > 1)
		{
			String accumulatedName = parts[0];
			// We create parent types on demand to hang the properties off of. If we encounter teh definition later
			// we'll use fromJSON to populate the info we get into this type.
			TypeElement type = getType(accumulatedName);

			for (int i = 1; i < parts.length; i++)
			{
				// grab name part
				String pName = parts[i];

				// update accumulated type name
				accumulatedName += "." + pName; //$NON-NLS-1$ // $codepro.audit.disable stringConcatenationInLoop

				// try to grab the property off of the current type
				PropertyElement property = type.getProperty(pName);

				// create property, if we didn't have one
				if (property == null)
				{
					property = new PropertyElement();

					property.setName(pName);
					property.setIsClassProperty(true);
					property.addType(accumulatedName);

					type.addProperty(property);
				}

				// make sure to save last visited type
				typesByName.put(type.getName(), type);

				type = getType(accumulatedName);
			}
		}
		return currentType;
	}

	/**
	 * getType
	 * 
	 * @param typeName
	 * @return
	 */
	private TypeElement getType(String typeName)
	{
		TypeElement result = typesByName.get(typeName);

		if (result == null)
		{
			result = new TypeElement();
			result.setName(typeName);
		}

		return result;
	}

	/**
	 * Set the isInternal flag for the specified type. If the type name includes a namespace, then property for that
	 * type on the namespace will have its flag set. Otherwise, the type itself will have its flag set
	 * 
	 * @param typeName
	 *            The name of the type to process
	 * @param isInternal
	 *            The value to use when setting the type's isInternal flag
	 */
	private void setIsInternal(String typeName, boolean isInternal)
	{
		String namespace = getNamespace(typeName);

		if (!StringUtil.isEmpty(namespace))
		{
			TypeElement namespaceType = typesByName.get(namespace);

			if (namespaceType != null)
			{
				String name = typeName.substring(namespace.length() + 1);

				// get property for type name
				PropertyElement property = namespaceType.getProperty(name);

				if (property != null)
				{
					// tag property as internal
					property.setIsInternal(isInternal);
				}
			}
		}
		else
		{
			TypeElement type = typesByName.get(typeName);

			type.setIsInternal(isInternal);
		}
	}

	/**
	 * getNamespace
	 * 
	 * @param typeName
	 * @return
	 */
	private String getNamespace(String typeName)
	{
		int index = typeName.lastIndexOf('.');
		return (index != -1) ? typeName.substring(0, index) : StringUtil.EMPTY;
	}

	/**
	 * Possibly hide/show the specified namespace by visiting its properties. If all properties are internal, then the
	 * namespace will become internal. Note that this method both hides and shows namespaces and all parent namespaces
	 * are visited as well
	 * 
	 * @param namespace
	 *            The namespace to process
	 */
	private void hideNamespace(String namespace)
	{
		while (!StringUtil.isEmpty(namespace))
		{
			TypeElement type = typesByName.get(namespace);

			if (type != null)
			{
				boolean isInternal = true;

				for (PropertyElement property : type.getProperties())
				{
					if (!property.isInternal())
					{
						isInternal = false;
						break;
					}
				}

				setIsInternal(namespace, isInternal);
			}
			else
			{
				log("Unrecognized namespace in JSCAModel#hideNamespace: " + namespace); //$NON-NLS-1$
			}

			// move back one more segment
			namespace = getNamespace(namespace);
		}
	}

	/**
	 * log
	 * 
	 * @param message
	 */
	private void log(String message)
	{
		if (Platform.inDevelopmentMode())
		{
			System.out.println(message); // $codepro.audit.disable debuggingCode
		}
		else
		{
			IdeLog.logError(JSCorePlugin.getDefault(), message);
		}
	}
}
