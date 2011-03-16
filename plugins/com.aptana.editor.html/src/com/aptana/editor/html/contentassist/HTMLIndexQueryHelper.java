/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.editor.html.contentassist.index.HTMLIndexReader;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class HTMLIndexQueryHelper
{
	/**
	 * getIndex
	 * 
	 * @return
	 */
	public static Index getIndex()
	{
		return IndexManager.getInstance().getIndex(URI.create(HTMLIndexConstants.METADATA_INDEX_LOCATION));
	}

	private HTMLIndexReader _reader;

	/**
	 * HTMLContentAssistHelper
	 */
	public HTMLIndexQueryHelper()
	{
		this._reader = new HTMLIndexReader();
	}

	/**
	 * getAttribute
	 * 
	 * @param name
	 * @return
	 */
	private List<AttributeElement> getAttribute(String name)
	{
		List<AttributeElement> result = Collections.emptyList();

		if (name != null && name.length() > 0)
		{
			try
			{
				result = this._reader.getAttribute(getIndex(), name);
			}
			catch (IOException e)
			{
				HTMLPlugin.logError(e.getMessage(), e);
			}
		}

		return result;
	}

	/**
	 * getAttribute
	 * 
	 * @param attributeName
	 * @return
	 */
	public AttributeElement getAttribute(String elementName, String attributeName)
	{
		AttributeElement result = null;

		if (elementName != null && elementName.length() > 0)
		{
			AttributeElement defaultAttribute = null;
			AttributeElement candidateAttribute = null;

			// TODO: optimize with a name->attribute hash
			for (AttributeElement attribute : this.getAttribute(attributeName))
			{
				String elementRef = attribute.getElement();

				if (elementRef != null && elementRef.length() > 0)
				{
					if (elementName.equals(elementRef))
					{
						candidateAttribute = attribute;
					}
				}
				else
				{
					defaultAttribute = attribute;
				}
			}

			if (candidateAttribute != null)
			{
				result = candidateAttribute;
			}
			else if (defaultAttribute != null)
			{
				result = defaultAttribute;
			}
		}

		return result;
	}

	/**
	 * getAttributes
	 * 
	 * @param element
	 * @return
	 */
	public List<AttributeElement> getAttributes(ElementElement element)
	{
		List<AttributeElement> result = Collections.emptyList();

		if (element != null)
		{
			List<AttributeElement> attributes = null;

			try
			{
				attributes = this._reader.getAttributes(getIndex(), element.getAttributes());
			}
			catch (IOException e)
			{
				HTMLPlugin.logError(e.getMessage(), e);
			}

			if (attributes != null && attributes.isEmpty() == false)
			{
				String elementName = element.getName();
				Map<String, AttributeElement> attributeMap = new HashMap<String, AttributeElement>();

				// filter attribute list
				for (AttributeElement attribute : attributes)
				{
					// grab the current attribute's name and any element to which it is bound
					String attributeName = attribute.getName();
					String owningElement = attribute.getElement();

					// a null or empty owning element name means this attribute is good for any element, otherwise, we
					// only want this attribute if it is specifically for this element
					boolean validAttribute = (owningElement == null || owningElement.length() == 0 || owningElement.equals(elementName));

					if (validAttribute)
					{
						// see if we already have an attribute with this name
						AttributeElement previousAttribute = attributeMap.get(attributeName);

						if (previousAttribute == null)
						{
							// no other attribute with this name, so use this one
							attributeMap.put(attributeName, attribute);
						}
						else
						{
							boolean currentHasElement = StringUtil.isEmpty(owningElement) == false;
							boolean previousHasElement = StringUtil.isEmpty(previousAttribute.getName()) == false;

							// xnor element names
							if ((currentHasElement && previousHasElement) || (!currentHasElement && !previousHasElement))
							{
								// either duplicate entry for this element, or dupe for any element
								// last definition wins
								attributeMap.put(attributeName, attribute);
							}
							else if (currentHasElement)
							{
								// element-specific attribute wins over general case
								attributeMap.put(attributeName, attribute);
							}
							// else the one in the map is already more specific
						}
					}
				}

				result = new ArrayList<AttributeElement>(attributeMap.values());
			}
		}

		return result;
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
				HTMLPlugin.logError(e.getMessage(), e);
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
			HTMLPlugin.logError(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * getEntities
	 * 
	 * @return
	 */
	public List<EntityElement> getEntities()
	{
		List<EntityElement> result = Collections.emptyList();

		try
		{
			result = this._reader.getEntities(getIndex());
		}
		catch (IOException e)
		{
			HTMLPlugin.logError(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * getEvents
	 * 
	 * @param elementName
	 * @return
	 */
	public List<EventElement> getEvents(String elementName)
	{
		return this.getEvents(this.getElement(elementName));
	}

	/**
	 * getEvents
	 * 
	 * @param element
	 * @return
	 */
	public List<EventElement> getEvents(ElementElement element)
	{
		List<EventElement> result = Collections.emptyList();

		if (element != null)
		{
			List<String> names = element.getEvents();

			try
			{
				result = this._reader.getEvents(getIndex(), names);
			}
			catch (IOException e)
			{
				HTMLPlugin.logError(e.getMessage(), e);
			}
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
}
