package com.aptana.editor.js.outline;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.js.outline.JSOutlineItem.Type;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.parsing.ast.IParseNode;

public class JSOutlineContentProvider extends CommonOutlineContentProvider
{
	private static final String CONTAINER_TYPE = "/"; //$NON-NLS-1$
	private static final String PROPERTY_TYPE = "."; //$NON-NLS-1$

	private Map<String, JSOutlineItem> fItemsByScope;

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
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof AbstractThemeableEditor)
		{
			fItemsByScope.clear();
			return super.getChildren(parentElement);
		}
		else if (parentElement instanceof JSOutlineItem)
		{
			JSOutlineItem item = (JSOutlineItem) parentElement;
			return filter(item.getAllReferenceNodes());
		}
		return super.getChildren(parentElement);
	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof JSOutlineItem)
		{
			JSOutlineItem item = (JSOutlineItem) element;
			return item.getReferenceNode().getParent();
		}

		return super.getParent(element);
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
		List<JSOutlineItem> elements = new ArrayList<JSOutlineItem>();
		for (IParseNode node : nodes)
		{
			processNode(elements, node);
		}
		return elements.toArray(new JSOutlineItem[elements.size()]);
	}

	private void addValue(List<JSOutlineItem> elements, Reference reference, IParseNode value)
	{
		addValue(elements, reference, value, null);
	}

	private void addValue(List<JSOutlineItem> elements, Reference reference, IParseNode value, IParseNode parent)
	{
		boolean processed = false;
		switch (value.getType())
		{
			case JSNodeTypes.FUNCTION:
				processFunction(elements, value, reference);
				processed = true;
				break;
			case JSNodeTypes.INVOKE:
				IParseNode child = value.getChild(0);
				if (child.getType() == JSNodeTypes.FUNCTION)
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
			if (!fItemsByScope.containsKey(path))
			{
				int count = 0;
				if (value.getType() == JSNodeTypes.OBJECT_LITERAL)
				{
					count = value.getChildrenCount();
				}
				if (parent == null)
				{
					parent = value.getParent();
				}

				JSOutlineItem item = new JSOutlineItem(reference.getName(), getOutlineType(value), parent, value, count);
				fItemsByScope.put(path, item);
				elements.add(item);
			}
		}
	}

	private void addVirtualChild(List<JSOutlineItem> elements, Reference reference, IParseNode node, IParseNode target)
	{
		String key = reference.getScope();
		JSOutlineItem item;
		if (fItemsByScope.containsKey(key))
		{
			item = fItemsByScope.get(key);
		}
		else
		{
			// gets the outline node type
			Type type = (node.getType() == JSNodeTypes.FUNCTION) ? Type.FUNCTION : Type.PROPERTY;
			// creates the outline item
			item = new JSOutlineItem(node.getText(), type, node, node);
			// caches associated by scope
			fItemsByScope.put(key, item);
			// adds item to the result list
			elements.add(item);
		}
		item.addVirtualChild(target);
	}

	private void processNode(List<JSOutlineItem> elements, IParseNode node)
	{
		short type = node.getType();
		switch (type)
		{
			case JSNodeTypes.ASSIGN:
				processAssignment(elements, node.getChild(0), node.getChild(1));
				break;
			case JSNodeTypes.FUNCTION:
				processFunction(elements, node, null);
				break;
			case JSNodeTypes.GROUP:
				if (node.getChildrenCount() > 0)
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
				int size = node.getChildrenCount();
				for (int i = 0; i < size; ++i)
				{
					processNode(elements, node.getChild(i));
				}
				break;
			case JSNodeTypes.RETURN:
				if (node.getChildrenCount() > 0)
				{
					IParseNode child = node.getChild(0);
					if (child.getType() == JSNodeTypes.OBJECT_LITERAL)
					{
						size = child.getChildrenCount();
						for (int i = 0; i < size; ++i)
						{
							processNode(elements, child.getChild(i));
						}
					}
				}
				break;
			case JSNodeTypes.STATEMENTS:
				// processes named functions first
				IParseNode child;
				size = node.getChildrenCount();
				for (int i = 0; i < size; ++i)
				{
					child = node.getChild(i);
					if (child.getType() == JSNodeTypes.FUNCTION && child.getText().length() > 0)
					{
						processNode(elements, child);
					}
				}
				// processes var declarations
				for (int i = 0; i < size; ++i)
				{
					child = node.getChild(i);
					if (child.getType() == JSNodeTypes.VAR)
					{
						processNode(elements, child);
					}
				}
				// processes var assignments, identifiers, and name/value pairs
				short childType;
				for (int i = 0; i < size; ++i)
				{
					child = node.getChild(i);
					childType = child.getType();
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
					if (child.getType() == JSNodeTypes.IF)
					{
						processNode(elements, child);
					}
				}
				// process try/catch statements
				for (int i = 0; i < size; ++i)
				{
					child = node.getChild(i);
					childType = child.getType();
					if (childType == JSNodeTypes.TRY || childType == JSNodeTypes.CATCH)
					{
						processNode(elements, child);
					}
				}
				break;
			case JSNodeTypes.IF:
			case JSNodeTypes.TRY:
			case JSNodeTypes.CATCH:
				size = node.getChildrenCount();
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

	private void processAssignment(List<JSOutlineItem> elements, IParseNode lhs, IParseNode rhs)
	{
		short lhsType = lhs.getType();
		short rhsType = rhs.getType();

		switch (lhsType)
		{
			case JSNodeTypes.STRING:
				if (rhsType == JSNodeTypes.FUNCTION || rhsType == JSNodeTypes.OBJECT_LITERAL)
				{
					String text = lhs.getText();
					Reference reference = new Reference(lhs.getParent(), text.substring(1, text.length() - 1),
							CONTAINER_TYPE);
					addValue(elements, reference, rhs);
				}
				break;
			case JSNodeTypes.IDENTIFIER:
				if (rhsType == JSNodeTypes.FUNCTION || rhsType == JSNodeTypes.OBJECT_LITERAL)
				{
					Reference reference = new Reference(lhs.getParent(), lhs.getText(), CONTAINER_TYPE);
					addValue(elements, reference, rhs);
				}
				else if (rhsType == JSNodeTypes.INVOKE && rhs.getChildrenCount() == 2)
				{
					IParseNode child = rhs.getChild(0);
					Reference reference = new Reference(lhs.getParent(), lhs.getText(), CONTAINER_TYPE);
					addValue(elements, reference, child);
				}
				break;
			case JSNodeTypes.GET_PROPERTY:
				IParseNode target = null;
				// traverses down the left-side get-property nodes
				while (lhs.getType() == JSNodeTypes.GET_PROPERTY)
				{
					target = lhs.getChild(1);
					lhs = lhs.getChild(0);
				}

				// only processes get-property expressions that begin with an identifier or 'this'
				if (lhs.getType() == JSNodeTypes.IDENTIFIER || lhs.getType() == JSNodeTypes.THIS)
				{
					String scopeString = Reference.createScopeString(lhs.getParent());
					Reference reference;
					if (fItemsByScope.containsKey(scopeString))
					{
						reference = new Reference(scopeString, target.getText(), CONTAINER_TYPE);
						addVirtualChild(elements, reference, lhs, target);
					}
					else
					{
						reference = new Reference(lhs, lhs.getText(), CONTAINER_TYPE);
						addValue(elements, reference, target);

						JSOutlineItem item = fItemsByScope.get(scopeString);
						item.addVirtualChild(target);
					}
				}
				break;
		}
	}

	private void processFunction(List<JSOutlineItem> elements, IParseNode node, Reference reference)
	{
		String name;
		if (node.getType() == JSNodeTypes.FUNCTION && node.getText().length() > 0)
		{
			name = node.getText();
		}
		else
		{
			if (reference != null)
			{
				name = reference.getName();
			}
			else
			{
				name = "<literal>"; //$NON-NLS-1$
			}
		}

		// keeps track of this item's scope so we can add virtual children later, if needed
		if (reference == null)
		{
			reference = new Reference(node, name, CONTAINER_TYPE);
		}

		String fullpath = reference.toString();
		if (!fItemsByScope.containsKey(fullpath))
		{
			IParseNode parameters = node.getChild(0);
			IParseNode body = node.getChild(1);

			JSOutlineItem item = new JSOutlineItem(MessageFormat.format("{0}({1})", name, parameters), Type.FUNCTION, //$NON-NLS-1$
					node, body, getChildrenCount(body));
			fItemsByScope.put(fullpath, item);
			elements.add(item);
		}
	}

	private void processIdentifier(List<JSOutlineItem> elements, IParseNode node)
	{
		IParseNode parent = node.getParent();
		if (parent.getChildrenCount() > 1)
		{
			IParseNode rhs = parent.getChild(1);
			if (rhs == node)
			{
				IParseNode grandparent = parent.getParent();
				if (grandparent != null && grandparent.getChildrenCount() > 1)
				{
					IParseNode target = grandparent.getChild(1);
					Reference reference;
					switch (grandparent.getType())
					{
						case JSNodeTypes.ARGUMENTS:
							// supports dojo.lang.extend, MochiKit.Base.update, and Object.extend
							target = grandparent.getChild(grandparent.getChildrenCount() - 1);
							reference = new Reference(parent, rhs.getText(), PROPERTY_TYPE);
							String parentFullPath = reference.toString();

							// processes all key/value pairs
							IParseNode keyValuePair;
							int size = target.getChildrenCount();
							for (int i = 0; i < size; ++i)
							{
								keyValuePair = target.getChild(i);
								String keyString = keyValuePair.getChild(0).toString();
								Reference keyValueReference = new Reference(parentFullPath, keyString, PROPERTY_TYPE);
								addVirtualChild(elements, keyValueReference, node, keyValuePair);
							}
							break;
						case JSNodeTypes.ASSIGN:
							reference = new Reference(parent, rhs.getText(), PROPERTY_TYPE);
							addValue(elements, reference, target);
							break;
						case JSNodeTypes.GET_PROPERTY:
							IParseNode property = grandparent.getChild(1);
							reference = new Reference(grandparent, property.getText(), PROPERTY_TYPE);
							addVirtualChild(elements, reference, node, target);
							break;
					}
				}
			}
		}
	}

	private void processInvoke(List<JSOutlineItem> elements, IParseNode node)
	{
		IParseNode lhs = node.getChild(0);
		String source = lhs.toString();
		if (CLASS_EXTENDERS.contains(source))
		{
			IParseNode args = node.getChild(1);
			if (args.getType() == JSNodeTypes.ARGUMENTS)
			{
				if (args.getChildrenCount() == 2)
				{
					IParseNode arg1 = args.getChild(0);
					IParseNode arg2 = args.getChild(1);
					if (arg2.getType() == JSNodeTypes.OBJECT_LITERAL)
					{
						switch (arg1.getType())
						{
							case JSNodeTypes.STRING:
							case JSNodeTypes.IDENTIFIER:
							case JSNodeTypes.GET_PROPERTY:
								processAssignment(elements, arg1, arg2);
								break;
						}
					}
				}
				else if (args.getChildrenCount() == 3)
				{
					// EXT case
					IParseNode arg1 = args.getChild(0);
					IParseNode arg3 = args.getChild(2);
					if (arg3.getType() == JSNodeTypes.OBJECT_LITERAL)
					{
						switch (arg1.getType())
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
		else if (lhs.getType() == JSNodeTypes.GROUP)
		{
			// sees if we are in a self-invoking function
			IParseNode[] nodes = lhs.getChildren();
			for (IParseNode node2 : nodes)
			{
				if (node2.getType() == JSNodeTypes.FUNCTION
						&& (node.getType() != JSNodeTypes.FUNCTION || node.getText().length() == 0))
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
	private void processNameValuePair(List<JSOutlineItem> elements, IParseNode node)
	{
		IParseNode property = node.getChild(0);
		String name = property.getText();
		if (property.getType() == JSNodeTypes.STRING)
		{
			name = name.substring(1, name.length());
		}

		IParseNode value = node.getChild(1);
		Type type = getOutlineType(value);
		switch (value.getType())
		{
			case JSNodeTypes.FUNCTION:
				processFunction(elements, value, new Reference(value, name, "")); //$NON-NLS-1$
				break;
			case JSNodeTypes.OBJECT_LITERAL:
				elements.add(new JSOutlineItem(name, type, node, value, value.getChildrenCount()));
				break;
			default:
				elements.add(new JSOutlineItem(name, type, node, value));
				break;
		}
	}

	private void processVar(List<JSOutlineItem> elements, IParseNode node)
	{
		// processes all declarations
		IParseNode declaration;
		IParseNode identifier, value;
		Reference reference;
		int size = node.getChildrenCount();
		for (int i = 0; i < size; ++i)
		{
			declaration = node.getChild(i);
			identifier = declaration.getChild(0);
			value = declaration.getChild(1);

			if (value.getType() != JSNodeTypes.EMPTY)
			{
				while (value.getType() == JSNodeTypes.ASSIGN)
				{
					value = value.getChild(1);
				}
			}

			reference = new Reference(node, identifier.getText(), CONTAINER_TYPE);
			addValue(elements, reference, value, node);
		}
	}

	private static int getChildrenCount(IParseNode node)
	{
		int result = 0;
		IParseNode child;
		int size = node.getChildrenCount();
		for (int i = 0; i < size; ++i)
		{
			child = node.getChild(i);

			switch (child.getType())
			{
				case JSNodeTypes.ASSIGN:
					IParseNode lhs = child.getChild(0);
					IParseNode rhs = child.getChild(1);
					short lhsTypeIndex = lhs.getType();
					short rhsTypeIndex = rhs.getType();

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
					if (child.getChildrenCount() > 0)
					{
						IParseNode grandchild = child.getChild(0);
						if (grandchild.getType() == JSNodeTypes.OBJECT_LITERAL)
						{
							result++;
						}
					}
					break;
				case JSNodeTypes.INVOKE:
					if (child.getChildrenCount() > 0)
					{
						IParseNode grandchild = child.getChild(0);
						if (grandchild.getType() == JSNodeTypes.FUNCTION)
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
		switch (node.getType())
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
