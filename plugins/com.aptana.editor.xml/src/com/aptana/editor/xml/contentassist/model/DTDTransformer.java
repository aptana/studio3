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
package com.aptana.editor.xml.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.editor.dtd.parsing.DTDParserConstants;
import com.aptana.editor.dtd.parsing.ast.DTDAttListDeclNode;
import com.aptana.editor.dtd.parsing.ast.DTDAttributeNode;
import com.aptana.editor.dtd.parsing.ast.DTDElementDeclNode;
import com.aptana.editor.dtd.parsing.ast.DTDParseRootNode;
import com.aptana.editor.dtd.parsing.ast.DTDTreeWalker;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

public class DTDTransformer
{
	private class NodeCollector extends DTDTreeWalker
	{
		private Map<String, ElementElement> _elementMap = new HashMap<String, ElementElement>();
		private Set<AttributeElement> _attributes = new HashSet<AttributeElement>();

		public List<AttributeElement> getAttributes()
		{
			return new ArrayList<AttributeElement>(this._attributes);
		}

		public List<ElementElement> getElements()
		{
			return new ArrayList<ElementElement>(this._elementMap.values());
		}

		public void visit(DTDElementDeclNode node)
		{
			String elementName = node.getName();

			if (this._elementMap.containsKey(elementName) == false)
			{
				ElementElement element = new ElementElement();

				element.setName(elementName);

				this._elementMap.put(elementName, element);
			}
		}

		public void visit(DTDAttributeNode node)
		{
			IParseNode parent = node.getParent();

			if (parent instanceof DTDAttListDeclNode)
			{
				String elementName = ((DTDAttListDeclNode) parent).getName();
				ElementElement element = this._elementMap.get(elementName);

				if (element != null)
				{
					String attributeName = node.getName();
					AttributeElement attribute = new AttributeElement();

					attribute.setName(attributeName);
					attribute.setElement(elementName);

					// add name reference to element
					element.addAttribute(attributeName);

					// add attribute to main list of attributes
					this._attributes.add(attribute);
				}
			}
		}
	}

	private List<ElementElement> _elements;
	private List<AttributeElement> _attributes;

	/**
	 * DTDTransfomer
	 */
	public DTDTransformer()
	{
	}

	/**
	 * transform
	 */
	public void transform(String source)
	{
		IParseRootNode root = this.parse(source);

		if (root instanceof DTDParseRootNode)
		{
			NodeCollector collector = new NodeCollector();

			collector.visit((DTDParseRootNode) root);

			this._elements = collector.getElements();
			Collections.sort(this._elements, new Comparator<ElementElement>()
			{
				public int compare(ElementElement o1, ElementElement o2)
				{
					return o1.getName().compareTo(o2.getName());
				}
			});

			this._attributes = collector.getAttributes();
			Collections.sort(this._attributes, new Comparator<AttributeElement>()
			{
				public int compare(AttributeElement o1, AttributeElement o2)
				{
					return o1.getName().compareTo(o2.getName());
				}
			});
		}
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
	 * parser
	 * 
	 * @param source
	 * @return
	 */
	protected IParseRootNode parse(String source)
	{
		IParseRootNode result = null;

		try
		{
			result = ParserPoolFactory.parse(DTDParserConstants.LANGUAGE, source);
		}
		catch (Exception e)
		{
		}

		return result;
	}
}
