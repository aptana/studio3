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
