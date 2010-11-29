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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.aptana.editor.ruby.core.IImportContainer;
import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyMethod;
import com.aptana.editor.ruby.core.IRubyMethod.Visibility;
import com.aptana.editor.ruby.parsing.ast.RubyBlock;
import com.aptana.editor.ruby.parsing.ast.RubyClassVariable;
import com.aptana.editor.ruby.parsing.ast.RubyConstant;
import com.aptana.editor.ruby.parsing.ast.RubyDynamicVariable;
import com.aptana.editor.ruby.parsing.ast.RubyElement;
import com.aptana.editor.ruby.parsing.ast.RubyField;
import com.aptana.editor.ruby.parsing.ast.RubyGlobal;
import com.aptana.editor.ruby.parsing.ast.RubyImport;
import com.aptana.editor.ruby.parsing.ast.RubyImportContainer;
import com.aptana.editor.ruby.parsing.ast.RubyInstanceVariable;
import com.aptana.editor.ruby.parsing.ast.RubyLocalVariable;
import com.aptana.editor.ruby.parsing.ast.RubyMethod;
import com.aptana.editor.ruby.parsing.ast.RubyModule;
import com.aptana.editor.ruby.parsing.ast.RubyScript;
import com.aptana.editor.ruby.parsing.ast.RubyType;
import com.aptana.parsing.ast.IParseNode;

/**
 * @author Chris Williams
 * @author Michael Xia
 */
public class RubyStructureBuilder implements ISourceElementRequestor
{

	private RubyScript script;
	private Stack<RubyElement> modelStack;

	public RubyStructureBuilder(RubyScript script)
	{
		this.script = script;
		modelStack = new Stack<RubyElement>();
		modelStack.push(script);
	}

	public void enterBlock(int startOffset, int endOffset)
	{
		RubyElement parent = modelStack.peek();
		RubyBlock block = new RubyBlock(startOffset, endOffset);

		parent.addChild(block);
		modelStack.push(block);
	}

	public void acceptConstructorReference(String name, int argCount, int offset)
	{
	}

	public void acceptFieldReference(String name, int offset)
	{
	}

	public void acceptImport(String value, int startOffset, int endOffset)
	{
		IImportContainer importContainer = script.getImportContainer();
		if (importContainer instanceof RubyImportContainer)
		{
			((RubyImportContainer) importContainer).addChild(new RubyImport(value, startOffset, endOffset));
		}
	}

	public void acceptMethodReference(String name, int argCount, int offset)
	{
	}

	public void acceptMethodVisibilityChange(String methodName, Visibility visibility)
	{
		RubyElement element = getCurrentType();
		if (!(element instanceof RubyType))
		{
			return;
		}

		RubyType parentType = (RubyType) element;
		IRubyMethod[] methods = parentType.getMethods();
		for (IRubyMethod method : methods)
		{
			if (!method.getName().equals(methodName))
			{
				continue;
			}
			if (method instanceof RubyMethod)
			{
				((RubyMethod) method).setVisibility(visibility);
			}
		}
	}

	public void acceptMixin(String string)
	{
		// pushes mixins into parent type, if available
		RubyElement element = getCurrentType();
		if (!(element instanceof RubyType))
		{
			return;
		}

		RubyType parentType = (RubyType) element;
		List<String> moduleNames = new LinkedList<String>();
		moduleNames.addAll(Arrays.asList(parentType.getIncludedModuleNames()));
		moduleNames.add(string);
		// applies included module names back to the parent type
		parentType.setIncludedModuleNames(moduleNames.toArray(new String[moduleNames.size()]));
	}

	public void acceptModuleFunction(String function)
	{
		RubyElement element = getCurrentType();
		if (!(element instanceof RubyType))
		{
			return;
		}

		RubyType parentType = (RubyType) element;
		IRubyMethod[] methods = parentType.getMethods();
		for (IRubyMethod method : methods)
		{
			if (!method.getName().equals(function))
			{
				continue;
			}
			if (method instanceof RubyMethod)
			{
				((RubyMethod) method).setIsSingleton(true);
			}
		}
	}

	public void acceptTypeReference(String name, int startOffset, int endOffset)
	{
	}

	public void acceptYield(String name)
	{
		if (!modelStack.isEmpty())
		{
			RubyElement element = modelStack.peek();
			if (element instanceof RubyMethod)
			{
				((RubyMethod) element).addBlockVar(name);
			}
		}
	}

	public void enterConstructor(MethodInfo constructor)
	{
		enterMethod(constructor);
	}

	public void enterField(FieldInfo fieldInfo)
	{
		RubyField handle;
		RubyElement parent = getCurrentType();
		if (fieldInfo.name.startsWith("@@")) //$NON-NLS-1$
		{
			handle = new RubyClassVariable(fieldInfo.name, fieldInfo.declarationStart, fieldInfo.nameSourceStart,
					fieldInfo.nameSourceEnd);
		}
		else if (fieldInfo.name.startsWith("@")) //$NON-NLS-1$
		{
			handle = new RubyInstanceVariable(fieldInfo.name, fieldInfo.declarationStart, fieldInfo.nameSourceStart,
					fieldInfo.nameSourceEnd);
		}
		else if (fieldInfo.name.startsWith("$")) //$NON-NLS-1$
		{
			parent = script;
			handle = new RubyGlobal(fieldInfo.name, fieldInfo.declarationStart, fieldInfo.nameSourceStart,
					fieldInfo.nameSourceEnd);
		}
		else if (Character.isUpperCase(fieldInfo.name.charAt(0)))
		{
			handle = new RubyConstant(fieldInfo.name, fieldInfo.declarationStart, fieldInfo.nameSourceStart,
					fieldInfo.nameSourceEnd);
		}
		else
		{
			parent = modelStack.peek();
			if (fieldInfo.isDynamic)
			{
				handle = new RubyDynamicVariable(fieldInfo.name, fieldInfo.declarationStart, fieldInfo.nameSourceStart,
						fieldInfo.nameSourceEnd);
			}
			else
			{
				handle = new RubyLocalVariable(fieldInfo.name, fieldInfo.declarationStart, fieldInfo.nameSourceStart,
						fieldInfo.nameSourceEnd);
			}
		}
		// for field, checks if it has been stored in the parent once
		if (!hasChild(parent, handle))
		{
			parent.addChild(handle);
		}
		modelStack.push(handle);
	}

	public void enterMethod(MethodInfo methodInfo)
	{
		RubyMethod method = new RubyMethod(methodInfo.name, methodInfo.parameterNames, methodInfo.declarationStart,
				methodInfo.nameSourceStart, methodInfo.nameSourceEnd);
		method.setVisibility(methodInfo.visibility);
		method.setIsSingleton(methodInfo.isClassLevel);
		getCurrentType().addChild(method);
		modelStack.push(method);
	}

	public void enterScript()
	{
	}

	public void enterType(TypeInfo typeInfo)
	{
		RubyType handle;
		if (typeInfo.isModule)
		{
			handle = new RubyModule(typeInfo.name, typeInfo.declarationStart, typeInfo.nameSourceStart,
					typeInfo.nameSourceEnd);
		}
		else
		{
			handle = new RubyType(typeInfo.name, typeInfo.declarationStart, typeInfo.nameSourceStart,
					typeInfo.nameSourceEnd);
		}
		handle.setSuperclassName(typeInfo.superclass);
		handle.setIncludedModuleNames(typeInfo.modules);

		RubyElement parent = modelStack.peek();
		RubyType existing = (RubyType) findChild(parent, IRubyElement.TYPE, typeInfo.name);
		if (existing != null)
		{
			handle.incrementOccurrence();
		}
		parent.addChild(handle);
		modelStack.push(handle);
	}

	public void exitConstructor(int endOffset)
	{
		exitMethod(endOffset);
	}

	public void exitField(int endOffset)
	{
		RubyElement element = modelStack.pop();
		element.setLocation(element.getStartingOffset(), endOffset + 1);
	}

	public void exitMethod(int endOffset)
	{
		RubyElement element = modelStack.pop();
		element.setLocation(element.getStartingOffset(), endOffset + 1);
	}

	public void exitScript(int endOffset)
	{
		RubyElement element = modelStack.pop();
		element.setLocation(element.getStartingOffset(), endOffset + 1);
	}

	public void exitType(int endOffset)
	{
		RubyElement element = modelStack.pop();
		element.setLocation(element.getStartingOffset(), endOffset + 1);
	}

	public void exitBlock(int endOffset)
	{
		RubyElement element = modelStack.pop();
		element.setLocation(element.getStartingOffset(), endOffset);
	}

	private static IRubyElement findChild(RubyElement parent, int type, String name)
	{
		IRubyElement[] elements = parent.getChildrenOfType(type);
		for (IRubyElement element : elements)
		{
			if (element.getName().equals(name))
			{
				return element;
			}
		}
		return null;
	}

	private RubyElement getCurrentType()
	{
		List<RubyElement> extras = new ArrayList<RubyElement>();
		RubyElement element = null;
		if (!modelStack.isEmpty())
		{
			element = modelStack.peek();
			while (!(element instanceof RubyType))
			{
				extras.add(modelStack.pop());

				if (modelStack.isEmpty())
				{
					break;
				}
				element = modelStack.peek();
			}
		}

		// needs to reverse the extras list before pushing the elements back onto the stack
		Collections.reverse(extras);
		for (RubyElement extra : extras)
		{
			modelStack.push(extra);
		}
		if (element == null)
		{
			return script;
		}
		return element;
	}

	private static boolean hasChild(RubyElement parent, IRubyElement child)
	{
		IParseNode[] nodes = parent.getChildren();
		IRubyElement element;
		for (IParseNode node : nodes)
		{
			element = (IRubyElement) node;
			if (element.getName().equals(child.getName()) && element.getNodeType() == child.getNodeType())
			{
				return true;
			}
		}
		return false;
	}
}
