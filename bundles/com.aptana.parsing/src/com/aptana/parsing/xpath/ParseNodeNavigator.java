/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.xpath;

import java.util.Iterator;

import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenConstants;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;

import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.TextNode;

/**
 * @author Kevin Lindsey
 */
public class ParseNodeNavigator extends DefaultNavigator
{
	private static final long serialVersionUID = 841503047993117262L;
	private static ParseNodeNavigator INSTANCE = new ParseNodeNavigator();

	/**
	 * Flag used to make element name matching case insensitive (by forcing element names to lowercase)
	 */
	private boolean caseInsensitive;

	public ParseNodeNavigator()
	{
		this(false);
	}

	public ParseNodeNavigator(boolean caseInsensitive)
	{
		this.caseInsensitive = caseInsensitive;
	}

	/**
	 * getInstance
	 * 
	 * @return ParseNodeNavigator
	 */
	public static ParseNodeNavigator getInstance()
	{
		return INSTANCE;
	}

	/**
	 * @see org.jaxen.DefaultNavigator#getAttributeAxisIterator(java.lang.Object)
	 */
	public Iterator<?> getAttributeAxisIterator(Object contextNode)
	{
		if (isElement(contextNode))
		{
			IParseNode element = (IParseNode) contextNode;

			return new ParseNodeAttributeIterator(element.getAttributes());
		}
		return JaxenConstants.EMPTY_ITERATOR;
	}

	/**
	 * @see org.jaxen.Navigator#getAttributeName(java.lang.Object)
	 */
	public String getAttributeName(Object attr)
	{
		if (isAttribute(attr))
		{
			return ((IParseNodeAttribute) attr).getName();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see org.jaxen.Navigator#getAttributeNamespaceUri(java.lang.Object)
	 */
	public String getAttributeNamespaceUri(Object attr)
	{
		return null;
	}

	/**
	 * @see org.jaxen.Navigator#getAttributeQName(java.lang.Object)
	 */
	public String getAttributeQName(Object attr)
	{
		return null;
	}

	/**
	 * @see org.jaxen.Navigator#getAttributeStringValue(java.lang.Object)
	 */
	public String getAttributeStringValue(Object attr)
	{
		if (isAttribute(attr))
		{
			return ((IParseNodeAttribute) attr).getValue();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see org.jaxen.DefaultNavigator#getChildAxisIterator(java.lang.Object)
	 */
	public Iterator<?> getChildAxisIterator(Object contextNode)
	{
		if (contextNode instanceof IParseNode)
		{
			return new ParseNodeIterator((IParseNode) contextNode)
			{
				protected IParseNode getFirstNode(IParseNode node)
				{
					return node.getFirstChild();
				}

				protected IParseNode getNextNode(IParseNode node)
				{
					int index = node.getIndex();
					IParseNode parent = node.getParent();
					final int childCount = parent.getChildCount();
					if (index >= childCount - 1) {
						return null;
					}

					return parent.getChild(index + 1);
				}
			};
		}
		else
		{
			return new Iterator<Object>()
			{
				public boolean hasNext()
				{
					return false;
				}

				public Object next()
				{
					return null;
				}

				public void remove()
				{
				}
			};
		}
	}

	/**
	 * @see org.jaxen.Navigator#getCommentStringValue(java.lang.Object)
	 */
	public String getCommentStringValue(Object comment)
	{
		return null;
	}

	/**
	 * @see org.jaxen.DefaultNavigator#getDocumentNode(java.lang.Object)
	 */
	public Object getDocumentNode(Object contextNode)
	{
		IParseNode result = null;

		if (contextNode instanceof IParseNode)
		{
			result = (IParseNode) contextNode;

			while (result.getParent() != null)
			{
				result = result.getParent();
			}
		}

		return result;
	}

	/**
	 * @see org.jaxen.Navigator#getElementName(java.lang.Object)
	 */
	public String getElementName(Object element)
	{
		String elementName = ((IParseNode) element).getElementName();
		return this.caseInsensitive ? elementName.toLowerCase() : elementName;
	}

	/**
	 * @see org.jaxen.Navigator#getElementNamespaceUri(java.lang.Object)
	 */
	public String getElementNamespaceUri(Object element)
	{
		return null;
	}

	/**
	 * @see org.jaxen.Navigator#getElementQName(java.lang.Object)
	 */
	public String getElementQName(Object element)
	{
		return null;
	}

	/**
	 * @see org.jaxen.Navigator#getElementStringValue(java.lang.Object)
	 */
	public String getElementStringValue(Object element)
	{
		String result = null;

		if (element instanceof IParseNode)
		{
			result = ((IParseNode) element).getText();
		}

		return result;
	}

	/**
	 * @see org.jaxen.Navigator#getNamespacePrefix(java.lang.Object)
	 */
	public String getNamespacePrefix(Object ns)
	{
		return null;
	}

	/**
	 * @see org.jaxen.Navigator#getNamespaceStringValue(java.lang.Object)
	 */
	public String getNamespaceStringValue(Object ns)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jaxen.DefaultNavigator#getParentAxisIterator(java.lang.Object)
	 */
	public Iterator<?> getParentAxisIterator(final Object contextNode) throws UnsupportedAxisException
	{
		if (isAttribute(contextNode))
		{
			throw new UnsupportedAxisException("Need to add an iterator that supports attributes"); //$NON-NLS-1$
		}
		else
		{
			return new Iterator<Object>()
			{
				IParseNode next;

				{
					if (contextNode != null)
					{
						if (isAttribute(contextNode))
						{
							next = ((IParseNodeAttribute) contextNode).getParent();
						}
						else if (isElement(contextNode))
						{
							next = ((IParseNode) contextNode).getParent();
						}
					}
				}

				public boolean hasNext()
				{
					return next != null;
				}

				public Object next()
				{
					Object result = next;

					next = next.getParent();

					return result;
				}

				public void remove()
				{
					// do nothing
				}
			};
		}
	}

	/**
	 * @see org.jaxen.DefaultNavigator#getParentNode(java.lang.Object)
	 */
	public Object getParentNode(Object contextNode)
	{
		if (isAttribute(contextNode))
		{
			return ((IParseNodeAttribute) contextNode).getParent();
		}
		else
		{
			return ((IParseNode) contextNode).getParent();
		}
	}

	/**
	 * @see org.jaxen.Navigator#getTextStringValue(java.lang.Object)
	 */
	public String getTextStringValue(Object text)
	{
		if (isText(text))
		{
			return text.toString();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see org.jaxen.Navigator#isAttribute(java.lang.Object)
	 */
	public boolean isAttribute(Object object)
	{
		return object instanceof IParseNodeAttribute;
	}

	/**
	 * @see org.jaxen.Navigator#isComment(java.lang.Object)
	 */
	public boolean isComment(Object object)
	{
		return false;
	}

	/**
	 * @see org.jaxen.Navigator#isDocument(java.lang.Object)
	 */
	public boolean isDocument(Object object)
	{
		return false;
	}

	/**
	 * @see org.jaxen.Navigator#isElement(java.lang.Object)
	 */
	public boolean isElement(Object object)
	{
		return object instanceof IParseNode;
	}

	/**
	 * @see org.jaxen.Navigator#isNamespace(java.lang.Object)
	 */
	public boolean isNamespace(Object object)
	{
		return false;
	}

	/**
	 * @see org.jaxen.Navigator#isProcessingInstruction(java.lang.Object)
	 */
	public boolean isProcessingInstruction(Object object)
	{
		return false;
	}

	/**
	 * @see org.jaxen.Navigator#isText(java.lang.Object)
	 */
	public boolean isText(Object object)
	{
		return object instanceof String || object instanceof TextNode;
	}

	/**
	 * @see org.jaxen.Navigator#parseXPath(java.lang.String)
	 */
	public XPath parseXPath(String xpath)
	{
		return null;
	}
}
