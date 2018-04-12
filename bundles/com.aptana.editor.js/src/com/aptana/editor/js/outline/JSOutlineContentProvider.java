// $codepro.audit.disable unnecessaryImport
// NOTE: CodePro is saying "import com.aptana.editor.js.outline.JSOutlineItem.Type;"
// is unnecessary even though it is required. Also, I couldn't disable code pro
// on that import statement and had to place this at the file level
/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.outline;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.common.outline.CommonOutlinePageInput;
import com.aptana.editor.js.outline.JSOutlineItem.Type;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;
import com.aptana.parsing.lexer.IRange;

public class JSOutlineContentProvider extends CommonOutlineContentProvider
{

	private static final String CONTAINER_TYPE = "/"; //$NON-NLS-1$
	private static final String PROPERTY_TYPE = "."; //$NON-NLS-1$
	private static final String FUNCTION_LITERAL = "<function>"; //$NON-NLS-1$

	private Map<String, JSOutlineItem> fItemsByScope;
	private JSOutlineItem fLastAddedItem;

	private static final Set<String> CLASS_EXTENDERS;
	static
	{
		// @formatter:off
		CLASS_EXTENDERS = CollectionsUtil.newSet(
			"dojo.lang.extend", //$NON-NLS-1$
			"Ext.extend", //$NON-NLS-1$
			"jQuery.extend", //$NON-NLS-1$
			"MochiKit.Base.update", //$NON-NLS-1$
			"Object.extend", //$NON-NLS-1$
			"qx.Class.define", //$NON-NLS-1$
			"qx.Interface.define", //$NON-NLS-1$
			"qx.Theme.define", //$NON-NLS-1$
			"qx.Mixin.define" //$NON-NLS-1$
		);
		// @formatter:on
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
		if (parentElement instanceof AbstractThemeableEditor || parentElement instanceof ParseRootNode
				|| parentElement instanceof CommonOutlinePageInput)
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
			case IJSNodeTypes.FUNCTION:
				processFunction(elements, value, reference);
				processed = true;
				break;
			case IJSNodeTypes.INVOKE:
				IParseNode child = value.getChild(0);
				if (child.getNodeType() == IJSNodeTypes.FUNCTION)
				{
					processFunction(elements, child, reference);
					processed = true;
				}
				else
				{
					value = child; // $codepro.audit.disable questionableAssignment
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
				if (value.getNodeType() == IJSNodeTypes.OBJECT_LITERAL)
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
			Type type = (node.getNodeType() == IJSNodeTypes.FUNCTION) ? Type.FUNCTION : Type.PROPERTY;
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
			case IJSNodeTypes.ASSIGN:
				processAssignment(elements, node.getChild(0), node.getChild(1));
				break;
			case IJSNodeTypes.FUNCTION:
				processFunction(elements, node, null);
				break;
			case IJSNodeTypes.GROUP:
				if (node.getChildCount() > 0)
				{
					processNode(elements, node.getChild(0));
				}
				break;
			case IJSNodeTypes.IDENTIFIER:
				processIdentifier(elements, node);
				break;
			case IJSNodeTypes.INVOKE:
				processInvoke(elements, node);
				break;
			case IJSNodeTypes.NAME_VALUE_PAIR:
				processNameValuePair(elements, node);
				break;
			case IJSNodeTypes.OBJECT_LITERAL:
				int size = node.getChildCount();
				for (int i = 0; i < size; ++i)
				{
					processNode(elements, node.getChild(i));
				}
				break;
			case IJSNodeTypes.RETURN:
				if (node.getChildCount() > 0)
				{
					IParseNode child = node.getChild(0);
					if (child.getNodeType() == IJSNodeTypes.OBJECT_LITERAL)
					{
						size = child.getChildCount();
						for (int i = 0; i < size; ++i)
						{
							processNode(elements, child.getChild(i));
						}
					}
				}
				break;
			case IJSNodeTypes.STATEMENTS:
				processStatements(elements, node);
				break;
			case IJSNodeTypes.IF:
			case IJSNodeTypes.TRY:
			case IJSNodeTypes.CATCH:
				size = node.getChildCount();
				for (int i = 0; i < size; ++i)
				{
					processNode(elements, node.getChild(i));
				}
				break;
			case IJSNodeTypes.THIS:
			case IJSNodeTypes.VAR:
				processVar(elements, node);
				break;
		}
	}

	private IParseNode processAssignment(Collection<JSOutlineItem> elements, IParseNode lhs, IParseNode rhs)
	{
		short lhsType = lhs.getNodeType();
		short rhsType = rhs.getNodeType();
		if (rhsType == IJSNodeTypes.ASSIGN)
		{
			// processes the right-hand assignment recursively
			rhs = processAssignment(elements, rhs.getChild(0), rhs.getChild(1)); // $codepro.audit.disable
																					// questionableAssignment
			rhsType = rhs.getNodeType();
		}

		switch (lhsType)
		{
			case IJSNodeTypes.STRING:
				if (rhsType == IJSNodeTypes.FUNCTION || rhsType == IJSNodeTypes.OBJECT_LITERAL)
				{
					String text = lhs.getText();
					Reference reference = new Reference(lhs.getParent(), lhs, text.substring(1, text.length() - 1),
							CONTAINER_TYPE);
					addValue(elements, reference, rhs);
				}
				break;
			case IJSNodeTypes.IDENTIFIER:
				if (rhsType == IJSNodeTypes.FUNCTION || rhsType == IJSNodeTypes.OBJECT_LITERAL)
				{
					Reference reference = new Reference(lhs.getParent(), lhs, lhs.getText(), CONTAINER_TYPE);
					addValue(elements, reference, rhs);
				}
				else if (rhsType == IJSNodeTypes.INVOKE && rhs.getChildCount() == 2)
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
			case IJSNodeTypes.GET_PROPERTY:
				IParseNode target = null;
				// traverses down the left-side get-property nodes
				while (lhs.getNodeType() == IJSNodeTypes.GET_PROPERTY)
				{
					target = lhs.getChild(1);
					lhs = lhs.getChild(0); // $codepro.audit.disable questionableAssignment
				}

				// only processes get-property expressions that begin with an identifier or 'this'
				if (lhs.getNodeType() == IJSNodeTypes.IDENTIFIER || lhs.getNodeType() == IJSNodeTypes.THIS)
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
		if (node.getNodeType() == IJSNodeTypes.FUNCTION && node.getText().length() > 0)
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
				name = FUNCTION_LITERAL;
			}
		}

		// keeps track of this item's scope so we can add virtual children later, if needed
		if (reference == null)
		{
			reference = new Reference(node, nameNode, name, CONTAINER_TYPE); // $codepro.audit.disable
																				// questionableAssignment
		}

		String fullpath = reference.toString();
		JSOutlineItem item = fItemsByScope.get(fullpath);
		boolean addToMap = (item == null || item.getType() != Type.FUNCTION);
		if (!addToMap)
		{
			IRange range = item.getSourceRange();
			if (range instanceof IParseNode)
			{
				addToMap = !name.equals(((IParseNode) range).getText());
			}
		}
		if (addToMap)
		{
			String text;
			if (name.endsWith(FUNCTION_LITERAL + ")")) //$NON-NLS-1$
			{
				text = name;
			}
			else
			{
				String pattern = "{0}({1})"; //$NON-NLS-1$
				IParseNode parameters = node.getChild(1);
				String parmsString = parameters.toString();
				if (parmsString.startsWith("(")) //$NON-NLS-1$ // $codepro.audit.disable useCharAtRatherThanStartsWith
				{
					pattern = "{0}{1}"; //$NON-NLS-1$
				}
				text = MessageFormat.format(pattern, name, parmsString);
			}

			IParseNode body = node.getChild(2);
			item = new JSOutlineItem(text, Type.FUNCTION, reference.getNameNode(), body, getChildrenCount(body));
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
						case IJSNodeTypes.ARGUMENTS:
							// supports dojo.lang.extend, MochiKit.Base.update, and Object.extend
							target = grandparent.getChild(grandparent.getChildCount() - 1);
							reference = new Reference(parent, rhs, rhs.getText(), PROPERTY_TYPE);
							String parentFullPath = reference.toString();

							// processes all key/value pairs
							IParseNode keyValuePair;
							IParseNode key;
							String keyString;
							Reference keyValueReference;
							int size = target.getChildCount();
							for (int i = 0; i < size; ++i)
							{
								keyValuePair = target.getChild(i);
								key = keyValuePair.getChild(0);
								keyString = key.toString();
								keyValueReference = new Reference(parentFullPath, key, keyString, PROPERTY_TYPE);
								addVirtualChild(elements, keyValueReference, node, keyValuePair);
							}
							break;
						case IJSNodeTypes.ASSIGN:
							reference = new Reference(parent, rhs, rhs.getText(), PROPERTY_TYPE);
							while (target.getNodeType() == IJSNodeTypes.ASSIGN)
							{
								// finds the right-most element
								target = target.getChild(1);
							}
							addValue(elements, reference, target);
							break;
						case IJSNodeTypes.GET_PROPERTY:
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
			if (args.getNodeType() == IJSNodeTypes.ARGUMENTS)
			{
				if (args.getChildCount() == 2)
				{
					IParseNode arg1 = args.getChild(0);
					IParseNode arg2 = args.getChild(1);
					if (arg2.getNodeType() == IJSNodeTypes.OBJECT_LITERAL)
					{
						switch (arg1.getNodeType())
						{
							case IJSNodeTypes.STRING:
							case IJSNodeTypes.IDENTIFIER:
							case IJSNodeTypes.GET_PROPERTY:
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
					if (arg3.getNodeType() == IJSNodeTypes.OBJECT_LITERAL)
					{
						switch (arg1.getNodeType())
						{
							case IJSNodeTypes.STRING:
							case IJSNodeTypes.IDENTIFIER:
							case IJSNodeTypes.GET_PROPERTY:
								processAssignment(elements, arg1, arg3);
								break;
						}
					}
				}
			}
		}
		else if (lhs.getNodeType() == IJSNodeTypes.GROUP)
		{
			// sees if we are in a self-invoking function
			IParseNode[] nodes = lhs.getChildren();
			for (IParseNode node2 : nodes)
			{
				if (node2.getNodeType() == IJSNodeTypes.FUNCTION
						&& (node.getNodeType() != IJSNodeTypes.FUNCTION || node.getText().length() == 0))
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
		else if (lhs.getNodeType() == IJSNodeTypes.FUNCTION)
		{
			// sees if we are in a self-invoking function
			if (node.getNodeType() != IJSNodeTypes.FUNCTION || node.getText().length() == 0)
			{
				IParseNode[] children = lhs.getChildren();
				for (IParseNode node2 : children)
				{
					processNode(elements, node2);
				}
			}
			else
			{
				processNode(elements, lhs);
			}
		}
		else if (lhs.getNodeType() == IJSNodeTypes.IDENTIFIER)
		{
			IParseNode args = node.getChild(1);
			if (args.getNodeType() == IJSNodeTypes.ARGUMENTS)
			{
				int count = args.getChildCount();
				IParseNode node2;
				for (int i = 0; i < count; ++i)
				{
					node2 = args.getChild(i);
					if (node2.getNodeType() == IJSNodeTypes.FUNCTION)
					{
						processFunction(elements, node2,
								new Reference(node2, node2, MessageFormat.format("{0}(@{1}:{2})", lhs.getText(), i, //$NON-NLS-1$
										FUNCTION_LITERAL), StringUtil.EMPTY));
					}
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
		if (property.getNodeType() == IJSNodeTypes.STRING)
		{
			name = name.substring(1, name.length() - 1);
		}

		IParseNode value = node.getChild(1);
		Type type = getOutlineType(value);
		switch (value.getNodeType())
		{
			case IJSNodeTypes.FUNCTION:
				processFunction(elements, value, new Reference(value, property, name, "")); //$NON-NLS-1$
				break;
			case IJSNodeTypes.OBJECT_LITERAL:
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
			if (child.getNodeType() == IJSNodeTypes.FUNCTION && child.getText().length() > 0)
			{
				processNode(elements, child);
			}
		}
		// processes if statements
		for (int i = 0; i < size; ++i)
		{
			child = node.getChild(i);
			if (child.getNodeType() == IJSNodeTypes.IF)
			{
				processNode(elements, child);
			}
		}
		// processes var declarations
		for (int i = 0; i < size; ++i)
		{
			child = node.getChild(i);
			if (child.getNodeType() == IJSNodeTypes.VAR)
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
			if (childType == IJSNodeTypes.ASSIGN || childType == IJSNodeTypes.IDENTIFIER
					|| childType == IJSNodeTypes.NAME_VALUE_PAIR || childType == IJSNodeTypes.INVOKE
					|| childType == IJSNodeTypes.GROUP || childType == IJSNodeTypes.RETURN)
			{
				processNode(elements, child);
			}
		}
		// process try/catch statements
		for (int i = 0; i < size; ++i)
		{
			child = node.getChild(i);
			childType = child.getNodeType();
			if (childType == IJSNodeTypes.TRY || childType == IJSNodeTypes.CATCH)
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

			if (value.getNodeType() != IJSNodeTypes.EMPTY)
			{
				while (value.getNodeType() == IJSNodeTypes.ASSIGN)
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
				case IJSNodeTypes.ASSIGN:
					IParseNode lhs = child.getChild(0);
					IParseNode rhs = child.getChild(1);
					short lhsTypeIndex = lhs.getNodeType();
					short rhsTypeIndex = rhs.getNodeType();

					boolean identifierOrProperty = (lhsTypeIndex == IJSNodeTypes.IDENTIFIER || lhsTypeIndex == IJSNodeTypes.GET_PROPERTY);
					boolean ofInterest = (rhsTypeIndex == IJSNodeTypes.FUNCTION
							|| rhsTypeIndex == IJSNodeTypes.OBJECT_LITERAL || rhsTypeIndex == IJSNodeTypes.INVOKE);
					if (identifierOrProperty && ofInterest)
					{
						result++;
					}
					break;
				case IJSNodeTypes.FUNCTION:
				case IJSNodeTypes.VAR:
					result++;
					break;
				case IJSNodeTypes.IF:
				case IJSNodeTypes.TRY:
				case IJSNodeTypes.CATCH:
				case IJSNodeTypes.STATEMENTS:
					result += getChildrenCount(child);
					break;
				case IJSNodeTypes.RETURN:
					if (child.getChildCount() > 0)
					{
						IParseNode grandchild = child.getChild(0);
						if (grandchild.getNodeType() == IJSNodeTypes.OBJECT_LITERAL)
						{
							result++;
						}
					}
					break;
				case IJSNodeTypes.INVOKE:
					if (child.getChildCount() > 0)
					{
						IParseNode grandchild = child.getChild(0);
						if (grandchild.getNodeType() == IJSNodeTypes.FUNCTION)
						{
							result++;
						}
						else if (grandchild.getNodeType() == IJSNodeTypes.IDENTIFIER && child.getChildCount() > 1)
						{
							IParseNode args = child.getChild(1);
							if (args.getNodeType() == IJSNodeTypes.ARGUMENTS)
							{
								int count = args.getChildCount();
								for (int j = 0; j < count; ++j)
								{
									if (args.getChild(j).getNodeType() == IJSNodeTypes.FUNCTION)
									{
										result++;
									}
								}
							}
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
			case IJSNodeTypes.ARRAY_LITERAL:
				return Type.ARRAY;
			case IJSNodeTypes.TRUE:
			case IJSNodeTypes.FALSE:
				return Type.BOOLEAN;
			case IJSNodeTypes.FUNCTION:
				return Type.FUNCTION;
			case IJSNodeTypes.NULL:
				return Type.NULL;
			case IJSNodeTypes.NUMBER:
				return Type.NUMBER;
			case IJSNodeTypes.OBJECT_LITERAL:
				return Type.OBJECT_LITERAL;
			case IJSNodeTypes.REGEX:
				return Type.REGEX;
			case IJSNodeTypes.STRING:
				return Type.STRING;
			default:
				return Type.PROPERTY;
		}
	}
}
