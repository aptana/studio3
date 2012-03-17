/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.sax;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.aptana.core.util.SourcePrinter;

/**
 * @author Kevin Lindsey
 */
public class SchemaElement implements ISchemaElement
{
	private String _name;
	private Schema _owningSchema;
	private Map<String, ISchemaElement> _transitions;
	private Map<String, Integer> _attributes;
	private List<String> _requiredAttributes;
	private boolean _allowFreeformMarkup;

	private String _instanceAttributes;

	private boolean _hasText;

	/**
	 * Create a new instance of SchemaNode
	 * 
	 * @param owningSchema
	 *            The schema that owns this element
	 * @param name
	 *            The name of this node
	 */
	public SchemaElement(Schema owningSchema, String name)
	{
		// make sure we have a valid schema reference
		if (owningSchema == null)
		{
			throw new IllegalArgumentException(Messages.SchemaElement_Undefined_Owning_Schema);
		}

		// make sure we have a valid name
		if (name == null || name.length() == 0)
		{
			throw new IllegalArgumentException(Messages.SchemaElement_Undefined_Name);
		}

		this._owningSchema = owningSchema;
		this._name = name;
		this._transitions = new HashMap<String, ISchemaElement>();
		this._attributes = new HashMap<String, Integer>();
		this._requiredAttributes = new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#addAttribute(java.lang.String, java.lang.String)
	 */
	public void addAttribute(String name, String usage)
	{
		// make sure we have a valid name
		if (name == null || name.length() == 0)
		{
			throw new IllegalArgumentException(Messages.SchemaElement_Undefined_Name);
		}

		// make sure we haven't defined this attribute already
		if (this.hasAttribute(name))
		{
			String msg = MessageFormat.format(Messages.SchemaElement_Attribute_already_defined, name, this._name);
			throw new IllegalArgumentException(msg);
		}

		int usageValue;

		if (usage != null)
		{
			if (usage.equals("required")) //$NON-NLS-1$
			{
				usageValue = AttributeUsage.REQUIRED;
			}
			else if (usage.equals("optional")) //$NON-NLS-1$
			{
				usageValue = AttributeUsage.OPTIONAL;
			}
			else
			{
				String msg = MessageFormat.format(Messages.SchemaElement_Not_valid_usage_attribute, usage);
				throw new IllegalArgumentException(msg);
			}
		}
		else
		{
			usageValue = AttributeUsage.REQUIRED;
		}

		// store attribute and attribute usage
		this._attributes.put(name, Integer.valueOf(usageValue));

		// add required attributes to array list for easier testing
		if ((usageValue & AttributeUsage.USAGE_MASK) == AttributeUsage.REQUIRED)
		{
			this._requiredAttributes.add(name);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#addTransition(com.aptana.sax.SchemaElement)
	 */
	public void addTransition(ISchemaElement node)
	{
		// make sure we have a valid object
		if (node == null)
		{
			throw new IllegalArgumentException(Messages.SchemaElement_Undefined_Node);
		}

		// get the new node's name
		String nodeName = node.getName();

		// make sure we haven't added this name already
		if (this._transitions.containsKey(nodeName))
		{
			String msg = "A node name '" + nodeName + "' has already been added to " + this._name; //$NON-NLS-1$ //$NON-NLS-2$

			throw new IllegalArgumentException(msg);
		}

		// add a transition to the new node
		this._transitions.put(nodeName, node);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#allowFreeformMarkup()
	 */
	public boolean allowFreeformMarkup()
	{
		return this._allowFreeformMarkup;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#getName()
	 */
	public String getName()
	{
		return this._name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#getOwningSchema()
	 */
	public Schema getOwningSchema()
	{
		return this._owningSchema;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#getTransitionElements()
	 */
	public SchemaElement[] getTransitionElements()
	{
		Collection<ISchemaElement> values = this._transitions.values();

		return values.toArray(new SchemaElement[values.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#hasAttribute(java.lang.String)
	 */
	public boolean hasAttribute(String name)
	{
		return this._attributes.containsKey(name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#hasText()
	 */
	public boolean hasText()
	{
		return this._hasText;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#hasTransitions()
	 */
	public boolean hasTransitions()
	{
		return this._transitions.size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#isDeprecatedAttribute(java.lang.String)
	 */
	public boolean isDeprecatedAttribute(String name)
	{
		boolean result = false;

		if (this.isValidAttribute(name))
		{
			int flags = this._attributes.get(name).intValue();

			result = ((flags & AttributeUsage.DEPRECATED) == AttributeUsage.DEPRECATED);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#isOptionalAttribute(java.lang.String)
	 */
	public boolean isOptionalAttribute(String name)
	{
		boolean result = false;

		if (this.isValidAttribute(name))
		{
			int flags = this._attributes.get(name).intValue();

			result = ((flags & AttributeUsage.USAGE_MASK) == AttributeUsage.OPTIONAL);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#isRequiredAttribute(java.lang.String)
	 */
	public boolean isRequiredAttribute(String name)
	{
		boolean result = false;

		if (this.isValidAttribute(name))
		{
			int flags = this._attributes.get(name).intValue();

			result = ((flags & AttributeUsage.USAGE_MASK) == AttributeUsage.REQUIRED);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#isValidAttribute(java.lang.String)
	 */
	public boolean isValidAttribute(String name)
	{
		return this._attributes.containsKey(name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#isValidTransition(java.lang.String)
	 */
	public boolean isValidTransition(String name)
	{
		return this._transitions.containsKey(name) || this.allowFreeformMarkup()
				|| this.getOwningSchema().allowFreeformMarkup();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#moveTo(java.lang.String)
	 */
	public ISchemaElement moveTo(String name)
	{
		ISchemaElement result = this._transitions.get(name);

		if (result == null && (this.allowFreeformMarkup() || this.getOwningSchema().allowFreeformMarkup()))
		{
			result = new SchemaFreeformElement(this.getOwningSchema());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#setAllowFreeformMarkup(boolean)
	 */
	public void setAllowFreeformMarkup(boolean value)
	{
		this._allowFreeformMarkup = value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#setHasText(boolean)
	 */
	public void setHasText(boolean value)
	{
		this._hasText = value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#toString()
	 */
	public String toString()
	{
		String result = "<" + this._name; //$NON-NLS-1$

		if (this._instanceAttributes != null)
		{
			result += this._instanceAttributes;
		}

		if (this.hasTransitions())
		{
			result += ">"; //$NON-NLS-1$
		}
		else
		{
			result += "/>"; //$NON-NLS-1$
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.sax.ISchemaElement#validateAttributes(org.xml.sax.Attributes)
	 */
	public void validateAttributes(Attributes attributes) throws SAXException
	{
		// save attributes for possible error messaging
		if (attributes.getLength() > 0)
		{
			StringBuilder buffer = new StringBuilder();

			for (int i = 0; i < attributes.getLength(); i++)
			{
				String key = attributes.getLocalName(i);
				String value = attributes.getValue(i);

				buffer.append(' ').append(key).append("=\"").append(value).append('"'); //$NON-NLS-1$
			}

			this._instanceAttributes = buffer.toString();
		}

		// make sure all required attributes are in the list
		for (int i = 0; i < this._requiredAttributes.size(); i++)
		{
			String name = this._requiredAttributes.get(i);
			String value = attributes.getValue(name);

			if (value == null)
			{
				SourcePrinter writer = new SourcePrinter();

				writer.print('<').print(this._name).print("> requires a '").print(name).println("' attribute"); //$NON-NLS-1$ //$NON-NLS-2$
				this.getOwningSchema().buildErrorMessage(writer, this._name, attributes);

				throw new SAXException(writer.toString());
			}
		}

		// make sure all attributes are allowed on this element
		for (int i = 0; i < attributes.getLength(); i++)
		{
			String name = attributes.getLocalName(i);

			if (!this._attributes.containsKey(name))
			{
				String message = MessageFormat.format(Messages.SchemaElement_Invalid_attribute_on_tag, new Object[] {
						name, this._name });
				SourcePrinter writer = new SourcePrinter();

				writer.println(message);
				this.getOwningSchema().buildErrorMessage(writer, this._name, attributes);

				throw new SAXException(writer.toString());
			}
		}
	}
}
