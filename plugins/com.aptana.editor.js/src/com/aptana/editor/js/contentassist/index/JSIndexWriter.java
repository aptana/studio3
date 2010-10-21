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
package com.aptana.editor.js.contentassist.index;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.SinceElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.contentassist.model.UserAgentElement;
import com.aptana.index.core.Index;

public class JSIndexWriter
{
	private static Map<UserAgentElement, String> keysByUserAgent = new HashMap<UserAgentElement, String>();

	/**
	 * cacheUserAgent
	 * 
	 * @param userAgent
	 * @param key
	 */
	private void cacheUserAgent(UserAgentElement userAgent)
	{
		String key = userAgent.getKey();

		keysByUserAgent.put(userAgent, key);
	}

	/**
	 * getDocumentPath
	 * 
	 * @return
	 */
	protected URI getDocumentPath()
	{
		return URI.create(JSIndexConstants.METADATA_FILE_LOCATION);
	}

	/**
	 * writeDescription
	 * 
	 * @param description
	 */
	protected String writeDescription(Index index, String description, URI location)
	{
		String indexString;

		if (description != null && description.length() > 0)
		{
			indexString = UUID.randomUUID().toString();

			String value = indexString + JSIndexConstants.DELIMITER + description;

			index.addEntry(JSIndexConstants.DESCRIPTION, value, location);
		}
		else
		{
			indexString = JSIndexConstants.NO_ENTRY;
		}

		return indexString;
	}

	/**
	 * writeExamples
	 * 
	 * @param index
	 * @param examples
	 * @param location
	 */
	protected String writeExamples(Index index, List<String> examples, URI location)
	{
		String indexString;

		if (examples != null && examples.isEmpty() == false)
		{
			indexString = UUID.randomUUID().toString();

			String value = indexString + JSIndexConstants.DELIMITER + StringUtil.join(JSIndexConstants.DELIMITER, examples);

			index.addEntry(JSIndexConstants.EXAMPLES, value, location);
		}
		else
		{
			indexString = JSIndexConstants.NO_ENTRY;
		}

		return indexString;
	}

	/**
	 * writeFunction
	 * 
	 * @param index
	 * @param function
	 * @param location
	 */
	protected void writeFunction(Index index, FunctionElement function, URI location)
	{
		String functionTypesKey = this.writeReturnTypes(index, function.getTypes(), location);
		String parametersKey = this.writeParameters(index, function.getParameters(), location);
		String returnTypesKey = this.writeReturnTypes(index, function.getReturnTypes(), location);
		String descriptionKey = this.writeDescription(index, function.getDescription(), location);
		String examplesKey = this.writeExamples(index, function.getExamples(), location);
		String sinceListKey = this.writeSinceList(index, function.getSinceList(), location);

		String value = StringUtil.join( //
			JSIndexConstants.DELIMITER, //
			function.getOwningType(), //
			function.getName(), //
			descriptionKey, //
			functionTypesKey, //
			parametersKey, //
			returnTypesKey, //
			examplesKey, //
			sinceListKey, //
			StringUtil.join(JSIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, function.getUserAgents())) //
		);

		index.addEntry(JSIndexConstants.FUNCTION, value, location);
	}

	/**
	 * writeParameters
	 * 
	 * @param index
	 * @param parameters
	 * @return
	 */
	protected String writeParameters(Index index, List<ParameterElement> parameters, URI location)
	{
		List<String> keyList = new ArrayList<String>();
		String indexString = UUID.randomUUID().toString();

		keyList.add(indexString);

		for (ParameterElement parameter : parameters)
		{
			String name = parameter.getName();
			String usage = parameter.getUsage();
			String types = StringUtil.join(",", parameter.getTypes()); //$NON-NLS-1$

			keyList.add(name + "," + usage + "," + types); //$NON-NLS-1$ //$NON-NLS-2$
		}

		String value = StringUtil.join(JSIndexConstants.DELIMITER, keyList);

		index.addEntry(JSIndexConstants.PARAMETERS, value, location);

		return indexString;
	}

	/**
	 * writeProperty
	 * 
	 * @param index
	 * @param property
	 * @param location
	 */
	protected void writeProperty(Index index, PropertyElement property, URI location)
	{
		String propertyTypesKey = this.writeReturnTypes(index, property.getTypes(), location);
		String descriptionKey = this.writeDescription(index, property.getDescription(), location);
		String examplesKey = this.writeExamples(index, property.getExamples(), location);
		String sinceListKey = this.writeSinceList(index, property.getSinceList(), location);

		String value = StringUtil.join( //
			JSIndexConstants.DELIMITER, //
			property.getOwningType(), //
			property.getName(), //
			descriptionKey, //
			propertyTypesKey, //
			examplesKey, //
			sinceListKey, //
			StringUtil.join(JSIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, property.getUserAgents())) //
		);

		index.addEntry(JSIndexConstants.PROPERTY, value, location);
	}

	/**
	 * writeReturnTypes
	 * 
	 * @param index
	 * @param returnTypes
	 * @return
	 */
	protected String writeReturnTypes(Index index, List<ReturnTypeElement> returnTypes, URI location)
	{
		List<String> keyList = new ArrayList<String>();
		String indexString = UUID.randomUUID().toString();

		keyList.add(indexString);

		for (ReturnTypeElement returnType : returnTypes)
		{
			String type = returnType.getType();
			String descriptionKey = this.writeDescription(index, returnType.getDescription(), location);

			keyList.add(type + "," + descriptionKey); //$NON-NLS-1$
		}

		String value = StringUtil.join(JSIndexConstants.DELIMITER, keyList);

		index.addEntry(JSIndexConstants.RETURN_TYPES, value, location);

		return indexString;
	}

	/**
	 * writeSinceList
	 * 
	 * @param index
	 * @param sinceList
	 * @param location
	 * @return
	 */
	protected String writeSinceList(Index index, List<SinceElement> sinceList, URI location)
	{
		String indexString;

		if (sinceList != null && sinceList.isEmpty() == false)
		{
			// generate new key
			indexString = UUID.randomUUID().toString();

			// create temporary list and add key
			List<String> keyList = new ArrayList<String>();

			keyList.add(indexString);

			// process the list
			for (SinceElement since : sinceList)
			{
				String version = since.getVersion();
				String value = (version != null && version.length() > 0) ? since.getName() + JSIndexConstants.SUB_DELIMITER + version : since.getName();

				keyList.add(value);
			}

			// generate the key
			String key = StringUtil.join(JSIndexConstants.DELIMITER, keyList);

			index.addEntry(JSIndexConstants.SINCE_LIST, key, location);
		}
		else
		{
			indexString = JSIndexConstants.NO_ENTRY;
		}

		return indexString;
	}

	/**
	 * writeType
	 * 
	 * @param index
	 * @param type
	 */
	public void writeType(Index index, TypeElement type)
	{
		this.writeType(index, type, this.getDocumentPath());
	}

	/**
	 * writeType
	 * 
	 * @param index
	 * @param type
	 * @param location
	 */
	public void writeType(Index index, TypeElement type, URI location)
	{
		if (index != null && type != null && location != null)
		{
			// write type entry
			List<String> parentTypes = type.getParentTypes();
			String descriptionKey = this.writeDescription(index, type.getDescription(), location);
			// SinceElement[] sinceList = type.getSinceList();
			// UserAgentElement[] userAgents = type.getUserAgents();

			// calculate key value and add to index
			String value = StringUtil.join(JSIndexConstants.DELIMITER, type.getName(), (parentTypes != null && parentTypes.isEmpty() == false) ? StringUtil
				.join(",", parentTypes) //$NON-NLS-1$
				: (type.equals(JSTypeConstants.OBJECT_TYPE) == false)
				? JSTypeConstants.OBJECT_TYPE
					: "", //$NON-NLS-1$
				descriptionKey);

			index.addEntry(JSIndexConstants.TYPE, value, location);

			// write properties
			for (PropertyElement property : type.getProperties())
			{
				if (property instanceof FunctionElement)
				{
					this.writeFunction(index, (FunctionElement) property, location);
				}
				else
				{
					this.writeProperty(index, property, location);
				}
			}
		}
	}

	/**
	 * writeUserAgent
	 * 
	 * @param userAgent
	 * @return
	 */
	public String writeUserAgent(UserAgentElement userAgent)
	{
		String key = keysByUserAgent.get(userAgent);

		if (key == null)
		{
			// get key
			key = userAgent.getKey();

			// see if it has been written already
			JSIndexReader reader = new JSIndexReader();

			UserAgentElement diskUserAgent = null;

			try
			{
				diskUserAgent = reader.getUserAgent(key);
			}
			catch (IOException e)
			{
				Activator.logError(e.getMessage(), e);
			}

			// write to index, if we didn't have it there already
			if (diskUserAgent == null)
			{
				Index index = JSIndexQueryHelper.getIndex();

				// store user agent in index so we can recover it during the next session
				String[] columns = new String[] { key, userAgent.getDescription(), userAgent.getOS(), userAgent.getPlatform(), userAgent.getVersion() };
				String value = StringUtil.join(JSIndexConstants.DELIMITER, columns);

				index.addEntry(JSIndexConstants.USER_AGENT, value, this.getDocumentPath());
			}

			// cache to prevent unnecessary reads and writes
			this.cacheUserAgent(userAgent);
		}

		return key;
	}

	/**
	 * writeUserAgents
	 * 
	 * @param userAgents
	 * @return
	 */
	protected List<String> writeUserAgents(Index index, List<UserAgentElement> userAgents)
	{
		List<String> keys = new ArrayList<String>();

		for (UserAgentElement userAgent : userAgents)
		{
			keys.add(this.writeUserAgent(userAgent));
		}

		return keys;
	}
}
