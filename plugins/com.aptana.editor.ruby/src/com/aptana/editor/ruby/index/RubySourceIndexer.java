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
package com.aptana.editor.ruby.index;

import java.net.URI;
import java.util.Stack;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.ruby.core.IRubyMethod.Visibility;
import com.aptana.editor.ruby.parsing.ISourceElementRequestor;
import com.aptana.index.core.Index;

public class RubySourceIndexer implements ISourceElementRequestor
{
	private static final String NAMESPACE_DELIMETER = "::"; //$NON-NLS-1$

	private Stack<TypeInfo> typeStack = new Stack<TypeInfo>();
	private Index index;
	private URI documentPath;

	public RubySourceIndexer(Index index, URI documentPath)
	{
		this.index = index;
		this.documentPath = documentPath;
	}

	private void addIndex(String category, String word)
	{
		index.addEntry(category, word, documentPath);
	}

	public void exitType(int endOffset)
	{
		typeStack.pop();
	}

	public void exitScript(int endOffset)
	{
		typeStack.clear();
	}

	public void exitMethod(int endOffset)
	{
	}

	public void exitField(int endOffset)
	{
	}

	public void exitConstructor(int endOffset)
	{
	}

	public void enterType(TypeInfo type)
	{
		String simpleName = getSimpleName(type.name);
		String[] enclosingTypes = getEnclosingTypeNames(type.name);
		addClassDeclaration(type.isModule, simpleName, enclosingTypes, type.superclass, type.modules, type.secondary);
		typeStack.push(type);
	}

	private void addClassDeclaration(boolean isModule, String simpleName, String[] enclosingTypes, String superclass,
			String[] modules, boolean secondary)
	{
		String indexKey = createTypeDeclarationKey(isModule, simpleName, enclosingTypes, secondary);
		addIndex(IRubyIndexConstants.TYPE_DECL, indexKey);

		if (superclass != null && !superclass.equals(IRubyIndexConstants.OBJECT))
		{
			addTypeReference(superclass);
		}

		if (!isModule)
		{
			// We know that both class and superclass must be classes because Modules can't have subclasses
			addIndex(
					IRubyIndexConstants.SUPER_REF,
					createSuperTypeReferenceKey(simpleName, enclosingTypes, IRubyIndexConstants.CLASS_SUFFIX,
							superclass, IRubyIndexConstants.CLASS_SUFFIX));
		}
		if (modules != null)
		{
			for (String module : modules)
			{
				addTypeReference(module);
				addIncludedModuleReference(simpleName, enclosingTypes, module);
			}
		}
	}

	/**
	 * Generates a key of the form: TypeName/namespace/(M|C)/S i.e. "Base/ActiveRecord/C"
	 * 
	 * @param isModule
	 * @param typeName
	 * @param packageName
	 * @param enclosingTypeNames
	 * @param secondary
	 * @return
	 */
	private String createTypeDeclarationKey(boolean isModule, String typeName, String[] enclosingTypeNames,
			boolean secondary)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(typeName);
		builder.append(IRubyIndexConstants.SEPARATOR);
		if (enclosingTypeNames != null && enclosingTypeNames.length > 0)
		{
			for (String enclosingName : enclosingTypeNames)
			{
				builder.append(enclosingName);
				builder.append(NAMESPACE_DELIMETER);
			}
			builder.delete(builder.length() - 2, builder.length());
		}
		builder.append(IRubyIndexConstants.SEPARATOR);

		builder.append(isModule ? IRubyIndexConstants.MODULE_SUFFIX : IRubyIndexConstants.CLASS_SUFFIX);

		if (secondary)
		{
			builder.append(IRubyIndexConstants.SEPARATOR);
			builder.append('S');
		}
		return builder.toString();
	}

	private String[] getEnclosingTypeNames(String typeName)
	{
		String[] parts = typeName.split(NAMESPACE_DELIMETER);

		String[] names = new String[typeStack.size() + parts.length - 1];
		int i = 0;
		for (TypeInfo info : typeStack)
		{
			names[i++] = info.name;
		}
		for (int j = 0; j < parts.length - 1; j++)
		{
			names[i++] = parts[j];
		}
		return names;
	}

	public void enterScript()
	{
	}

	public void enterConstructor(MethodInfo constructor)
	{
		addIndex(IRubyIndexConstants.CONSTRUCTOR_DECL,
				createMethodKey(getSimpleName(constructor.name), constructor.parameterNames.length));
	}

	public void enterField(FieldInfo field)
	{
		if (field == null || field.name == null || field.name.length() == 0)
		{
			return;
		}
		String category = IRubyIndexConstants.LOCAL_DECL;
		if (field.name.startsWith("@@")) //$NON-NLS-1$
		{
			category = IRubyIndexConstants.FIELD_DECL;
		}
		else if (field.name.startsWith("@")) //$NON-NLS-1$
		{
			category = IRubyIndexConstants.FIELD_DECL;
		}
		else if (field.name.startsWith("$")) //$NON-NLS-1$
		{
			category = IRubyIndexConstants.GLOBAL_DECL;
		}
		else if (Character.isUpperCase(field.name.charAt(0)))
		{
			category = IRubyIndexConstants.CONSTANT_DECL;
		}
		addIndex(category, field.name);
	}

	public void enterMethod(MethodInfo method)
	{
		addIndex(IRubyIndexConstants.METHOD_DECL, createMethodKey(method.name, method.parameterNames.length));
	}

	public void acceptYield(String name)
	{
	}

	public void acceptTypeReference(String name, int startOffset, int endOffset)
	{
		addTypeReference(name);
	}

	private void addTypeReference(String name)
	{
		addIndex(IRubyIndexConstants.REF, getSimpleName(name));
	}

	private String lastSegment(String name, String delimeter)
	{
		if (name == null)
		{
			return null;
		}
		int index = name.lastIndexOf(delimeter);
		if (index != -1)
		{
			return name.substring(index + delimeter.length());
		}
		return name;
	}

	public void acceptModuleFunction(String function)
	{
	}

	public void acceptMixin(String moduleName)
	{
		addIndex(IRubyIndexConstants.REF, getSimpleName(moduleName));

		if (typeStack != null && !typeStack.isEmpty())
		{
			TypeInfo info = typeStack.peek();
			String[] enclosingTypes = getEnclosingTypeNames(info.name);
			addIncludedModuleReference(getSimpleName(info.name), enclosingTypes, moduleName);
		}
	}

	private void addIncludedModuleReference(String simpleName, String[] enclosingTypes, String moduleName)
	{
		addIndex(
				IRubyIndexConstants.SUPER_REF,
				createSuperTypeReferenceKey(simpleName, enclosingTypes, IRubyIndexConstants.CLASS_SUFFIX, moduleName,
						IRubyIndexConstants.MODULE_SUFFIX));
	}

	/**
	 * SuperTypeName(Simple)/SuperTypeNamespace/SimpleName/EnclosingTypeName/SuperIsClassOrModule(M|C)
	 * isClassorModule(M|C)
	 * 
	 * @param typeName
	 * @param enclosingTypeNames
	 * @param classOrModule
	 * @param superTypeName
	 * @param superClassOrModule
	 * @return
	 */
	private String createSuperTypeReferenceKey(String typeName, String[] enclosingTypeNames, char classOrModule,
			String superTypeName, char superClassOrModule)
	{
		if (superTypeName == null)
		{
			superTypeName = IRubyIndexConstants.OBJECT;
		}
		String superSimpleName = lastSegment(superTypeName, NAMESPACE_DELIMETER);
		char[] superQualification = null;
		if (!superTypeName.equals(superSimpleName))
		{
			int length = superTypeName.length() - superSimpleName.length() - 1;
			superQualification = new char[length - 1];
			System.arraycopy(superTypeName.toCharArray(), 0, superQualification, 0, length - 1);
		}

		// if the supertype name contains a $, then split it into: source name and append the $
		// prefix to the qualification
		// e.g. p.A$B ---> p.A$ + B
		String superTypeSourceName = lastSegment(superSimpleName, NAMESPACE_DELIMETER);
		if (superSimpleName != null && !superSimpleName.equals(superTypeSourceName))
		{
			int start = superQualification == null ? 0 : superQualification.length + 1;
			int prefixLength = superSimpleName.length() - superTypeSourceName.length();
			char[] mangledQualification = new char[start + prefixLength];
			if (superQualification != null)
			{
				System.arraycopy(superQualification, 0, mangledQualification, 0, start - 1);
				mangledQualification[start - 1] = '.';
			}
			System.arraycopy(superSimpleName.toCharArray(), 0, mangledQualification, start, prefixLength);
			superQualification = mangledQualification;
			superSimpleName = superTypeSourceName;
		}

		String simpleName = lastSegment(typeName, NAMESPACE_DELIMETER);
		String enclosingTypeName = StringUtil.join(NAMESPACE_DELIMETER, enclosingTypeNames);

		StringBuilder builder = new StringBuilder();
		builder.append(superSimpleName);
		builder.append(IRubyIndexConstants.SEPARATOR);
		if (superQualification != null)
		{
			builder.append(superQualification);
		}
		builder.append(IRubyIndexConstants.SEPARATOR);
		builder.append(simpleName);
		builder.append(IRubyIndexConstants.SEPARATOR);
		builder.append(enclosingTypeName);
		builder.append(IRubyIndexConstants.SEPARATOR);
		builder.append(superClassOrModule);
		builder.append(classOrModule);

		return builder.toString();
	}

	private String getSimpleName(String name)
	{
		return lastSegment(name, NAMESPACE_DELIMETER);
	}

	public void acceptMethodVisibilityChange(String methodName, Visibility visibility)
	{
	}

	public void acceptMethodReference(String name, int argCount, int offset)
	{
		addIndex(IRubyIndexConstants.METHOD_REF, createMethodKey(name, argCount));
	}

	private String createMethodKey(String name, int argCount)
	{
		return name + IRubyIndexConstants.SEPARATOR + String.valueOf(argCount);
	}

	public void acceptImport(String value, int startOffset, int endOffset)
	{
		// FIXME This is really, really bad. requires are relative to loadpaths, which are dynamic.
		// IFile requireFile = file.getParent().getFile(new Path(value));
		// if (requireFile.exists())
		// {
		// addIndex(IRubyIndexConstants.REQUIRE, requireFile.getProjectRelativePath().toPortableString());
		// }
		addIndex(IRubyIndexConstants.REQUIRE, value);
	}

	public void acceptFieldReference(String name, int offset)
	{
		addIndex(IRubyIndexConstants.REF, name);
	}

	public void acceptConstructorReference(String name, int argCount, int offset)
	{
		String simpleTypeName = getSimpleName(name);
		addIndex(IRubyIndexConstants.REF, simpleTypeName);
		addIndex(IRubyIndexConstants.CONSTRUCTOR_REF, createMethodKey(simpleTypeName, argCount));
	}

	public void enterBlock(int startOffset, int endOffset)
	{
	}

	public void exitBlock(int endOffset)
	{
		// no-op
	}
}
