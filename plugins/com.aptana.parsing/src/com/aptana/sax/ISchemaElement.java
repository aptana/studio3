/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * ISchemaElement
 */
public interface ISchemaElement
{
	/**
	 * Add an attribute to this element
	 * 
	 * @param name
	 *            The name of the attribute
	 * @param usage
	 *            The usage requirements for the attribute
	 */
	void addAttribute(String name, String usage);

	/**
	 * Add a transition out of this node to another node
	 * 
	 * @param node
	 *            The node to which this node can transition
	 */
	void addTransition(ISchemaElement node);

	/**
	 * allowFreeformMarkup
	 * 
	 * @return
	 */
	boolean allowFreeformMarkup();

	/**
	 * Get the name associated with this Schema node
	 * 
	 * @return this node's name
	 */
	String getName();

	/**
	 * getOwningSchema
	 * 
	 * @return
	 */
	Schema getOwningSchema();

	/**
	 * getTransitionElements
	 * 
	 * @return Returns an array of schema elements to which this element can transition.
	 */
	ISchemaElement[] getTransitionElements();

	/**
	 * Determine if this element has a definition for the specified attribute name
	 * 
	 * @param name
	 *            The name of the attribute to test
	 * @return Returns true if this element has an entry for the specified attribute name
	 */
	boolean hasAttribute(String name);

	/**
	 * Determine if this element expects text as a child node or not
	 * 
	 * @return Returns true if this element expects to contain text
	 */
	boolean hasText();

	/**
	 * hasTransitions
	 * 
	 * @return Returns true if this element has transitions
	 */
	boolean hasTransitions();

	/**
	 * Determine if the specified attribute name is optional on this element
	 * 
	 * @param name
	 *            The name of the attribute to test
	 * @return Returns true if the specified attribute name does not have to exist on this element
	 */
	boolean isDeprecatedAttribute(String name);

	/**
	 * Determine if the specified attribute name is optional on this element
	 * 
	 * @param name
	 *            The name of the attribute to test
	 * @return Returns true if the specified attribute name does not have to exist on this element
	 */
	boolean isOptionalAttribute(String name);

	/**
	 * Determine if the specified attribute name is required on this element
	 * 
	 * @param name
	 *            The name of the attribute to test
	 * @return Returns true if the specified attribute name must exist on this element
	 */
	boolean isRequiredAttribute(String name);

	/**
	 * Determine if the specified attribute name is allowed on this element
	 * 
	 * @param name
	 *            The name of the attribute to test
	 * @return Returns true if the specified attribute name is allowed on this element
	 */
	boolean isValidAttribute(String name);

	/**
	 * Determine if this node can transition to another node using the given name
	 * 
	 * @param name
	 *            The name of the node to test as a possible transition target
	 * @return Returns true if this node can transition to the given node name
	 */
	boolean isValidTransition(String name);

	/**
	 * Get the named SchemaElement that transitions from this element
	 * 
	 * @param name
	 *            The name of the SchemaElement to transition to
	 * @return The new SchemaElement
	 */
	ISchemaElement moveTo(String name);

	/**
	 * setAllowFreeformMarkup
	 * 
	 * @param value
	 */
	void setAllowFreeformMarkup(boolean value);

	/**
	 * Set a flag indicating whether this element expects text as a child node
	 * 
	 * @param value
	 */
	void setHasText(boolean value);

	/**
	 * @see java.lang.Object#toString()
	 */
	String toString();

	/**
	 * Validate the list of attributes against this element's definition. Required attributes must exist and no
	 * attributes can be in the list that have not been defined for this element.
	 * 
	 * @param attributes
	 *            The list of attributes to test
	 * @throws SAXException
	 */
	void validateAttributes(Attributes attributes) throws SAXException;

}