package com.aptana.editor.html.contentassist.index;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
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
			// specifications,
			StringUtil.join(CSSIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, element.getUserAgents())),
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
