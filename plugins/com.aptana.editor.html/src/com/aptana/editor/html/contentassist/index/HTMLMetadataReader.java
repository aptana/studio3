/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;

import com.aptana.editor.common.contentassist.MetadataReader;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.editor.html.contentassist.model.SpecificationElement;
import com.aptana.editor.html.contentassist.model.UserAgentElement;
import com.aptana.editor.html.contentassist.model.ValueElement;

/**
 * @author Kevin Lindsey
 */
public class HTMLMetadataReader extends MetadataReader
{
	private static final String HTML_METADATA_SCHEMA = "/metadata/HTMLMetadataSchema.xml"; //$NON-NLS-1$

	private List<ElementElement> _elements = new LinkedList<ElementElement>();
	private ElementElement _currentElement;
	private List<AttributeElement> _attributes = new LinkedList<AttributeElement>();
	private AttributeElement _currentAttribute;
	private UserAgentElement _currentUserAgent;
	private ValueElement _currentValue;
	private List<EventElement> _events = new LinkedList<EventElement>();
	private EventElement _currentEvent;
	private List<EntityElement> _entities = new LinkedList<EntityElement>();
	private EntityElement _currentEntity;

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
		attribute.setName(attributes.getValue("name")); //$NON-NLS-1$
		attribute.setType(attributes.getValue("type")); //$NON-NLS-1$
		attribute.setElement(attributes.getValue("element")); //$NON-NLS-1$

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
		this._currentElement.addAttribute(attributes.getValue("name")); //$NON-NLS-1$
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

		userAgent.setPlatform(attributes.getValue("platform")); //$NON-NLS-1$
		userAgent.setVersion(attributes.getValue("version")); //$NON-NLS-1$

		this._currentUserAgent = userAgent;
	}

	/**
	 * start processing an element element
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
		element.setName(attributes.getValue("name")); //$NON-NLS-1$
		element.setRelatedClass(attributes.getValue("related-class")); //$NON-NLS-1$
		element.setDisplayName(attributes.getValue("display-name")); //$NON-NLS-1$

		// set current item
		this._currentElement = element;
	}

	/**
	 * start processing an entity element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterEntity(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		EntityElement entity = new EntityElement();

		// grab and set property values
		entity.setName(attributes.getValue("name")); //$NON-NLS-1$
		entity.setDecimalValue(attributes.getValue("decimal")); //$NON-NLS-1$
		entity.setHexValue(attributes.getValue("hex")); //$NON-NLS-1$

		// set current item
		this._currentEntity = entity;
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
		event.setName(attributes.getValue("name")); //$NON-NLS-1$
		event.setType(attributes.getValue("type")); //$NON-NLS-1$

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
		this._currentElement.addEvent(attributes.getValue("name")); //$NON-NLS-1$
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
		String reference = attributes.getValue("name"); //$NON-NLS-1$

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

		specification.setName(attributes.getValue("name")); //$NON-NLS-1$
		specification.setVersion(attributes.getValue("version")); //$NON-NLS-1$

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
		value.setName(attributes.getValue("name")); //$NON-NLS-1$
		value.setDescription(attributes.getValue("description")); //$NON-NLS-1$

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
			this._currentAttribute.setDeprecated(this.resolveEntities(text));
		}
		else if (this._currentElement != null)
		{
			this._currentElement.setDeprecated(this.resolveEntities(text));
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
			this._currentAttribute.setDescription(this.resolveEntities(text));
		}
		else if (this._currentElement != null)
		{
			this._currentElement.setDescription(this.resolveEntities(text));
		}
		else if (this._currentEvent != null)
		{
			this._currentEvent.setDescription(this.resolveEntities(text));
		}
		else if (this._currentEntity != null)
		{
			this._currentEntity.setDescription(this.resolveEntities(text));
		}
	}

	/**
	 * Exit an element element
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
	 * Exit an entity element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitEntity(String ns, String name, String qname)
	{
		this._entities.add(this._currentEntity);
		this._currentEntity = null;
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
			this._currentElement.setExample(this.resolveEntities(text));
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
	 * getEntities
	 * 
	 * @return
	 */
	public List<EntityElement> getEntities()
	{
		return this._entities;
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
		try
		{
			return FileLocator.openStream(HTMLPlugin.getDefault().getBundle(),
					Path.fromPortableString(HTML_METADATA_SCHEMA), false);
		}
		catch (IOException e)
		{
			return this.getClass().getResourceAsStream(HTML_METADATA_SCHEMA);
		}
	}
}