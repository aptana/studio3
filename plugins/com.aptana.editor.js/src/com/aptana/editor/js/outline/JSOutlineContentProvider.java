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
package com.aptana.editor.js.outline;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.js.outline.JSOutlineItem.Type;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class JSOutlineContentProvider extends CommonOutlineContentProvider
{
	private static final String CONTAINER_TYPE = "/"; //$NON-NLS-1$
	private static final String PROPERTY_TYPE = "."; //$NON-NLS-1$

	private Map<String, JSOutlineItem> fItemsByScope;
	private JSOutlineItem fLastAddedItem;

	private static final Set<String> CLASS_EXTENDERS;
	static
	{
		CLASS_EXTENDERS = new HashSet<String>();
		CLASS_EXTENDERS.add("dojo.lang.extend"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("Ext.extend"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("jQuery.extend"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("MochiKit.Base.update"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("Object.extend"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("qx.Class.define"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("qx.Interface.define"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("qx.Theme.define"); //$NON-NLS-1$
		CLASS_EXTENDERS.add("qx.Mixin.define"); //$NON-NLS-1$
	}

	public JSOutlineContentProvider()
	{
		fItemsByScope = new HashMap<String, JSOutlineItem>();
	}

	@Override
	public CommonOutlineItem getOutlineItem(IParseNode node)
	{
		if (node == null)
		{
			return null;
		}
		return new JSOutlineItem(node.getText(), getOutlineType(node), node, node);
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof AbstractThemeableEditor || parentElement instanceof ParseRootNode)
		{
			fItemsByScope.clear();
			fLastAddedItem = null;
			return super.getChildren(parentElement);
		}
		else if (parentElement instanceof JSOutlineItem)
		{
			JSOutlineItem item = (JSOutlineItem) parentElement;
			Object[] children = filter(item.getAllReferenceNodes());
			// the returned list potentially contains item itself; filters that out
			List<Object> list = new ArrayList<Object>();
			for (Object child : children)
			{
				if (child.equals(item))
				{
					continue;
				}
				list.add(child);
			}
			return list.toArray(new Object[list.size()]);
		}
		return super.getChildren(parentElement);
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof JSOutlineItem)
		{
			JSOutlineItem item = (JSOutlineItem) element;
			return item.getChildrenCount() > 0;
		}
		return super.hasChildren(element);
	}

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		Set<JSOutlineItem> elements = new TreeSet<JSOutlineItem>();
		if (nodes.length > 0 && nodes[0].getParent() instanceof ParseRootNode)
		{
			// treating the root node as type STATEMENTS and process its children directly
			processStatements(elements, nodes[0].getParent());
		}
		else
		{
			for (IParseNode node : nodes)
			{
				processNode(elements, node);
			}
		}
		return elements.toArray(new JSOutlineItem[elements.size()]);
	}

	private void addValue(Collection<JSOutlineItem> elements, Reference reference, IParseNode value)
	{
		addValue(elements, reference, value, null);
	}

	private void addValue(Collection<JSOutlineItem> elements, Reference reference, IParseNode value, IParseNode parent)
	{
		boolean processed = false;
		switch (value.getNodeType())
		{
			case JSNodeTypes.FUNCTION:
				processFunction(elements, value, reference);
				processed = true;
				break;
			case JSNodeTypes.INVOKE:
				IParseNode child = value.getChild(0);
				if (child.getNodeType() == JSNodeTypes.FUNCTION)
				{
					processFunction(elements, child, reference);
					processed = true;
				}
				else
				{
					value = child;
				}
				break;
		}

		if (!processed)
		{
			// keeps track of this item's scope so we can add virtual children later, if needed
			String path = reference.toString();
			JSOutlineItem item = fItemsByScope.get(path);
			if (item == null)
			{
				int count = 0;
				if (value.getNodeType() == JSNodeTypes.OBJECT_LITERAL)
				{
					count = value.getChildCount();
				}

				item = new JSOutlineItem(reference.getName(), getOutlineType(value), reference.getNameNode(), value,
						count);
				fItemsByScope.put(path, item);
			}
			elements.add(item);
			fLastAddedItem = item;
		}
	}

	private void addVirtualChild(Collection<JSOutlineItem> elements, Reference reference, IParseNode node,
			IParseNode target)
	{
		String key = reference.getScope();
		JSOutlineItem item = fItemsByScope.get(key);
		if (item == null)
		{
			// gets the outline node type
			Type type = (node.getNodeType() == JSNodeTypes.FUNCTION) ? Type.FUNCTION : Type.PROPERTY;
			// creates the outline item
			item = new JSOutlineItem(node.getText(), type, node, node);
			// caches associated by scope
			fItemsByScope.put(key, item);
		}
		elements.add(item);
		item.addVirtualChild(target);
		fLastAddedItem = item;
	}

	private void processNode(Collection<JSOutlineItem> elements, IParseNode node)
	{
		short type = node.getNodeType();
		switch (type)
		{
			case JSNodeTypes.ASSIGN:
				processAssignment(elements, node.getChild(0), node.getChild(1));
				break;
			case JSNodeTypes.FUNCTION:
				processFunction(elements, node, null);
				break;
			case JSNodeTypes.GROUP:
				if (node.getChildCount() > 0)
				{
					processNode(elements, node.getChild(0));
				}
				break;
			case JSNodeTypes.IDENTIFIER:
				processIdentifier(elements, node);
				break;
			case JSNodeTypes.INVOKE:
				processInvoke(elements, node);
				break;
			case JSNodeTypes.NAME_VALUE_PAIR:
				processNameValuePair(elements, node);
				break;
			case JSNodeTypes.OBJECT_LITERAL:
				int size = node.getChildCount();
				for (int i = 0; i < size; ++i)
				{
					processNode(elements, node.getChild(i));
				}
				break;
			case JSNodeTypes.RETURN:
				if (node.getChildCount() > 0)
				{
					IParseNode child = node.getChild(0);
					if (child.getNodeType() == JSNodeTypes.OBJECT_LITERAL)
					{
						size = child.getChildCount();
						for (int i = 0; i < size; ++i)
						{
							processNode(elements, child.getChild(i));
						}
					}
				}
				break;
			case JSNodeTypes.STATEMENTS:
				processStatements(elements, node);
				break;
			case JSNodeTypes.IF:
			case JSNodeTypes.TRY:
			case JSNodeTypes.CATCH:
				size = node.getChildCount();
				for (int i = 0; i < size; ++i)
				{
					processNode(elements, node.getChild(i));
				}
				break;
			case JSNodeTypes.THIS:
			case JSNodeTypes.VAR:
				processVar(elements, node);
				break;
		}
	}

	private IParseNode processAssignment(Collection<JSOutlineItem> elements, IParseNode lhs, IParseNode rhs)
	{
		short lhsType = lhs.getNodeType();
		short rhsType = rhs.getNodeType();
		if (rhsType == JSNodeTypes.ASSIGN)
		{
			// processes the right-hand assignment recursively
			rhs = processAssignment(elements, rhs.getChild(0), rhs.getChild(1));
			rhsType = rhs.getNodeType();
		}

		switch (lhsType)
		{
			case JSNodeTypes.STRING:
				if (rhsType == JSNodeTypes.FUNCTION || rhsType == JSNodeTypes.OBJECT_LITERAL)
				{
					String text = lhs.getText();
					Reference reference = new Reference(lhs.getParent(), lhs, text.substring(1, text.length() - 1),
							CONTAINER_TYPE);
					addValue(elements, reference, rhs);
				}
				break;
			case JSNodeTypes.IDENTIFIER:
				if (rhsType == JSNodeTypes.FUNCTION || rhsType == JSNodeTypes.OBJECT_LITERAL)
				{
					Reference reference = new Reference(lhs.getParent(), lhs, lhs.getText(), CONTAINER_TYPE);
					addValue(elements, reference, rhs);
				}
				else if (rhsType == JSNodeTypes.INVOKE && rhs.getChildCount() == 2)
				{
					IParseNode child = rhs.getChild(0);
					if (CLASS_EXTENDERS.contains(child.toString()))
					{
						processInvoke(elements, rhs);
						if (fLastAddedItem != null)
						{
							fLastAddedItem.setLabel(lhs.getText());
							fLastAddedItem.setRange(lhs.getNameNode().getNameRange());
						}
					}
					else
					{
						Reference reference = new Reference(lhs.getParent(), lhs, lhs.getText(), CONTAINER_TYPE);
						addValue(elements, reference, child);
					}
				}
				break;
			case JSNodeTypes.GET_PROPERTY:
				IParseNode target = null;
				// traverses down the left-side get-property nodes
				while (lhs.getNodeType() == JSNodeTypes.GET_PROPERTY)
				{
					target = lhs.getChild(1);
					lhs = lhs.getChild(0);
				}

				// only processes get-property expressions that begin with an identifier or 'this'
				if (lhs.getNodeType() == JSNodeTypes.IDENTIFIER || lhs.getNodeType() == JSNodeTypes.THIS)
				{
					String scopeString = Reference.createScopeString(lhs.getParent());
					Reference reference;
					if (fItemsByScope.containsKey(scopeString))
					{
						reference = new Reference(scopeString, target, target.getText(), CONTAINER_TYPE);
						addVirtualChild(elements, reference, lhs, target);
					}
					else
					{
						reference = new Reference(lhs, lhs, lhs.getText(), CONTAINER_TYPE);
						addValue(elements, reference, target);

						JSOutlineItem item = fItemsByScope.get(scopeString);
						item.addVirtualChild(target);
					}
				}
				break;
		}
		return rhs;
	}

	private void processFunction(Collection<JSOutlineItem> elements, IParseNode node, Reference reference)
	{
		IParseNode nameNode;
		String name;
		if (node.getNodeType() == JSNodeTypes.FUNCTION && node.getText().length() > 0)
		{
			nameNode = node.getChild(0);
			name = node.getText();
		}
		else
		{
			if (reference != null)
			{
				nameNode = reference.getNameNode();
				name = reference.getName();
			}
			else
			{
				nameNode = node;
				name = "<literal>"; //$NON-NLS-1$
			}
		}

		// keeps track of this item's scope so we can add virtual children later, if needed
		if (reference == null)
		{
			reference = new Reference(node, nameNode, name, CONTAINER_TYPE);
		}

		String fullpath = reference.toString();
		JSOutlineItem item = fItemsByScope.get(fullpath);
		if (item == null)
		{
			IParseNode parameters = node.getChild(1);
			IParseNode body = node.getChild(2);

			String pattern = "{0}({1})"; //$NON-NLS-1$
			String parmsString = parameters.toString();
			if (parmsString.startsWith("(")) //$NON-NLS-1$
				pattern = "{0}{1}"; //$NON-NLS-1$

			item = new JSOutlineItem(MessageFormat.format(pattern, name, parmsString), Type.FUNCTION, reference
					.getNameNode(), body, getChildrenCount(body));
			fItemsByScope.put(fullpath, item);
		}
		elements.add(item);
	}

	private void processIdentifier(Collection<JSOutlineItem> elements, IParseNode node)
	{
		IParseNode parent = node.getParent();
		if (parent.getChildCount() > 1)
		{
			IParseNode rhs = parent.getChild(1);
			if (rhs == node)
			{
				IParseNode grandparent = parent.getParent();
				if (grandparent != null && grandparent.getChildCount() > 1)
				{
					IParseNode target = grandparent.getChild(1);
					Reference reference;
					switch (grandparent.getNodeType())
					{
						case JSNodeTypes.ARGUMENTS:
							// supports dojo.lang.extend, MochiKit.Base.update, and Object.extend
							target = grandparent.getChild(grandparent.getChildCount() - 1);
							reference = new Reference(parent, rhs, rhs.getText(), PROPERTY_TYPE);
							String parentFullPath = reference.toString();

							// processes all key/value pairs
							IParseNode keyValuePair;
							int size = target.getChildCount();
							for (int i = 0; i < size; ++i)
							{
								keyValuePair = target.getChild(i);
								IParseNode key = keyValuePair.getChild(0);
								String keyString = key.toString();
								Reference keyValueReference = new Reference(parentFullPath, key, keyString,
										PROPERTY_TYPE);
								addVirtualChild(elements, keyValueReference, node, keyValuePair);
							}
							break;
						case JSNodeTypes.ASSIGN:
							reference = new Reference(parent, rhs, rhs.getText(), PROPERTY_TYPE);
							while (target.getNodeType() == JSNodeTypes.ASSIGN)
							{
								// finds the right-most element
								target = target.getChild(1);
							}
							addValue(elements, reference, target);
							break;
						case JSNodeTypes.GET_PROPERTY:
							IParseNode property = grandparent.getChild(1);
							reference = new Reference(grandparent, property, property.getText(), PROPERTY_TYPE);
							addVirtualChild(elements, reference, node, target);
							break;
					}
				}
			}
		}
	}

	private void processInvoke(Collection<JSOutlineItem> elements, IParseNode node)
	{
		IParseNode lhs = node.getChild(0);
		String source = lhs.toString();
		if (CLASS_EXTENDERS.contains(source))
		{
			IParseNode args = node.getChild(1);
			if (args.getNodeType() == JSNodeTypes.ARGUMENTS)
			{
				if (args.getChildCount() == 2)
				{
					IParseNode arg1 = args.getChild(0);
					IParseNode arg2 = args.getChild(1);
					if (arg2.getNodeType() == JSNodeTypes.OBJECT_LITERAL)
					{
						switch (arg1.getNodeType())
						{
							case JSNodeTypes.STRING:
							case JSNodeTypes.IDENTIFIER:
							case JSNodeTypes.GET_PROPERTY:
								processAssignment(elements, arg1, arg2);
								break;
						}
					}
				}
				else if (args.getChildCount() == 3)
				{
					// EXT case
					IParseNode arg1 = args.getChild(0);
					IParseNode arg3 = args.getChild(2);
					if (arg3.getNodeType() == JSNodeTypes.OBJECT_LITERAL)
					{
						switch (arg1.getNodeType())
						{
							case JSNodeTypes.STRING:
							case JSNodeTypes.IDENTIFIER:
							case JSNodeTypes.GET_PROPERTY:
								processAssignment(elements, arg1, arg3);
								break;
						}
					}
				}
			}
		}
		else if (lhs.getNodeType() == JSNodeTypes.GROUP)
		{
			// sees if we are in a self-invoking function
			IParseNode[] nodes = lhs.getChildren();
			for (IParseNode node2 : nodes)
			{
				if (node2.getNodeType() == JSNodeTypes.FUNCTION
						&& (node.getNodeType() != JSNodeTypes.FUNCTION || node.getText().length() == 0))
				{
					IParseNode[] grandChildren = node2.getChildren();
					for (IParseNode node3 : grandChildren)
					{
						processNode(elements, node3);
					}
				}
				else
				{
					processNode(elements, node2);
				}
			}
		}
	}

	/**
	 * processNameValuePair
	 * 
	 * @param elements
	 * @param node
	 */
	private void processNameValuePair(Collection<JSOutlineItem> elements, IParseNode node)
	{
		IParseNode property = node.getChild(0);
		String name = property.getText();
		if (property.getNodeType() == JSNodeTypes.STRING)
		{
			name = name.substring(1, name.length());
		}

		IParseNode value = node.getChild(1);
		Type type = getOutlineType(value);
		switch (value.getNodeType())
		{
			case JSNodeTypes.FUNCTION:
				processFunction(elements, value, new Reference(value, property, name, "")); //$NON-NLS-1$
				break;
			case JSNodeTypes.OBJECT_LITERAL:
				elements.add(new JSOutlineItem(name, type, property, value, value.getChildCount()));
				break;
			default:
				elements.add(new JSOutlineItem(name, type, property, value));
				break;
		}
	}

	private void processStatements(Collection<JSOutlineItem> elements, IParseNode node)
	{
		// processes named functions first
		IParseNode child;
		int size = node.getChildCount();
		for (int i = 0; i < size; ++i)
		{
			child = node.getChild(i);
			if (child.getNodeType() == JSNodeTypes.FUNCTION && child.getText().length() > 0)
			{
				processNode(elements, child);
			}
		}
		// processes var declarations
		for (int i = 0; i < size; ++i)
		{
			child = node.getChild(i);
			if (child.getNodeType() == JSNodeTypes.VAR)
			{
				processNode(elements, child);
			}
		}
		// processes var assignments, identifiers, and name/value pairs
		short childType;
		for (int i = 0; i < size; ++i)
		{
			child = node.getChild(i);
			childType = child.getNodeType();
			if (childType == JSNodeTypes.ASSIGN || childType == JSNodeTypes.IDENTIFIER
					|| childType == JSNodeTypes.NAME_VALUE_PAIR || childType == JSNodeTypes.INVOKE
					|| childType == JSNodeTypes.RETURN)
			{
				processNode(elements, child);
			}
		}
		// processes if statements
		for (int i = 0; i < size; ++i)
		{
			child = node.getChild(i);
			if (child.getNodeType() == JSNodeTypes.IF)
			{
				processNode(elements, child);
			}
		}
		// process try/catch statements
		for (int i = 0; i < size; ++i)
		{
			child = node.getChild(i);
			childType = child.getNodeType();
			if (childType == JSNodeTypes.TRY || childType == JSNodeTypes.CATCH)
			{
				processNode(elements, child);
			}
		}
	}

	private void processVar(Collection<JSOutlineItem> elements, IParseNode node)
	{
		// processes all declarations
		IParseNode declaration;
		IParseNode identifier, value;
		Reference reference;
		int size = node.getChildCount();
		for (int i = 0; i < size; ++i)
		{
			declaration = node.getChild(i);
			identifier = declaration.getChild(0);
			value = declaration.getChild(1);

			if (value.getNodeType() != JSNodeTypes.EMPTY)
			{
				while (value.getNodeType() == JSNodeTypes.ASSIGN)
				{
					value = value.getChild(1);
				}
			}

			reference = new Reference(node, identifier, identifier.getText(), CONTAINER_TYPE);
			addValue(elements, reference, value, node);
		}
	}

	private static int getChildrenCount(IParseNode node)
	{
		int result = 0;
		IParseNode child;
		int size = node.getChildCount();
		for (int i = 0; i < size; ++i)
		{
			child = node.getChild(i);

			switch (child.getNodeType())
			{
				case JSNodeTypes.ASSIGN:
					IParseNode lhs = child.getChild(0);
					IParseNode rhs = child.getChild(1);
					short lhsTypeIndex = lhs.getNodeType();
					short rhsTypeIndex = rhs.getNodeType();

					boolean identifierOrProperty = (lhsTypeIndex == JSNodeTypes.IDENTIFIER || lhsTypeIndex == JSNodeTypes.GET_PROPERTY);
					boolean ofInterest = (rhsTypeIndex == JSNodeTypes.FUNCTION
							|| rhsTypeIndex == JSNodeTypes.OBJECT_LITERAL || rhsTypeIndex == JSNodeTypes.INVOKE);
					if (identifierOrProperty && ofInterest)
					{
						result++;
					}
					break;
				case JSNodeTypes.FUNCTION:
				case JSNodeTypes.VAR:
					result++;
					break;
				case JSNodeTypes.IF:
				case JSNodeTypes.TRY:
				case JSNodeTypes.CATCH:
				case JSNodeTypes.STATEMENTS:
					result += getChildrenCount(child);
					break;
				case JSNodeTypes.RETURN:
					if (child.getChildCount() > 0)
					{
						IParseNode grandchild = child.getChild(0);
						if (grandchild.getNodeType() == JSNodeTypes.OBJECT_LITERAL)
						{
							result++;
						}
					}
					break;
				case JSNodeTypes.INVOKE:
					if (child.getChildCount() > 0)
					{
						IParseNode grandchild = child.getChild(0);
						if (grandchild.getNodeType() == JSNodeTypes.FUNCTION)
						{
							result++;
						}
					}
					break;
			}
		}

		return result;
	}

	private static Type getOutlineType(IParseNode node)
	{
		switch (node.getNodeType())
		{
			case JSNodeTypes.ARRAY_LITERAL:
				return Type.ARRAY;
			case JSNodeTypes.TRUE:
			case JSNodeTypes.FALSE:
				return Type.BOOLEAN;
			case JSNodeTypes.FUNCTION:
				return Type.FUNCTION;
			case JSNodeTypes.NULL:
				return Type.NULL;
			case JSNodeTypes.NUMBER:
				return Type.NUMBER;
			case JSNodeTypes.OBJECT_LITERAL:
				return Type.OBJECT_LITERAL;
			case JSNodeTypes.REGEX:
				return Type.REGEX;
			case JSNodeTypes.STRING:
				return Type.STRING;
			default:
				return Type.PROPERTY;
		}
	}
}
