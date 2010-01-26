package com.aptana.editor.js.outline;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.parsing.ast.IParseNode;

/**
 * @author Kevin Lindsey
 * @author Michael Xia
 */
class Reference
{
	private static final String DELIMITER = "/"; //$NON-NLS-1$

	private String fScope;
	private String fName;
	private String fType;

	public Reference(IParseNode node, String name, String type)
	{
		this(createScopeString(node), name, type);
	}

	public Reference(String scope, String name, String type)
	{
		fScope = scope;
		fName = name;
		fType = type;
	}

	public String getName()
	{
		return fName;
	}

	public String getScope()
	{
		return fScope;
	}

	@Override
	public String toString()
	{
		if (fScope.equals(fType))
		{
			return fScope + fName;
		}
		return fScope + fType + fName;
	}

	static String createScopeString(IParseNode node)
	{
		List<String> parts = new ArrayList<String>();
		IParseNode currentNode = node;
		IParseNode parent;

		switch (currentNode.getType())
		{
			case JSNodeTypes.IDENTIFIER:
			case JSNodeTypes.THIS:
				parent = currentNode.getParent();
				if (parent.getType() == JSNodeTypes.GET_PROPERTY)
				{
					if (parent.getChild(1) == currentNode)
					{
						parts.add(parent.toString());
					}
				}
				else
				{
					parts.add(currentNode.getText());
				}
				currentNode = parent;
				break;
			case JSNodeTypes.GET_PROPERTY:
				parts.add(currentNode.getChild(0).toString());
				currentNode = currentNode.getParent();
				break;
			case JSNodeTypes.FUNCTION:
				parent = currentNode.getParent();

				// NOTE: The following block is for 'dojo.lang.extend', 'MochiKit.Base.update',
				// and 'Object.extend' support
				if (parent != null && parent.getType() == JSNodeTypes.NAME_VALUE_PAIR)
				{
					IParseNode grandparent = parent.getParent();
					if (grandparent != null && grandparent.getType() == JSNodeTypes.OBJECT_LITERAL)
					{
						IParseNode greatgrandparent = grandparent.getParent();
						if (greatgrandparent != null && greatgrandparent.getType() == JSNodeTypes.ARGUMENTS)
						{
							parts.add(greatgrandparent.getChild(0) + "."); //$NON-NLS-1$
						}
					}
				}
				currentNode = parent;
				break;
		}

		while (currentNode != null)
		{
			switch (currentNode.getType())
			{
				case JSNodeTypes.FUNCTION:
					String functionName = currentNode.getText();
					if (functionName.length() > 0)
					{
						parts.add(functionName);
					}
					else
					{
						// checks for the case where we are inside a self invoking function, where the scope is of the
						// form
						// invoke / group / function
						IParseNode parentNode = currentNode.getParent();
						IParseNode grandParentNode = null;
						if (parentNode != null)
						{
							grandParentNode = parentNode.getParent();
						}

						if (parentNode != null && grandParentNode != null && parentNode.getType() == JSNodeTypes.GROUP
								&& grandParentNode.getType() == JSNodeTypes.INVOKE)
						{
							currentNode = grandParentNode;
						}
						else
						{
							// calculates name for anonymous function
							String path = ""; //$NON-NLS-1$
							IParseNode p = currentNode;
							IParseNode currentParent;
							int index;
							while (p != null)
							{
								currentParent = p.getParent();
								index = 0;
								if (currentParent != null)
								{
									index = currentParent.getIndex(p);
								}
								path = MessageFormat.format("[{0}]{1}{2}", index, p.getText(), path); //$NON-NLS-1$

								p = currentParent;
							}
							parts.add(path);
						}
					}
					break;
				case JSNodeTypes.NAME_VALUE_PAIR:
					IParseNode property = currentNode.getChild(0);
					parts.add(property.getText());
					break;
				case JSNodeTypes.DECLARATION:
					IParseNode assignedValue = currentNode.getChild(1);
					if (assignedValue.getType() == JSNodeTypes.OBJECT_LITERAL)
					{
						parts.add(currentNode.getChild(0).getText());
					}
					break;
			}

			currentNode = currentNode.getParent();
		}

		Collections.reverse(parts);

		return DELIMITER + join(DELIMITER, parts.toArray(new String[parts.size()]));
	}

	private static String join(String delimiter, String[] items)
	{
		int length = items.length;
		if (length == 0)
		{
			return ""; //$NON-NLS-1$
		}
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < length - 1; ++i)
		{
			text.append(items[i]).append(delimiter);
		}
		text.append(items[length - 1]);
		return text.toString();
	}
}
