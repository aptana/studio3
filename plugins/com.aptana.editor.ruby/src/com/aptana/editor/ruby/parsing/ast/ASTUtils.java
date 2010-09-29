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
package com.aptana.editor.ruby.parsing.ast;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jrubyparser.StaticScope;
import org.jrubyparser.ast.ArgsNode;
import org.jrubyparser.ast.ArgumentNode;
import org.jrubyparser.ast.ArrayNode;
import org.jrubyparser.ast.CallNode;
import org.jrubyparser.ast.ClassNode;
import org.jrubyparser.ast.Colon2Node;
import org.jrubyparser.ast.ConstNode;
import org.jrubyparser.ast.DAsgnNode;
import org.jrubyparser.ast.DStrNode;
import org.jrubyparser.ast.FCallNode;
import org.jrubyparser.ast.FalseNode;
import org.jrubyparser.ast.FixnumNode;
import org.jrubyparser.ast.HashNode;
import org.jrubyparser.ast.IArgumentNode;
import org.jrubyparser.ast.INameNode;
import org.jrubyparser.ast.IterNode;
import org.jrubyparser.ast.ListNode;
import org.jrubyparser.ast.LocalAsgnNode;
import org.jrubyparser.ast.ModuleNode;
import org.jrubyparser.ast.MultipleAsgnNode;
import org.jrubyparser.ast.NilNode;
import org.jrubyparser.ast.Node;
import org.jrubyparser.ast.SelfNode;
import org.jrubyparser.ast.SplatNode;
import org.jrubyparser.ast.StrNode;
import org.jrubyparser.ast.SymbolNode;
import org.jrubyparser.ast.TrueNode;
import org.jrubyparser.ast.ZArrayNode;

public class ASTUtils
{

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	private static final String NAMESPACE_DELIMETER = "::"; //$NON-NLS-1$

	public static String[] getArgs(ArgsNode argsNode, StaticScope scope)
	{
		if (argsNode == null)
		{
			return new String[0];
		}

		List<String> arguments = getArguments(argsNode.getPre());
		if (argsNode.getOptionalCount() > 0)
		{
			arguments.addAll(getArguments(argsNode.getOptional()));
		}
		if (argsNode.getRest() != null)
		{
			arguments.add("*" + argsNode.getRest().getName()); //$NON-NLS-1$
		}
		if (argsNode.getBlock() != null)
		{
			arguments.add("&" + scope.getVariables()[argsNode.getBlock().getCount()]); //$NON-NLS-1$
		}
		return arguments.toArray(new String[arguments.size()]);
	}

	public static List<String> getArguments(ListNode argList)
	{
		if (argList == null)
		{
			return new ArrayList<String>();
		}
		List<String> arguments = new ArrayList<String>();
		List<Node> childNodes = argList.childNodes();
		for (Node node : childNodes)
		{
			if (node instanceof ArgumentNode)
			{
				arguments.add(((ArgumentNode) node).getName());
			}
			else if (node instanceof LocalAsgnNode)
			{
				LocalAsgnNode local = (LocalAsgnNode) node;
				arguments.add(MessageFormat.format("{0} = {1}", local.getName(), getStringRepresentation(local //$NON-NLS-1$
						.getValueNode())));
			}
		}
		return arguments;
	}

	public static List<String> getArgumentsFromFunctionCall(IArgumentNode iVisited)
	{
		List<String> arguments = new ArrayList<String>();
		List<Node> nodes = getArgumentNodesFromFunctionCall(iVisited);
		for (Node node : nodes)
		{
			if (node instanceof DAsgnNode)
			{
				arguments.add(((DAsgnNode) node).getName());
			}
			else
			{
				arguments.add(getStringRepresentation(node));
			}
		}
		return arguments;
	}

	public static List<Node> getArgumentNodesFromFunctionCall(IArgumentNode iVisited)
	{
		List<Node> arguments = new ArrayList<Node>();
		Node argsNode = iVisited.getArgsNode();

		Iterator<Node> iter = null;
		if (argsNode instanceof SplatNode)
		{
			SplatNode splat = (SplatNode) argsNode;
			iter = splat.childNodes().iterator();
		}
		else if (argsNode instanceof ArrayNode)
		{
			ArrayNode arrayNode = (ArrayNode) iVisited.getArgsNode();
			iter = arrayNode.childNodes().iterator();
		}
		else if (argsNode == null)
		{
			// block?
			Node iterNode = null;
			if (iVisited instanceof FCallNode)
			{
				iterNode = ((FCallNode) iVisited).getIterNode();
			}
			else if (iVisited instanceof CallNode)
			{
				iterNode = ((CallNode) iVisited).getIterNode();
			}

			if (iterNode == null)
			{
				return arguments;
			}
			if (iterNode instanceof IterNode)
			{
				// yup, it has a block
				IterNode yeah = (IterNode) iterNode;
				Node varNode = yeah.getVarNode();
				if (varNode instanceof DAsgnNode)
				{
					// single variable in the block
					arguments.add(varNode);
				}
				else if (varNode instanceof MultipleAsgnNode)
				{
					// multiple variables in the block
					MultipleAsgnNode multi = (MultipleAsgnNode) varNode;
					ListNode list = multi.getHeadNode();
					if (list != null)
					{
						iter = list.childNodes().iterator();
					}
					else
					{
						Node multiArgsNode = multi.getArgsNode();
						if (multiArgsNode instanceof DAsgnNode)
						{
							arguments.add(multiArgsNode);
						}
					}
				}
			}
		}
		if (iter == null)
		{
			return arguments;
		}
		while (iter.hasNext())
		{
			arguments.add(iter.next());
		}
		return arguments;
	}

	public static String getFullyQualifiedName(Node node)
	{
		if (node == null)
		{
			return EMPTY_STRING;
		}
		if (node instanceof ConstNode)
		{
			return ((ConstNode) node).getName();
		}
		if (node instanceof Colon2Node)
		{
			Colon2Node colonNode = (Colon2Node) node;
			String prefix = getFullyQualifiedName(colonNode.getLeftNode());
			if (prefix.length() > 0)
			{
				prefix = prefix + NAMESPACE_DELIMETER;
			}
			return prefix + colonNode.getName();
		}
		return getName(node);
	}

	public static String getFullyQualifiedName(Colon2Node node)
	{
		StringBuilder name = new StringBuilder();
		Node left = node.getLeftNode();
		if (left instanceof Colon2Node)
		{
			name.append(getFullyQualifiedName((Colon2Node) left));
		}
		else if (left instanceof ConstNode)
		{
			name.append(((ConstNode) left).getName());
		}
		name.append(NAMESPACE_DELIMETER).append(node.getName());
		return name.toString();
	}

	public static String getName(Node node)
	{
		if (node == null)
		{
			return EMPTY_STRING;
		}
		if (node instanceof ClassNode)
		{
			return getName(((ClassNode) node).getCPath());
		}
		if (node instanceof ModuleNode)
		{
			return getName(((ModuleNode) node).getCPath());
		}
		if (node instanceof INameNode)
		{
			return ((INameNode) node).getName();
		}
		// tries reflection
		try
		{
			Method getNameMethod = node.getClass().getMethod("getName", new Class[] {}); //$NON-NLS-1$
			Object name = getNameMethod.invoke(node, new Object[0]);
			return (String) name;
		}
		catch (Exception e)
		{
			return EMPTY_STRING;
		}
	}

	public static String getStringRepresentation(Node node)
	{
		if (node == null)
		{
			return ""; //$NON-NLS-1$
		}
		if (node instanceof HashNode)
		{
			return "{}"; //$NON-NLS-1$
		}
		if (node instanceof SelfNode)
		{
			return "self"; //$NON-NLS-1$
		}
		if (node instanceof NilNode)
		{
			return "nil"; //$NON-NLS-1$
		}
		if (node instanceof TrueNode)
		{
			return "true"; //$NON-NLS-1$
		}
		if (node instanceof FalseNode)
		{
			return "false"; //$NON-NLS-1$
		}
		if (node instanceof SymbolNode)
		{
			return ':' + ((SymbolNode) node).getName();
		}
		if (node instanceof INameNode)
		{
			return ((INameNode) node).getName();
		}
		if (node instanceof ZArrayNode)
		{
			return "[]"; //$NON-NLS-1$
		}
		if (node instanceof FixnumNode)
		{
			return String.valueOf(((FixnumNode) node).getValue());
		}
		if (node instanceof StrNode)
		{
			return '"' + ((StrNode) node).getValue().toString() + '"';
		}
		if (node instanceof DStrNode)
		{
			List<Node> children = node.childNodes();
			StringBuilder text = new StringBuilder();
			text.append("\""); //$NON-NLS-1$
			for (Node child : children)
			{
				text.append(getStringRepresentation(child));
			}
			text.append("\""); //$NON-NLS-1$
			return text.toString();
		}
		return node.toString();
	}
}
