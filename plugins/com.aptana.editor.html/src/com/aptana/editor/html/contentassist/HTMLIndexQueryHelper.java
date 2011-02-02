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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.editor.html.contentassist.index.HTMLIndexReader;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
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
				result = this._reader.getAttributes(getIndex(), name);
			}
			catch (IOException e)
			{
				HTMLPlugin.logError(e.getMessage(), e);
			}
		}
		
		return result;
	}

	/**
	 * getAttributes
	 * 
	 * @return
	 */
	public List<AttributeElement> getAttributes()
	{
		List<AttributeElement> result = Collections.emptyList();

		try
		{
			result = this._reader.getAttributes(getIndex());
		}
		catch (IOException e)
		{
			HTMLPlugin.logError(e.getMessage(), e);
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
