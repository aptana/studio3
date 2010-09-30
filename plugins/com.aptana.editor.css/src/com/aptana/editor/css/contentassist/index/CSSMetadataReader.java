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
package com.aptana.editor.css.contentassist.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;

import com.aptana.editor.common.contentassist.MetadataReader;
import com.aptana.editor.css.Activator;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.PseudoClassElement;
import com.aptana.editor.css.contentassist.model.PseudoElementElement;
import com.aptana.editor.css.contentassist.model.SpecificationElement;
import com.aptana.editor.css.contentassist.model.UserAgentElement;
import com.aptana.editor.css.contentassist.model.ValueElement;

/**
 * @author Kevin Lindsey
 */
public class CSSMetadataReader extends MetadataReader
{
	private static final String METADATA_SCHEMA_XML = "/metadata/CSSMetadataSchema.xml"; //$NON-NLS-1$

	private List<ElementElement> _elements = new LinkedList<ElementElement>();
	private ElementElement _currentElement;
	private PropertyElement _currentProperty;
	private PseudoClassElement _currentPseudoClass;
	private PseudoElementElement _currentPseudoElement;
	private ValueElement _currentValue;
	private UserAgentElement _currentUserAgent;
	private List<PropertyElement> _properties = new LinkedList<PropertyElement>();
	private List<PseudoClassElement> _pseudoClasses = new LinkedList<PseudoClassElement>();
	private List<PseudoElementElement> _pseudoElements = new LinkedList<PseudoElementElement>();

	/**
	 * CSSMetadataReader
	 */
	public CSSMetadataReader()
	{
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
		userAgent.setOS(attributes.getValue("os")); //$NON-NLS-1$

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
		element.setDisplayName(attributes.getValue("display-name")); //$NON-NLS-1$

		// set current item
		this._currentElement = element;
	}

	/**
	 * start processing a property element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterProperty(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		PropertyElement property = new PropertyElement();

		// grab and set property values
		property.setName(attributes.getValue("name")); //$NON-NLS-1$
		property.setType(attributes.getValue("type")); //$NON-NLS-1$
		property.setAllowMultipleValues("true".equals(attributes.getValue("allow-multipe-values"))); //$NON-NLS-1$ //$NON-NLS-2$

		// set current item
		this._currentProperty = property;
	}

	/**
	 * start processing a property reference element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterPropertyReference(String ns, String name, String qname, Attributes attributes)
	{
		this._currentElement.addProperty(attributes.getValue("name")); //$NON-NLS-1$
	}

	/**
	 * start processing a pseudo-class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterPseudoClass(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		PseudoClassElement pseudoClass = new PseudoClassElement();

		// grab and set property values
		pseudoClass.setName(attributes.getValue("name")); //$NON-NLS-1$

		// set current item
		this._currentPseudoClass = pseudoClass;
	}

	/**
	 * start processing a pseudo-element element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterPseudoElement(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		PseudoElementElement pseudoElement = new PseudoElementElement();

		// grab and set property values
		pseudoElement.setName(attributes.getValue("name")); //$NON-NLS-1$
		pseudoElement.setAllowPseudoClassSyntax(Boolean.valueOf(attributes.getValue("allow-pseudo-class-syntax"))); //$NON-NLS-1$

		// set current item
		this._currentPseudoElement = pseudoElement;
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

		if (this._currentProperty != null)
		{
			this._currentProperty.addSpecification(specification);
		}
		else if (this._currentPseudoClass != null)
		{
			this._currentPseudoClass.addSpecification(specification);
		}
		else if (this._currentPseudoElement != null)
		{
			this._currentPseudoElement.addSpecification(specification);
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
	 * start processing a browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitBrowser(String ns, String name, String qname)
	{
		if (this._currentValue != null)
		{
			// add description to the current value
			this._currentValue.addUserAgent(this._currentUserAgent);
		}
		else if (this._currentProperty != null)
		{
			// add description to the current item
			this._currentProperty.addUserAgent(this._currentUserAgent);
		}
		else if (this._currentElement != null)
		{
			// add example to the current element
			this._currentElement.addUserAgent(this._currentUserAgent);
		}
		else if (this._currentPseudoClass != null)
		{
			// add example to the current pseudo-class
			this._currentPseudoClass.addUserAgent(this._currentUserAgent);
		}
		else if (this._currentPseudoElement != null)
		{
			// add example to the current pseudo-element
			this._currentPseudoElement.addUserAgent(this._currentUserAgent);
		}

		// clear current class
		this._currentUserAgent = null;
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

		if (this._currentProperty != null)
		{
			// add example to the current parameter
			this._currentProperty.setDescription(this.decodeHtml(text));
		}
		else if (this._currentElement != null)
		{
			// add example to the current parameter
			this._currentElement.setDescription(this.decodeHtml(text));
		}
		else if (this._currentUserAgent != null)
		{
			// add example to the current parameter
			this._currentUserAgent.setDescription(this.decodeHtml(text));
		}
		else if (this._currentPseudoClass != null)
		{
			// add example to the current pseudo-class
			this._currentPseudoClass.setDescription(this.decodeHtml(text));
		}
		else if (this._currentPseudoElement != null)
		{
			// add example to the current pseudo-element
			this._currentPseudoElement.setDescription(this.decodeHtml(text));
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
	 * exit an example element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitExample(String ns, String name, String qname)
	{
		String text = this.getText();

		if (this._currentProperty != null)
		{
			this._currentProperty.setExample(this.decodeHtml(text));
		}
		else if (this._currentElement != null)
		{
			this._currentElement.setExample(this.decodeHtml(text));
		}
		else if (this._currentPseudoClass != null)
		{
			this._currentPseudoClass.setExample(this.decodeHtml(text));
		}
		else if (this._currentPseudoElement != null)
		{
			this._currentPseudoElement.setExample(this.decodeHtml(text));
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

		if (this._currentProperty != null)
		{
			// add hint to the current property
			this._currentProperty.setHint(this.decodeHtml(text));
		}
		else if (this._currentElement != null)
		{
			// add hint to the current element
			this._currentElement.setDescription(this.decodeHtml(text));
		}
	}

	/**
	 * Exit a property element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitProperty(String ns, String name, String qname)
	{
		this._properties.add(this._currentProperty);

		this._currentProperty = null;
	}

	/**
	 * Exit a pseudo-class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitPseudoClass(String ns, String name, String qname)
	{
		this._pseudoClasses.add(this._currentPseudoClass);

		this._currentPseudoClass = null;
	}

	/**
	 * Exit a pseudo-element element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitPseudoElement(String ns, String name, String qname)
	{
		this._pseudoElements.add(this._currentPseudoElement);

		this._currentPseudoElement = null;
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

		if (this._currentProperty != null)
		{
			this._currentProperty.setRemark(text);
		}
		else if (this._currentElement != null)
		{
			this._currentElement.setRemark(text);
		}
	}

	/**
	 * Exit a value element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitValue(String ns, String name, String qname)
	{
		if (this._currentProperty != null)
		{
			this._currentProperty.addValue(this._currentValue);
		}
		else if (this._currentPseudoClass != null)
		{
			this._currentPseudoClass.addValue(this._currentValue);
		}

		// clear current value
		this._currentValue = null;
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
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		return this._properties;
	}

	/**
	 * getPseudoClasses
	 * 
	 * @return
	 */
	public List<PseudoClassElement> getPseudoClasses()
	{
		return this._pseudoClasses;
	}

	/**
	 * getPseudoElements
	 * 
	 * @return
	 */
	public List<PseudoElementElement> getPseudoElements()
	{
		return this._pseudoElements;
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
			return FileLocator.openStream(Activator.getDefault().getBundle(),
					Path.fromPortableString(METADATA_SCHEMA_XML), false);
		}
		catch (IOException e)
		{
			return this.getClass().getResourceAsStream(METADATA_SCHEMA_XML);
		}
	}
}