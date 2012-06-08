/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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

import com.aptana.editor.dtd.IDTDConstants;
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
	private static class NodeCollector extends DTDTreeWalker
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

			if (!this._elementMap.containsKey(elementName))
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
			result = ParserPoolFactory.parse(IDTDConstants.CONTENT_TYPE_DTD, source).getRootNode();
		}
		catch (Exception e)
		{
		}

		return result;
	}
}
