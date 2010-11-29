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
package com.aptana.editor.ruby.parsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jruby.runtime.Visibility;
import org.jrubyparser.ast.AliasNode;
import org.jrubyparser.ast.ArgsNode;
import org.jrubyparser.ast.ArgumentNode;
import org.jrubyparser.ast.ArrayNode;
import org.jrubyparser.ast.CallNode;
import org.jrubyparser.ast.ClassNode;
import org.jrubyparser.ast.ClassVarAsgnNode;
import org.jrubyparser.ast.ClassVarDeclNode;
import org.jrubyparser.ast.ClassVarNode;
import org.jrubyparser.ast.Colon2Node;
import org.jrubyparser.ast.ConstDeclNode;
import org.jrubyparser.ast.ConstNode;
import org.jrubyparser.ast.DAsgnNode;
import org.jrubyparser.ast.DStrNode;
import org.jrubyparser.ast.DefnNode;
import org.jrubyparser.ast.DefsNode;
import org.jrubyparser.ast.FCallNode;
import org.jrubyparser.ast.GlobalAsgnNode;
import org.jrubyparser.ast.GlobalVarNode;
import org.jrubyparser.ast.InstAsgnNode;
import org.jrubyparser.ast.InstVarNode;
import org.jrubyparser.ast.IterNode;
import org.jrubyparser.ast.ListNode;
import org.jrubyparser.ast.LocalAsgnNode;
import org.jrubyparser.ast.LocalVarNode;
import org.jrubyparser.ast.MethodDefNode;
import org.jrubyparser.ast.ModuleNode;
import org.jrubyparser.ast.Node;
import org.jrubyparser.ast.RootNode;
import org.jrubyparser.ast.SClassNode;
import org.jrubyparser.ast.SelfNode;
import org.jrubyparser.ast.SplatNode;
import org.jrubyparser.ast.StrNode;
import org.jrubyparser.ast.UnnamedRestArgNode;
import org.jrubyparser.ast.VCallNode;
import org.jrubyparser.ast.YieldNode;

import com.aptana.editor.ruby.core.IRubyMethod;
import com.aptana.editor.ruby.parsing.ISourceElementRequestor.FieldInfo;
import com.aptana.editor.ruby.parsing.ISourceElementRequestor.MethodInfo;
import com.aptana.editor.ruby.parsing.ISourceElementRequestor.TypeInfo;
import com.aptana.editor.ruby.parsing.ast.ASTUtils;
import com.aptana.editor.ruby.parsing.ast.InOrderVisitor;

/**
 * @author Chris Williams
 * @author Michael Xia
 */
public class SourceElementVisitor extends InOrderVisitor
{

	private static final String OBJECT = "Object"; //$NON-NLS-1$
	private static final String CONSTRUCTOR_NAME = "initialize"; //$NON-NLS-1$
	private static final String MODULE = "Module"; //$NON-NLS-1$
	private static final String REQUIRE = "require"; //$NON-NLS-1$
	private static final String LOAD = "load"; //$NON-NLS-1$
	private static final String INCLUDE = "include"; //$NON-NLS-1$
	private static final String PUBLIC = "public"; //$NON-NLS-1$
	private static final String PROTECTED = "protected"; //$NON-NLS-1$
	private static final String PRIVATE = "private"; //$NON-NLS-1$
	private static final String MODULE_FUNCTION = "module_function"; //$NON-NLS-1$
	private static final String ALIAS = "alias :"; //$NON-NLS-1$
	private static final String ALIAS_METHOD = "alias_method"; //$NON-NLS-1$
	private static final String ATTR = "attr"; //$NON-NLS-1$
	private static final String ATTR_ACCESSOR = "attr_accessor"; //$NON-NLS-1$
	private static final String ATTR_READER = "attr_reader"; //$NON-NLS-1$
	private static final String ATTR_WRITER = "attr_writer"; //$NON-NLS-1$
	private static final String CLASS_EVAL = "class_eval"; //$NON-NLS-1$
	private static final String NAMESPACE_DELIMETER = "::"; //$NON-NLS-1$

	private ISourceElementRequestor requestor;
	private List<Visibility> visibilities;

	private String typeName;
	private boolean inSingletonClass;
	private boolean inModuleFunction;

	/**
	 * Constructor.
	 * 
	 * @param requestor
	 *            the {@link ISourceElementRequestor} that wants to be notified of the source structure
	 */
	public SourceElementVisitor(ISourceElementRequestor requestor)
	{
		this.requestor = requestor;
		visibilities = new ArrayList<Visibility>();
	}

	@Override
	public Object visitAliasNode(AliasNode iVisited)
	{
		String name = iVisited.getNewName();
		int nameStart = iVisited.getPosition().getStartOffset() + ALIAS.length() - 1;
		addAliasMethod(name, iVisited.getPosition().getStartOffset(), iVisited.getPosition().getEndOffset(), nameStart);

		return super.visitAliasNode(iVisited);
	}

	@Override
	public Object visitArgsNode(ArgsNode iVisited)
	{
		ListNode args = iVisited.getPre();
		if (args != null)
		{
			Node arg;
			int size = args.size();
			for (int i = 0; i < size; ++i)
			{
				arg = args.get(i);

				requestor.enterField(createFieldInfo(arg));
				requestor.exitField(getFieldEndOffset(arg));
			}
		}
		ArgumentNode restArg = iVisited.getRest();
		if (restArg != null && !(restArg instanceof UnnamedRestArgNode))
		{
			FieldInfo field = createFieldInfo(restArg);
			// account for the leading "*"
			field.declarationStart += 1;
			field.nameSourceStart += 1;
			field.nameSourceEnd += 1;
			requestor.enterField(field);
			requestor.exitField(getFieldEndOffset(restArg) + 1);
		}

		return super.visitArgsNode(iVisited);
	}

	@Override
	public Object visitCallNode(CallNode iVisited)
	{
		List<String> arguments = ASTUtils.getArgumentsFromFunctionCall(iVisited);
		String name = iVisited.getName();
		if (name.equals(PUBLIC))
		{
			for (String methodName : arguments)
			{
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PUBLIC));
			}
		}
		else if (name.equals(PRIVATE))
		{
			for (String methodName : arguments)
			{
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PRIVATE));
			}
		}
		else if (name.equals(PROTECTED))
		{
			for (String methodName : arguments)
			{
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PROTECTED));
			}
		}
		else if (name.equals(MODULE_FUNCTION))
		{
			for (String methodName : arguments)
			{
				requestor.acceptModuleFunction(methodName);
			}
		}
		else if (name.equals(CLASS_EVAL))
		{
			Node receiver = iVisited.getReceiverNode();
			if (receiver instanceof ConstNode || receiver instanceof Colon2Node)
			{
				String receiverName = null;
				if (receiver instanceof Colon2Node)
				{
					receiverName = ASTUtils.getFullyQualifiedName((Colon2Node) receiver);
				}
				else
				{
					receiverName = ASTUtils.getName(receiver);
				}
				requestor.acceptMethodReference(name, arguments.size(), iVisited.getPosition().getStartOffset());

				pushVisibility(Visibility.PUBLIC);

				TypeInfo typeInfo = new TypeInfo();
				typeInfo.name = receiverName;
				typeInfo.declarationStart = iVisited.getPosition().getStartOffset();
				typeInfo.nameSourceStart = receiver.getPosition().getStartOffset();
				typeInfo.nameSourceEnd = receiver.getPosition().getEndOffset() - 1;
				typeInfo.modules = new String[0];
				requestor.enterType(typeInfo);

				Object ins = super.visitCallNode(iVisited);

				popVisibility();
				requestor.exitType(iVisited.getPosition().getEndOffset() - 2);
				return ins;
			}
		}
		requestor.acceptMethodReference(name, arguments.size(), iVisited.getPosition().getStartOffset());

		return super.visitCallNode(iVisited);
	}

	@Override
	public Object visitClassNode(ClassNode iVisited)
	{
		// This resets the visibility when opening or declaring a class to
		// public
		pushVisibility(Visibility.PUBLIC);

		TypeInfo typeInfo = createTypeInfo(iVisited.getCPath());
		typeInfo.declarationStart = iVisited.getPosition().getStartOffset();
		if (!typeInfo.name.equals(OBJECT))
		{
			Node superNode = iVisited.getSuperNode();
			if (superNode == null)
			{
				typeInfo.superclass = OBJECT;
			}
			else
			{
				typeInfo.superclass = ASTUtils.getFullyQualifiedName(superNode);
			}
		}
		typeName = typeInfo.name;
		requestor.enterType(typeInfo);

		Object ins = super.visitClassNode(iVisited);

		popVisibility();
		requestor.exitType(iVisited.getPosition().getEndOffset() - 2);
		return ins;
	}

	@Override
	public Object visitClassVarAsgnNode(ClassVarAsgnNode iVisited)
	{
		FieldInfo field = createFieldInfo(iVisited);
		requestor.enterField(field);
		requestor.exitField(getFieldEndOffset(iVisited));

		return super.visitClassVarAsgnNode(iVisited);
	}

	@Override
	public Object visitClassVarDeclNode(ClassVarDeclNode iVisited)
	{
		FieldInfo field = createFieldInfo(iVisited);
		requestor.enterField(field);
		requestor.exitField(getFieldEndOffset(iVisited));

		return super.visitClassVarDeclNode(iVisited);
	}

	@Override
	public Object visitClassVarNode(ClassVarNode iVisited)
	{
		requestor.acceptFieldReference(iVisited.getName(), iVisited.getPosition().getStartOffset());

		return super.visitClassVarNode(iVisited);
	}

	@Override
	public Object visitConstDeclNode(ConstDeclNode iVisited)
	{
		FieldInfo field = createFieldInfo(iVisited);
		requestor.enterField(field);
		requestor.exitField(getFieldEndOffset(iVisited));

		return super.visitConstDeclNode(iVisited);
	}

	@Override
	public Object visitConstNode(ConstNode iVisited)
	{
		requestor.acceptTypeReference(iVisited.getName(), iVisited.getPosition().getStartOffset(), iVisited
				.getPosition().getEndOffset());

		return super.visitConstNode(iVisited);
	}

	@Override
	public Object visitDAsgnNode(DAsgnNode iVisited)
	{
		FieldInfo field = createFieldInfo(iVisited);
		field.isDynamic = true;
		requestor.enterField(field);
		requestor.exitField(getFieldEndOffset(iVisited));

		return super.visitDAsgnNode(iVisited);
	}

	@Override
	public Object visitDefnNode(DefnNode iVisited)
	{
		Visibility visibility = getCurrentVisibility();
		MethodInfo methodInfo = createMethodInfo(iVisited);
		if (methodInfo.name.equals(CONSTRUCTOR_NAME))
		{
			visibility = Visibility.PROTECTED;
			methodInfo.isConstructor = true;
		}
		methodInfo.isClassLevel = inSingletonClass || inModuleFunction;
		methodInfo.visibility = convertVisibility(visibility);
		if (methodInfo.isConstructor)
		{
			requestor.enterConstructor(methodInfo);
		}
		else
		{
			requestor.enterMethod(methodInfo);
		}

		Object ins = super.visitDefnNode(iVisited);

		int end = iVisited.getPosition().getEndOffset() - 2;
		if (methodInfo.isConstructor)
		{
			requestor.exitConstructor(end);
		}
		else
		{
			requestor.exitMethod(end);
		}
		return ins;
	}

	@Override
	public Object visitDefsNode(DefsNode iVisited)
	{
		MethodInfo methodInfo = createMethodInfo(iVisited);
		methodInfo.isClassLevel = true;
		methodInfo.visibility = convertVisibility(getCurrentVisibility());
		requestor.enterMethod(methodInfo);

		Object ins = super.visitDefsNode(iVisited);

		requestor.exitMethod(iVisited.getPosition().getEndOffset() - 2);
		return ins;
	}

	@Override
	public Object visitFCallNode(FCallNode iVisited)
	{
		List<String> arguments = ASTUtils.getArgumentsFromFunctionCall(iVisited);
		String name = iVisited.getName();
		if (name.equals(REQUIRE) || name.equals(LOAD))
		{
			addImport(iVisited);
		}
		else if (name.equals(INCLUDE))
		{
			// Collect included mixins
			includeModule(iVisited);
		}
		else if (name.equals(PUBLIC))
		{
			for (String methodName : arguments)
			{
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PUBLIC));
			}
		}
		else if (name.equals(PRIVATE))
		{
			for (String methodName : arguments)
			{
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PRIVATE));
			}
		}
		else if (name.equals(PROTECTED))
		{
			for (String methodName : arguments)
			{
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PROTECTED));
			}
		}
		else if (name.equals(MODULE_FUNCTION))
		{
			for (String methodName : arguments)
			{
				requestor.acceptModuleFunction(methodName);
			}
		}
		else if (name.equals(ALIAS_METHOD))
		{
			String newName = arguments.get(0).substring(1);
			int nameStart = iVisited.getPosition().getStartOffset() + name.length() + 2;
			addAliasMethod(newName, iVisited.getPosition().getStartOffset(), iVisited.getPosition().getEndOffset(),
					nameStart);
		}
		if (name.equals(ATTR) || name.equals(ATTR_ACCESSOR) || name.equals(ATTR_READER) || name.equals(ATTR_WRITER))
		{
			List<Node> nodes = ASTUtils.getArgumentNodesFromFunctionCall(iVisited);
			if (name.equals(ATTR))
			{
				addReadMethod(arguments.get(0), nodes.get(0));
				if (arguments.size() == 2 && arguments.get(1).equals("true")) //$NON-NLS-1$
				{
					Node node = nodes.get(0);
					int start = node.getPosition().getEndOffset() + 2;
					addWriteMethod(arguments.get(0), start, start + arguments.get(1).length() - 1);
				}
			}
			else if (name.equals(ATTR_ACCESSOR))
			{
				int size = arguments.size();
				for (int i = 0; i < size; ++i)
				{
					addReadMethod(arguments.get(i), nodes.get(i));
				}
				for (int i = 0; i < size; ++i)
				{
					addWriteMethod(arguments.get(i), nodes.get(i));
				}
			}
			else if (name.equals(ATTR_READER))
			{
				int size = arguments.size();
				for (int i = 0; i < size; ++i)
				{
					addReadMethod(arguments.get(i), nodes.get(i));
				}
			}
			else if (name.equals(ATTR_WRITER))
			{
				int size = arguments.size();
				for (int i = 0; i < size; ++i)
				{
					addWriteMethod(arguments.get(i), nodes.get(i));
				}
			}

			FieldInfo field;
			Node node;
			int size = nodes.size();
			for (int i = 0; i < size; ++i)
			{
				node = nodes.get(i);

				field = new FieldInfo();
				field.declarationStart = node.getPosition().getStartOffset() + 1;
				String argName = arguments.get(i);
				if (argName.startsWith(":")) //$NON-NLS-1$
				{
					argName = argName.substring(1);
				}
				field.name = "@" + argName; //$NON-NLS-1$
				field.nameSourceStart = node.getPosition().getStartOffset() + 1;
				field.nameSourceEnd = node.getPosition().getEndOffset() - 1;
				requestor.enterField(field);
				requestor.exitField(node.getPosition().getEndOffset() - 1);
			}
		}
		requestor.acceptMethodReference(name, arguments.size(), iVisited.getPosition().getStartOffset());

		return super.visitFCallNode(iVisited);
	}

	@Override
	public Object visitGlobalAsgnNode(GlobalAsgnNode iVisited)
	{
		FieldInfo field = createFieldInfo(iVisited);
		requestor.enterField(field);
		requestor.exitField(getFieldEndOffset(iVisited));

		return super.visitGlobalAsgnNode(iVisited);
	}

	@Override
	public Object visitGlobalVarNode(GlobalVarNode iVisited)
	{
		requestor.acceptFieldReference(iVisited.getName(), iVisited.getPosition().getStartOffset());

		return super.visitGlobalVarNode(iVisited);
	}

	@Override
	public Object visitInstAsgnNode(InstAsgnNode iVisited)
	{
		FieldInfo field = createFieldInfo(iVisited);
		requestor.enterField(field);
		requestor.exitField(getFieldEndOffset(iVisited));

		return super.visitInstAsgnNode(iVisited);
	}

	@Override
	public Object visitInstVarNode(InstVarNode iVisited)
	{
		requestor.acceptFieldReference(iVisited.getName(), iVisited.getPosition().getStartOffset());

		return super.visitInstVarNode(iVisited);
	}

	@Override
	public Object visitIterNode(IterNode iVisited)
	{
		requestor.enterBlock(iVisited.getPosition().getStartOffset(), iVisited.getPosition().getEndOffset() - 1);

		Object ins = super.visitIterNode(iVisited);
		
		requestor.exitBlock(iVisited.getPosition().getEndOffset() - 1);
		return ins;
	}

	@Override
	public Object visitModuleNode(ModuleNode iVisited)
	{
		pushVisibility(Visibility.PUBLIC);

		TypeInfo typeInfo = createTypeInfo(iVisited.getCPath());
		typeInfo.declarationStart = iVisited.getPosition().getStartOffset();
		typeInfo.superclass = MODULE;
		typeInfo.isModule = true;
		typeName = typeInfo.name;
		requestor.enterType(typeInfo);

		Object ins = super.visitModuleNode(iVisited);

		popVisibility();
		requestor.exitType(iVisited.getPosition().getEndOffset() - 2);
		inModuleFunction = false;
		return ins;
	}

	@Override
	public Object visitLocalAsgnNode(LocalAsgnNode iVisited)
	{
		FieldInfo field = createFieldInfo(iVisited);
		requestor.enterField(field);
		requestor.exitField(getFieldEndOffset(iVisited));

		return super.visitLocalAsgnNode(iVisited);
	}

	@Override
	public Object visitRootNode(RootNode iVisited)
	{
		requestor.enterScript();
		pushVisibility(Visibility.PUBLIC);

		Object ins = super.visitRootNode(iVisited);

		popVisibility();
		requestor.exitScript(iVisited.getPosition().getEndOffset());
		return ins;
	}

	@Override
	public Object visitSClassNode(SClassNode iVisited)
	{
		Node receiver = iVisited.getReceiverNode();
		if (receiver instanceof SelfNode)
		{
			inSingletonClass = true;
		}
		pushVisibility(Visibility.PUBLIC);

		Object ins = super.visitSClassNode(iVisited);

		popVisibility();
		if (receiver instanceof SelfNode)
		{
			inSingletonClass = false;
		}
		return ins;
	}

	@Override
	public Object visitVCallNode(VCallNode iVisited)
	{
		String functionName = iVisited.getName();
		if (functionName.equals(PUBLIC))
		{
			setVisibility(Visibility.PUBLIC);
		}
		else if (functionName.equals(PRIVATE))
		{
			setVisibility(Visibility.PRIVATE);
		}
		else if (functionName.equals(PROTECTED))
		{
			setVisibility(Visibility.PROTECTED);
		}
		else if (functionName.equals(MODULE_FUNCTION))
		{
			inModuleFunction = true;
		}
		requestor.acceptMethodReference(functionName, 0, iVisited.getPosition().getStartOffset());

		return super.visitVCallNode(iVisited);
	}

	@Override
	public Object visitYieldNode(YieldNode iVisited)
	{
		Node argsNode = iVisited.getArgsNode();
		if (argsNode instanceof LocalVarNode)
		{
			requestor.acceptYield(((LocalVarNode) argsNode).getName());
		}
		else if (argsNode instanceof SelfNode)
		{
			String name = null;
			if (typeName == null)
			{
				name = "var"; //$NON-NLS-1$
			}
			else
			{
				name = typeName.toLowerCase();
				if (name.indexOf(NAMESPACE_DELIMETER) > -1)
				{
					name = name.substring(name.lastIndexOf(NAMESPACE_DELIMETER) + 2);
				}
			}
			requestor.acceptYield(name);
		}

		return super.visitYieldNode(iVisited);
	}

	private void pushVisibility(Visibility visibility)
	{
		visibilities.add(visibility);
	}

	private void popVisibility()
	{
		visibilities.remove(visibilities.size() - 1);
	}

	private Visibility getCurrentVisibility()
	{
		return visibilities.get(visibilities.size() - 1);
	}

	private void setVisibility(Visibility visibility)
	{
		popVisibility();
		pushVisibility(visibility);
	}

	private void addImport(FCallNode iVisited)
	{
		ArrayNode node = (ArrayNode) iVisited.getArgsNode();
		String arg = getString(node);
		if (arg != null)
		{
			requestor.acceptImport(arg, iVisited.getPosition().getStartOffset(), iVisited.getPosition().getEndOffset());
		}
	}

	private void addAliasMethod(String name, int start, int end, int nameStart)
	{
		MethodInfo method = new MethodInfo();
		// TODO Use the visibility for the original method that this is aliasing?
		Visibility visibility = getCurrentVisibility();
		if (name.equals(CONSTRUCTOR_NAME))
		{
			visibility = Visibility.PROTECTED;
			method.isConstructor = true;
		}
		method.declarationStart = start;
		method.isClassLevel = inSingletonClass;
		method.name = name;
		method.visibility = convertVisibility(visibility);
		method.nameSourceStart = nameStart;
		method.nameSourceEnd = nameStart + name.length() - 1;
		// TODO Use the parameters of the original method
		method.parameterNames = new String[0];
		requestor.enterMethod(method);
		requestor.exitMethod(end);
	}

	private void addReadMethod(String argument, Node node)
	{
		if (argument.startsWith(":")) //$NON-NLS-1$
		{
			argument = argument.substring(1);
		}
		MethodInfo info = new MethodInfo();
		info.declarationStart = node.getPosition().getStartOffset();
		info.name = argument;
		info.nameSourceStart = node.getPosition().getStartOffset();
		info.nameSourceEnd = node.getPosition().getEndOffset() - 1;
		info.visibility = IRubyMethod.Visibility.PUBLIC;
		info.parameterNames = new String[0];
		requestor.enterMethod(info);
		requestor.exitMethod(node.getPosition().getEndOffset() - 1);
	}

	private void addWriteMethod(String argument, Node node)
	{
		addWriteMethod(argument, node.getPosition().getStartOffset(), node.getPosition().getEndOffset() - 1);
	}

	private void addWriteMethod(String argument, int start, int end)
	{
		if (argument.startsWith(":")) //$NON-NLS-1$
		{
			argument = argument.substring(1);
		}
		MethodInfo info = new MethodInfo();
		info.declarationStart = start;
		info.name = argument + "="; //$NON-NLS-1$
		info.nameSourceStart = start;
		info.nameSourceEnd = end;
		info.visibility = IRubyMethod.Visibility.PUBLIC;
		info.parameterNames = new String[] { "new_value" }; //$NON-NLS-1$
		requestor.enterMethod(info);
		requestor.exitMethod(end);
	}

	private void includeModule(FCallNode iVisited)
	{
		List<String> mixins = new LinkedList<String>();

		Iterator<Node> iter = null;
		Node argsNode = iVisited.getArgsNode();
		if (argsNode instanceof SplatNode)
		{
			iter = ((SplatNode) argsNode).childNodes().iterator();
		}
		else if (argsNode instanceof ArrayNode)
		{
			iter = ((ArrayNode) argsNode).childNodes().iterator();
		}
		if (iter != null)
		{
			Node node;
			while (iter.hasNext())
			{
				node = iter.next();
				if (node instanceof StrNode)
				{
					mixins.add(((StrNode) node).getValue());
				}
				else if (node instanceof ConstNode)
				{
					mixins.add(((ConstNode) node).getName());
				}
				else if (node instanceof Colon2Node)
				{
					mixins.add(ASTUtils.getFullyQualifiedName((Colon2Node) node));
				}
				else if (node instanceof DStrNode)
				{
					Node next = ((DStrNode) node).childNodes().iterator().next();
					if (next instanceof StrNode)
					{
						mixins.add(((StrNode) next).getValue());
					}
				}
			}
		}

		for (String string : mixins)
		{
			requestor.acceptMixin(string);
		}
	}

	private static FieldInfo createFieldInfo(Node iVisited)
	{
		FieldInfo field = new FieldInfo();
		field.name = ASTUtils.getName(iVisited);
		field.declarationStart = iVisited.getPosition().getStartOffset();
		field.nameSourceStart = iVisited.getPosition().getStartOffset();
		field.nameSourceEnd = iVisited.getPosition().getStartOffset() + field.name.length() - 1;
		return field;
	}

	private static TypeInfo createTypeInfo(Node iVisited)
	{
		TypeInfo typeInfo = new TypeInfo();
		typeInfo.name = ASTUtils.getFullyQualifiedName(iVisited);
		typeInfo.nameSourceStart = iVisited.getPosition().getStartOffset();
		typeInfo.nameSourceEnd = iVisited.getPosition().getEndOffset() - 1;
		typeInfo.modules = new String[0];
		return typeInfo;
	}

	private static MethodInfo createMethodInfo(MethodDefNode iVisited)
	{
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.declarationStart = iVisited.getPosition().getStartOffset();
		methodInfo.name = iVisited.getName();
		methodInfo.nameSourceStart = iVisited.getNameNode().getPosition().getStartOffset();
		methodInfo.nameSourceEnd = iVisited.getNameNode().getPosition().getEndOffset() - 1;
		methodInfo.parameterNames = ASTUtils.getArgs(iVisited.getArgsNode(), iVisited.getScope());
		return methodInfo;
	}

	private static int getFieldEndOffset(Node iVisited)
	{
		return iVisited.getPosition().getEndOffset() - 1;
	}

	private static String getString(ArrayNode node)
	{
		Node child = node.childNodes().iterator().next();
		if (child instanceof DStrNode)
		{
			DStrNode dstrNode = (DStrNode) child;
			child = dstrNode.childNodes().iterator().next();
		}
		if (child instanceof StrNode)
		{
			return ((StrNode) child).getValue().toString();
		}
		return null;
	}

	private static IRubyMethod.Visibility convertVisibility(Visibility visibility)
	{
		if (visibility == Visibility.PUBLIC)
		{
			return IRubyMethod.Visibility.PUBLIC;
		}
		if (visibility == Visibility.PROTECTED)
		{
			return IRubyMethod.Visibility.PROTECTED;
		}
		return IRubyMethod.Visibility.PRIVATE;
	}
}
