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
import java.util.Collections;
import java.util.List;

import com.aptana.core.util.StringUtil;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
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
	private IParseNode fNameNode;

	Reference(IParseNode scopeNode, IParseNode nameNode, String name, String type)
	{
		this(createScopeString(scopeNode), nameNode, name, type);
	}

	Reference(String scope, IParseNode nameNode, String name, String type)
	{
		fScope = scope;
		fNameNode = nameNode;
		fName = name;
		fType = type;
	}

	public IParseNode getNameNode()
	{
		return fNameNode;
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

		switch (currentNode.getNodeType())
		{
			case IJSNodeTypes.IDENTIFIER:
			case IJSNodeTypes.THIS:
				parent = currentNode.getParent();
				if (parent.getNodeType() == IJSNodeTypes.GET_PROPERTY)
				{
					if (parent.getChild(1) == currentNode) // $codepro.audit.disable useEquals
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
			case IJSNodeTypes.GET_PROPERTY:
				parts.add(currentNode.getChild(0).toString());
				currentNode = currentNode.getParent();
				break;
			case IJSNodeTypes.FUNCTION:
				parent = currentNode.getParent();

				// NOTE: The following block is for 'dojo.lang.extend', 'MochiKit.Base.update',
				// and 'Object.extend' support
				if (parent != null && parent.getNodeType() == IJSNodeTypes.NAME_VALUE_PAIR)
				{
					IParseNode grandparent = parent.getParent();
					if (grandparent != null && grandparent.getNodeType() == IJSNodeTypes.OBJECT_LITERAL)
					{
						IParseNode greatgrandparent = grandparent.getParent();
						if (greatgrandparent != null && greatgrandparent.getNodeType() == IJSNodeTypes.ARGUMENTS)
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
			switch (currentNode.getNodeType())
			{
				case IJSNodeTypes.FUNCTION:
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

						if (parentNode != null && grandParentNode != null
								&& parentNode.getNodeType() == IJSNodeTypes.GROUP
								&& grandParentNode.getNodeType() == IJSNodeTypes.INVOKE)
						{
							currentNode = grandParentNode;
						}
						else
						{
							// calculates name for anonymous function
							String path = StringUtil.EMPTY;
							IParseNode p = currentNode;
							IParseNode currentParent;
							int index;
							while (p != null)
							{
								currentParent = p.getParent();
								index = 0;
								if (currentParent != null)
								{
									index = p.getIndex();
								}
								path = MessageFormat.format("[{0}]{1}{2}", index, p.getText(), path); //$NON-NLS-1$

								p = currentParent;
							}
							parts.add(path);
						}
					}
					break;
				case IJSNodeTypes.NAME_VALUE_PAIR:
					IParseNode property = currentNode.getChild(0);
					parts.add(property.getText());
					break;
				case IJSNodeTypes.DECLARATION:
					IParseNode assignedValue = currentNode.getChild(1);
					if (assignedValue.getNodeType() == IJSNodeTypes.OBJECT_LITERAL)
					{
						parts.add(currentNode.getChild(0).getText());
					}
					break;
			}

			currentNode = currentNode.getParent();
		}

		Collections.reverse(parts);

		return DELIMITER + StringUtil.join(DELIMITER, parts);
	}
}
