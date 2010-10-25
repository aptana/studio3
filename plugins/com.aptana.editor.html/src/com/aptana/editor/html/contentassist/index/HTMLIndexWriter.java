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
package com.aptana.editor.html.contentassist.index;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.editor.html.contentassist.model.UserAgentElement;
import com.aptana.index.core.Index;

public class HTMLIndexWriter
{
	private HTMLMetadataReader _reader;
	private Map<UserAgentElement,String> _userAgentKeyMap = new HashMap<UserAgentElement,String>();

	/**
	 * HTMLIndexWriter
	 */
	public HTMLIndexWriter()
	{
		this._reader = new HTMLMetadataReader();
	}

	/**
	 * getDocumentPath
	 * 
	 * @return
	 */
	protected URI getDocumentPath()
	{
		return URI.create(HTMLIndexConstants.METADATA);
	}

	/**
	 * loadXML
	 * 
	 * @param stream
	 * @throws Exception
	 */
	public void loadXML(InputStream stream) throws Exception
	{
		this._reader.loadXML(stream);
	}

	/**
	 * writeAttribute
	 * 
	 * @param index
	 * @param attribute
	 */
	protected void writeAttribute(Index index, AttributeElement attribute)
	{
		String[] columns = new String[] {
			attribute.getName(),
			attribute.getType(),
			attribute.getElement(),
			"", // specifications
			StringUtil.join(HTMLIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, attribute.getUserAgents())),
			attribute.getDeprecated(),
			attribute.getDescription(),
			attribute.getHint(),
			StringUtil.join(HTMLIndexConstants.SUB_DELIMITER, attribute.getReferences()),
			attribute.getRemark(),
			"" // values
		};
		String key = StringUtil.join(HTMLIndexConstants.DELIMITER, columns);

		index.addEntry(HTMLIndexConstants.ATTRIBUTE, key, this.getDocumentPath());
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
			element.getRelatedClass(),
			StringUtil.join(HTMLIndexConstants.SUB_DELIMITER, element.getAttributes()),
			"", // specifications,
			StringUtil.join(HTMLIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, element.getUserAgents())),
			element.getDeprecated(),
			element.getDescription(),
			StringUtil.join(HTMLIndexConstants.SUB_DELIMITER, element.getEvents()),
			element.getExample(),
			StringUtil.join(HTMLIndexConstants.SUB_DELIMITER, element.getReferences()),
			element.getRemark()
		};
		String key = StringUtil.join(HTMLIndexConstants.DELIMITER, columns);

		index.addEntry(HTMLIndexConstants.ELEMENT, key, this.getDocumentPath());
	}

	/**
	 * writeEvent
	 * 
	 * @param index
	 * @param event
	 */
	protected void writeEvent(Index index, EventElement event)
	{
		String[] columns = new String[] {
			event.getName(),
			event.getType(),
			"", // specifications
			StringUtil.join(HTMLIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, event.getUserAgents())),
			event.getDescription(),
			event.getRemark()
		};
		String key = StringUtil.join(HTMLIndexConstants.DELIMITER, columns);

		index.addEntry(HTMLIndexConstants.EVENT, key, this.getDocumentPath());
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

		for (AttributeElement attribute : this._reader.getAttributes())
		{
			this.writeAttribute(index, attribute);
		}

		for (EventElement event : this._reader.getEvents())
		{
			this.writeEvent(index, event);
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
		String key = this._userAgentKeyMap.get(userAgent);

		if (key == null)
		{
			key = Integer.toString(this._userAgentKeyMap.size());

			String[] columns = new String[] {
				key,
				userAgent.getPlatform(),
				userAgent.getVersion()
			};
			String value = StringUtil.join(HTMLIndexConstants.DELIMITER, columns);

			index.addEntry(HTMLIndexConstants.USER_AGENT, value, this.getDocumentPath());

			this._userAgentKeyMap.put(userAgent, key);
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
}
