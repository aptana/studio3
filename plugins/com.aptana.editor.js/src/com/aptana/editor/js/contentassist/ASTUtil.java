/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.IDebugScopes;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.inferencing.JSNodeTypeInferrer;
import com.aptana.editor.js.inferencing.JSScope;
import com.aptana.editor.js.inferencing.JSTypeMapper;
import com.aptana.editor.js.inferencing.JSTypeUtil;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.IJSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.index.core.Index;
import com.aptana.parsing.ast.IParseNode;

/**
 * ASTUtil
 */
public class ASTUtil
{
	/**
	 * Prevent instantiation of this class
	 */
	private ASTUtil()
	{
	}

	/**
	 * getGetPropertyNode
	 * 
	 * @param targetNode
	 * @param statementNode
	 * @return
	 */
	public static JSGetPropertyNode getGetPropertyNode(IParseNode targetNode, IParseNode statementNode)
	{
		JSGetPropertyNode propertyNode = null;

		if (targetNode != null)
		{
			if (targetNode.getNodeType() == IJSNodeTypes.GET_PROPERTY)
			{
				propertyNode = (JSGetPropertyNode) targetNode;
			}
			else if (targetNode.getNodeType() == IJSNodeTypes.ARGUMENTS)
			{
				IParseNode candidate = targetNode.getParent().getFirstChild();

				if (candidate instanceof JSGetPropertyNode)
				{
					propertyNode = (JSGetPropertyNode) candidate;
				}
			}
			else
			{
				IParseNode parentNode = targetNode.getParent();

				if (parentNode != null && parentNode.getNodeType() == IJSNodeTypes.GET_PROPERTY)
				{
					propertyNode = (JSGetPropertyNode) parentNode;
				}
			}
		}

		if (propertyNode == null && statementNode != null)
		{
			if (statementNode.getNodeType() == IJSNodeTypes.GET_PROPERTY)
			{
				propertyNode = (JSGetPropertyNode) statementNode;
			}
			else
			{
				IParseNode child = statementNode.getFirstChild();

				if (child != null && child.getNodeType() == IJSNodeTypes.GET_PROPERTY)
				{
					propertyNode = (JSGetPropertyNode) child;
				}
			}
		}

		return propertyNode;
	}

	/**
	 * getGlobalScope
	 * 
	 * @param node
	 * @return
	 */
	public static JSScope getGlobalScope(IParseNode node)
	{
		JSScope result = null;

		if (node != null)
		{
			IParseNode root = node;

			while (root != null)
			{
				if (root instanceof JSParseRootNode)
				{
					result = ((JSParseRootNode) root).getGlobals();
					break;
				}
				else
				{
					root = root.getParent();
				}
			}
		}

		return result;
	}

	/**
	 * getScopeAtOffset
	 * 
	 * @param node
	 * @param offset
	 * @return
	 */
	public static JSScope getScopeAtOffset(IParseNode node, int offset)
	{
		JSScope result = null;

		// grab global scope
		JSScope global = ASTUtil.getGlobalScope(node);

		if (global != null)
		{
			JSScope candidate = global.getScopeAtOffset(offset);

			result = (candidate != null) ? candidate : global;
		}

		return result;
	}

	/**
	 * getParentObjectTypes
	 * 
	 * @param projectIndex
	 * @param fileURI
	 * @param targetNode
	 * @param getPropertyNode
	 * @param offset
	 * @return
	 */
	public static List<String> getParentObjectTypes(Index projectIndex, URI fileURI, IParseNode targetNode,
			JSGetPropertyNode getPropertyNode, int offset)
	{
		List<String> result = new ArrayList<String>();

		if (getPropertyNode != null)
		{
			JSScope localScope = ASTUtil.getScopeAtOffset(targetNode, offset);

			if (localScope != null)
			{
				List<String> typeList = Collections.emptyList();

				// lookup in current file
				IParseNode lhs = getPropertyNode.getLeftHandSide();

				if (lhs instanceof JSNode)
				{
					JSNodeTypeInferrer typeWalker = new JSNodeTypeInferrer(localScope, projectIndex, fileURI);

					typeWalker.visit((JSNode) lhs);

					typeList = typeWalker.getTypes();
				}

				IdeLog.logInfo(JSPlugin.getDefault(),
						"types: " + StringUtil.join(", ", typeList), IDebugScopes.CONTENT_ASSIST_TYPES); //$NON-NLS-1$ //$NON-NLS-2$

				// add all properties of each type to our proposal list
				for (String type : typeList)
				{
					// Fix up type names as might be necessary
					type = JSTypeMapper.getInstance().getMappedType(type);

					if (JSTypeUtil.isFunctionPrefix(type))
					{
						String functionType = JSTypeUtil.getFunctionSignatureType(type);

						result.add(functionType);
					}
					else if (type.startsWith(JSTypeConstants.GENERIC_ARRAY_OPEN))
					{
						result.add(JSTypeConstants.ARRAY_TYPE);
					}
					else
					{
						result.add(type);
					}
				}
			}
		}

		return result;
	}
}
