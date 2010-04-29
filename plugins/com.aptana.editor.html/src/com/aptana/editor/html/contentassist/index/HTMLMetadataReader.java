/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;

import com.aptana.editor.common.contentassist.MetadataReader;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.editor.html.contentassist.model.SpecificationElement;
import com.aptana.editor.html.contentassist.model.UserAgentElement;
import com.aptana.editor.html.contentassist.model.ValueElement;

/**
 * @author Kevin Lindsey
 */
public class HTMLMetadataReader extends MetadataReader
{
	private static final String METADATA_SCHEMA_XML = "/metadata/HTMLMetadataSchema.xml"; //$NON-NLS-1$

	private List<ElementElement> _elements = new LinkedList<ElementElement>();
	private ElementElement _currentElement;
	private List<AttributeElement> _attributes = new LinkedList<AttributeElement>();
	private AttributeElement _currentAttribute;
	private UserAgentElement _currentUserAgent;
	private ValueElement _currentValue;
	private List<EventElement> _events = new LinkedList<EventElement>();
	private EventElement _currentEvent;

	/**
	 * Create a new instance of CoreLoader
	 */
	public HTMLMetadataReader()
	{
	}

	/**
	 * start processing a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterAttribute(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		AttributeElement attribute = new AttributeElement();

		// grab and set property values
		attribute.setName(attributes.getValue("name"));
		attribute.setType(attributes.getValue("type"));

		// set current item
		this._currentAttribute = attribute;
	}

	/**
	 * start processing an attribute-reference element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterAttributeReference(String ns, String name, String qname, Attributes attributes)
	{
		this._currentElement.addAttribute(attributes.getValue("name"));
	}

	/**
	 * start processing a browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterBrowser(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		UserAgentElement userAgent = new UserAgentElement();

		userAgent.setPlatform(attributes.getValue("platform"));
		userAgent.setVersion(attributes.getValue("version"));

		this._currentUserAgent = userAgent;
	}

	/**
	 * start processing a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterElement(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		ElementElement element = new ElementElement();

		// grab and set property values
		element.setName(attributes.getValue("name"));
		element.setRelatedClass(attributes.getValue("related-class"));
		element.setDisplayName(attributes.getValue("display-name"));

		// set current item
		this._currentElement = element;
	}

	/**
	 * start processing a event
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterEvent(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		EventElement event = new EventElement();

		// grab and set property values
		event.setName(attributes.getValue("name"));
		event.setType(attributes.getValue("type"));

		// set current item
		this._currentEvent = event;
	}

	/**
	 * start processing an attribute-reference element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterEventReference(String ns, String name, String qname, Attributes attributes)
	{
		this._currentElement.addEvent(attributes.getValue("name"));
	}

	/**
	 * Enter a reference element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterReference(String ns, String name, String qname, Attributes attributes)
	{
		String reference = attributes.getValue("name");

		if (this._currentAttribute != null)
		{
			this._currentAttribute.addReference(reference);
		}
		else if (this._currentElement != null)
		{
			this._currentElement.addReference(reference);
		}
	}

	/**
	 * start processing a specification element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterSpecification(String ns, String name, String qname, Attributes attributes)
	{
		SpecificationElement specification = new SpecificationElement();

		specification.setName(attributes.getValue("name"));
		specification.setVersion(attributes.getValue("version"));

		if (this._currentAttribute != null)
		{
			this._currentAttribute.addSpecification(specification);
		}
		else if (this._currentElement != null)
		{
			this._currentElement.addSpecification(specification);
		}
		else if (this._currentEvent != null)
		{
			this._currentEvent.addSpecification(specification);
		}
	}

	/**
	 * start processing a value element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterValue(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		ValueElement value = new ValueElement();

		// grab and set property values
		value.setName(attributes.getValue("name"));
		value.setDescription(attributes.getValue("description"));

		this._currentValue = value;
	}

	/**
	 * Exit a field element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitAttribute(String ns, String name, String qname)
	{
		this._attributes.add(this._currentAttribute);
		this._currentAttribute = null;
	}

	/**
	 * Exit an availability element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitAvailability(String ns, String name, String qname)
	{
	}

	/**
	 * Exit a browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitBrowser(String ns, String name, String qname)
	{
		if (this._currentAttribute != null)
		{
			this._currentAttribute.addUserAgent(this._currentUserAgent);
		}
		else if (this._currentElement != null)
		{
			this._currentElement.addUserAgent(this._currentUserAgent);
		}
		else if (this._currentEvent != null)
		{
			this._currentEvent.addUserAgent(this._currentUserAgent);
		}

		// clear current class
		this._currentUserAgent = null;
	}

	/**
	 * Exit a deprecated element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitDeprecated(String ns, String name, String qname)
	{
		String text = this.getText();

		if (this._currentAttribute != null)
		{
			this._currentAttribute.setDeprecated(this.decodeHtml(text));
		}
		else if (this._currentElement != null)
		{
			this._currentElement.setDeprecated(this.decodeHtml(text));
		}
	}

	/**
	 * Exit a description element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitDescription(String ns, String name, String qname)
	{
		String text = this.getText();

		if (this._currentAttribute != null)
		{
			this._currentAttribute.setDescription(this.decodeHtml(text));
		}
		else if (this._currentElement != null)
		{
			this._currentElement.setDescription(this.decodeHtml(text));
		}
		else if (this._currentEvent != null)
		{
			this._currentEvent.setDescription(this.decodeHtml(text));
		}
	}

	/**
	 * Exit a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitElement(String ns, String name, String qname)
	{
		this._elements.add(this._currentElement);
		this._currentElement = null;
	}

	/**
	 * Exit a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitEvent(String ns, String name, String qname)
	{
		this._events.add(this._currentEvent);
		this._currentEvent = null;
	}

	/**
	 * exit an example element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitExample(String ns, String name, String qname)
	{
		String text = this.getText();

		if (this._currentElement != null)
		{
			this._currentElement.setExample(this.decodeHtml(text));
		}
	}

	/**
	 * Exit a hint element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitHint(String ns, String name, String qname)
	{
		String text = this.getText();

		this._currentAttribute.setHint(text);
	}

	/**
	 * exit a remarks element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitRemarks(String ns, String name, String qname)
	{
		String text = this.getText();

		if (this._currentAttribute != null)
		{
			this._currentAttribute.setRemark(text);
		}
		else if (this._currentElement != null)
		{
			this._currentElement.setRemark(text);
		}
		else if (this._currentEvent != null)
		{
			this._currentEvent.setRemark(text);
		}
	}

	/**
	 * Exit a field element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitValue(String ns, String name, String qname)
	{
		this._currentAttribute.addValue(this._currentValue);
		this._currentValue = null;
	}

	/**
	 * getAttributes
	 * 
	 * @return
	 */
	public List<AttributeElement> getAttributes()
	{
		return this._attributes;
	}

	/**
	 * getElements
	 * 
	 * @return
	 */
	public List<ElementElement> getElements()
	{
		return this._elements;
	}

	/**
	 * getEvents
	 * 
	 * @return
	 */
	public List<EventElement> getEvents()
	{
		return this._events;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataReader#getSchemaStream()
	 */
	@Override
	protected InputStream getSchemaStream()
	{
		return this.getClass().getResourceAsStream(METADATA_SCHEMA_XML);
	}
}