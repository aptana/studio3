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
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.editor.html.contentassist.model.SpecificationElement;
import com.aptana.editor.html.contentassist.model.UserAgentElement;
import com.aptana.editor.html.contentassist.model.ValueElement;
import com.aptana.index.core.MetadataReader;

/**
 * @author Kevin Lindsey
 */
public class HTMLMetadataReader extends MetadataReader
{
	private enum Element
	{
		AVAILABILITY("availability"), //$NON-NLS-1$
		HINT("hint"), //$NON-NLS-1$
		DEPRECATED("deprecated"), //$NON-NLS-1$
		DESCRIPTION("description"), //$NON-NLS-1$
		REMARKS("remarks"), //$NON-NLS-1$
		EXAMPLE("example"), //$NON-NLS-1$
		VALUE("value"), //$NON-NLS-1$
		SPECIFICATION("specification"), //$NON-NLS-1$
		REFERENCE("reference"), //$NON-NLS-1$
		EVENT_REF("event-ref"), //$NON-NLS-1$
		EVENT("event"), //$NON-NLS-1$
		ENTITY("entity"), //$NON-NLS-1$
		ELEMENT("element"), //$NON-NLS-1$
		BROWSER("browser"), //$NON-NLS-1$
		ATTRIBUTE_REF("attribute-ref"), //$NON-NLS-1$
		ATTRIBUTE("attribute"), //$NON-NLS-1$
		ATTRIBUTES("attributes"), //$NON-NLS-1$
		ATTRIBUTE_REFS("attribute-refs"), //$NON-NLS-1$
		BROWSERS("browsers"), //$NON-NLS-1$
		HTML("html"), //$NON-NLS-1$
		ELEMENTS("elements"), //$NON-NLS-1$
		ENTITIES("entities"), //$NON-NLS-1$
		ESCAPE_CODES("escape-codes"), //$NON-NLS-1$
		ESCAPE_CODE("escape-code"), //$NON-NLS-1$
		EVENTS("events"), //$NON-NLS-1$
		EVENT_REFS("event-refs"), //$NON-NLS-1$
		VALUES("values"), //$NON-NLS-1$
		REFERENCES("references"), //$NON-NLS-1$
		UNDEFINED(null);

		private String name;

		private Element(String name)
		{
			this.name = name;
		}

		private static Element fromString(String name)
		{
			if (name != null)
			{
				for (Element b : Element.values())
				{
					if (name.equals(b.name))
					{
						return b;
					}
				}
			}
			return UNDEFINED;
		}
	}

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

	@Override
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes)
			throws SAXException
	{
		super.startElement(namespaceURI, localName, qualifiedName, attributes);

		switch (Element.fromString(localName))
		{
			case ATTRIBUTE:
				enterAttribute(namespaceURI, localName, qualifiedName, attributes);
				break;

			case ATTRIBUTE_REF:
				enterAttributeReference(namespaceURI, localName, qualifiedName, attributes);
				break;

			case BROWSER:
				enterBrowser(namespaceURI, localName, qualifiedName, attributes);
				break;

			case ELEMENT:
				enterElement(namespaceURI, localName, qualifiedName, attributes);
				break;

			case ENTITY:
				enterEntity(namespaceURI, localName, qualifiedName, attributes);
				break;

			case EVENT:
				enterEvent(namespaceURI, localName, qualifiedName, attributes);
				break;

			case EVENT_REF:
				enterEventReference(namespaceURI, localName, qualifiedName, attributes);
				break;

			case REFERENCE:
				enterReference(namespaceURI, localName, qualifiedName, attributes);
				break;

			case SPECIFICATION:
				enterSpecification(namespaceURI, localName, qualifiedName, attributes);
				break;

			case VALUE:
				enterValue(namespaceURI, localName, qualifiedName, attributes);
				break;

			case EXAMPLE:
			case REMARKS:
			case DESCRIPTION:
			case DEPRECATED:
			case HINT:
				startTextBuffer(namespaceURI, localName, qualifiedName, attributes);
				break;

			case UNDEFINED:
				IdeLog.logWarning(HTMLPlugin.getDefault(),
						MessageFormat.format("Unable to convert element with name {0} to enum value", localName)); //$NON-NLS-1$
				break;

			default:
				// do nothing
				break;
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException
	{
		switch (Element.fromString(localName))
		{
			case ATTRIBUTE:
				exitAttribute(namespaceURI, localName, qualifiedName);
				break;

			case AVAILABILITY:
				exitAvailability(namespaceURI, localName, qualifiedName);
				break;

			case BROWSER:
				exitBrowser(namespaceURI, localName, qualifiedName);
				break;

			case DEPRECATED:
				exitDeprecated(namespaceURI, localName, qualifiedName);
				break;

			case DESCRIPTION:
				exitDescription(namespaceURI, localName, qualifiedName);
				break;

			case ELEMENT:
				exitElement(namespaceURI, localName, qualifiedName);
				break;

			case ENTITY:
				exitEntity(namespaceURI, localName, qualifiedName);
				break;

			case EVENT:
				exitEvent(namespaceURI, localName, qualifiedName);
				break;

			case EXAMPLE:
				exitExample(namespaceURI, localName, qualifiedName);
				break;

			case HINT:
				exitHint(namespaceURI, localName, qualifiedName);
				break;

			case REMARKS:
				exitRemarks(namespaceURI, localName, qualifiedName);
				break;

			case VALUE:
				exitValue(namespaceURI, localName, qualifiedName);
				break;

			case UNDEFINED:
				IdeLog.logWarning(HTMLPlugin.getDefault(),
						MessageFormat.format("Unable to convert element with name {0} to enum value", localName)); //$NON-NLS-1$
				break;

			default:
				// do nothing
				break;
		}
		super.endElement(namespaceURI, localName, qualifiedName);
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
			this._currentElement.setExample(text);
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