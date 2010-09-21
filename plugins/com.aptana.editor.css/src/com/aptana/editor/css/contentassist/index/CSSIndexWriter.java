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
package com.aptana.editor.css.contentassist.index;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.UserAgentElement;
import com.aptana.editor.css.contentassist.model.ValueElement;
import com.aptana.index.core.Index;

public class CSSIndexWriter
{
	private static Map<UserAgentElement,String> keysByUserAgent = new HashMap<UserAgentElement,String>();
	static Map<String,UserAgentElement> userAgentsByKey = new HashMap<String,UserAgentElement>();
	
	private CSSMetadataReader _reader;
	private int _valueCount;
	
	/**
	 * CSSIndexWriter
	 */
	public CSSIndexWriter()
	{
		this._reader = new CSSMetadataReader();
	}
	
	/**
	 * getDocumentPath
	 * 
	 * @return
	 */
	protected URI getDocumentPath()
	{
		return URI.create(CSSIndexConstants.METADATA);
	}
	
	/**
	 * loadXML
	 * 
	 * @param stream
	 * @throws Exception 
	 * @throws ScriptDocException
	 */
	public void loadXML(InputStream stream) throws Exception
	{
		this._reader.loadXML(stream);
	}

	/**
	 * writeElement
	 * 
	 * @param index
	 * @param element
	 */
	protected void writeElement(Index index, ElementElement element)
	{
		String[] columns = new String[] {
			element.getName(),
			element.getDisplayName(),
			StringUtil.join(CSSIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, element.getUserAgents())),
			element.getDescription(),
			element.getExample(),
			StringUtil.join(CSSIndexConstants.SUB_DELIMITER, element.getProperties()),
			element.getRemark()
		};
		String key = StringUtil.join(CSSIndexConstants.DELIMITER, columns);
		
		index.addEntry(CSSIndexConstants.ELEMENT, key, this.getDocumentPath());
	}
	
	/**
	 * writeProperty
	 * 
	 * @param index
	 * @param property
	 */
	protected void writeProperty(Index index, PropertyElement property)
	{
		String[] columns = new String[] {
			property.getName(),
			Boolean.toString(property.allowMultipleValues()),
			property.getType(),
			// specifications
			StringUtil.join(CSSIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, property.getUserAgents())),
			property.getDescription(),
			property.getExample(),
			property.getHint(),
			property.getRemark(),
			StringUtil.join(CSSIndexConstants.SUB_DELIMITER, this.writeValues(index, property.getValues()))
		};
		String key = StringUtil.join(CSSIndexConstants.DELIMITER, columns);
		
		index.addEntry(CSSIndexConstants.PROPERTY, key, this.getDocumentPath());
	}

	/**
	 * writeToIndex
	 * 
	 * @param index
	 */
	public void writeToIndex(Index index)
	{
		for (ElementElement element : this._reader.getElements())
		{
			this.writeElement(index, element);
		}
		
		for (PropertyElement property : this._reader.getProperties())
		{
			this.writeProperty(index, property);
		}
	}
	
	/**
	 * writeUserAgent
	 * 
	 * @param index
	 * @param userAgent
	 * @return
	 */
	protected String writeUserAgent(Index index, UserAgentElement userAgent)
	{
		String key = keysByUserAgent.get(userAgent);
		
		if (key == null)
		{
			key = Integer.toString(keysByUserAgent.size());
			
			String[] columns = new String[] {
				key,
				userAgent.getDescription(),
				userAgent.getOS(),
				userAgent.getPlatform(),
				userAgent.getVersion()
			};
			String value = StringUtil.join(CSSIndexConstants.DELIMITER, columns);
			
			index.addEntry(CSSIndexConstants.USER_AGENT, value, this.getDocumentPath());
			
			keysByUserAgent.put(userAgent, key);
			userAgentsByKey.put(key, userAgent);
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
		List<String> keys = new LinkedList<String>();
		
		for (UserAgentElement userAgent : userAgents)
		{
			keys.add(this.writeUserAgent(index, userAgent));
		}
		
		return keys;
	}
	
	/**
	 * writeValue
	 * 
	 * @param index
	 * @param value
	 * @return
	 */
	protected String writeValue(Index index, ValueElement value)
	{
		String key = Integer.toString(this._valueCount++);
		
		String[] columns = new String[] {
			key,
			value.getName(),
			value.getDescription(),
			StringUtil.join(CSSIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, value.getUserAgents()))
		};
		String valueString = StringUtil.join(CSSIndexConstants.DELIMITER, columns);
		
		index.addEntry(CSSIndexConstants.VALUE, valueString, this.getDocumentPath());
		
		return key;
	}
	
	/**
	 * writeValues
	 * 
	 * @param index
	 * @param values
	 * @return
	 */
	protected List<String> writeValues(Index index, List<ValueElement> values)
	{
		List<String> keys = new LinkedList<String>();
		
		for (ValueElement value : values)
		{
			keys.add(this.writeValue(index, value));
		}
		
		return keys;
	}
}
