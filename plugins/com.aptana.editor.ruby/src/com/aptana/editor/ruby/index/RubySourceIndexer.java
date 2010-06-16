package com.aptana.editor.ruby.index;

import java.util.Stack;

import com.aptana.editor.ruby.core.IRubyMethod.Visibility;
import com.aptana.editor.ruby.parsing.ISourceElementRequestor;
import com.aptana.index.core.Index;

public class RubySourceIndexer implements ISourceElementRequestor
{
	private static final String NAMESPACE_DELIMETER = "::"; //$NON-NLS-1$

	private Stack<TypeInfo> typeStack = new Stack<TypeInfo>();
	private Index index;
	private String documentPath;

	public RubySourceIndexer(Index index, String documentPath)
	{
		this.index = index;
		this.documentPath = documentPath;
	}

	private void addIndex(String category, String word)
	{
		index.addEntry(category, word, documentPath);
	}

	@Override
	public void exitType(int endOffset)
	{
		typeStack.pop();
	}

	@Override
	public void exitScript(int endOffset)
	{
		typeStack.clear();
	}

	@Override
	public void exitMethod(int endOffset)
	{
	}

	@Override
	public void exitField(int endOffset)
	{
	}

	@Override
	public void exitConstructor(int endOffset)
	{
	}

	@Override
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

		addIndex(
				IRubyIndexConstants.SUPER_REF,
				createSuperTypeReferenceKey(isModule, simpleName, enclosingTypes, IRubyIndexConstants.CLASS_SUFFIX,
						superclass, IRubyIndexConstants.CLASS_SUFFIX));
		if (modules != null)
		{
			for (String module : modules)
			{
				addTypeReference(module);
				addIncludedModuleReference(isModule, simpleName, enclosingTypes, module);
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
		int typeNameLength = typeName == null ? 0 : typeName.length();
		int enclosingNamesLength = 0;
		if (enclosingTypeNames != null)
		{
			for (int i = 0, length = enclosingTypeNames.length; i < length;)
			{
				enclosingNamesLength += enclosingTypeNames[i].length();
				if (++i < length)
					enclosingNamesLength += 2; // for the "::" separator
			}
		}

		int resultLength = typeNameLength + enclosingNamesLength + 5;
		if (secondary)
			resultLength += 2;
		char[] result = new char[resultLength];
		int pos = 0;
		if (typeNameLength > 0)
		{
			System.arraycopy(typeName.toCharArray(), 0, result, pos, typeNameLength);
			pos += typeNameLength;
		}
		result[pos++] = IRubyIndexConstants.SEPARATOR;
		if (enclosingTypeNames != null && enclosingNamesLength > 0)
		{
			for (int i = 0, length = enclosingTypeNames.length; i < length;)
			{
				String enclosingName = enclosingTypeNames[i];
				int itsLength = enclosingName.length();
				System.arraycopy(enclosingName.toCharArray(), 0, result, pos, itsLength);
				pos += itsLength;
				if (++i < length)
				{
					result[pos++] = ':';
					result[pos++] = ':';
				}
			}
		}
		result[pos++] = IRubyIndexConstants.SEPARATOR;
		if (isModule)
		{
			result[pos++] = IRubyIndexConstants.MODULE_SUFFIX;
		}
		else
		{
			result[pos++] = IRubyIndexConstants.CLASS_SUFFIX;
		}
		if (secondary)
		{
			result[++pos] = IRubyIndexConstants.SEPARATOR;
			result[++pos] = 'S';
		}
		return new String(result);
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

	@Override
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
		addIndex(IRubyIndexConstants.FIELD_DECL, field.name);
	}

	public void enterMethod(MethodInfo method)
	{
		addIndex(IRubyIndexConstants.METHOD_DECL, createMethodKey(method.name, method.parameterNames.length));
	}

	@Override
	public void acceptYield(String name)
	{
	}

	@Override
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

	@Override
	public void acceptModuleFunction(String function)
	{
	}

	@Override
	public void acceptMixin(String moduleName)
	{
		addIndex(IRubyIndexConstants.REF, getSimpleName(moduleName));

		TypeInfo info = typeStack.peek();
		String[] enclosingTypes = getEnclosingTypeNames(info.name);
		addIncludedModuleReference(info.isModule, getSimpleName(info.name), enclosingTypes, moduleName);
	}

	private void addIncludedModuleReference(boolean isModule, String simpleName, String[] enclosingTypes,
			String moduleName)
	{
		addIndex(
				IRubyIndexConstants.SUPER_REF,
				createSuperTypeReferenceKey(isModule, simpleName, enclosingTypes, IRubyIndexConstants.CLASS_SUFFIX,
						moduleName, IRubyIndexConstants.MODULE_SUFFIX));
	}

	private String createSuperTypeReferenceKey(boolean isModule, String typeName, String[] enclosingTypeNames,
			char classOrModule, String superTypeName, char superClassOrModule)
	{
		if (superTypeName == null)
			superTypeName = IRubyIndexConstants.OBJECT;
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
		String enclosingTypeName = join(enclosingTypeNames, NAMESPACE_DELIMETER);

		// superSimpleName / superQualification / simpleName / enclosingTypeName /
		// superClassOrModule classOrModule modifiers
		int superLength = superSimpleName == null ? 0 : superSimpleName.length();
		int superQLength = superQualification == null ? 0 : superQualification.length;
		int simpleLength = simpleName == null ? 0 : simpleName.length();
		int enclosingLength = enclosingTypeName == null ? 0 : enclosingTypeName.length();
		char[] result = new char[superLength + superQLength + simpleLength + enclosingLength + 8];
		int pos = 0;
		if (superLength > 0)
		{
			System.arraycopy(superSimpleName.toCharArray(), 0, result, pos, superLength);
			pos += superLength;
		}
		result[pos++] = IRubyIndexConstants.SEPARATOR;
		if (superQLength > 0)
		{
			System.arraycopy(superQualification, 0, result, pos, superQLength);
			pos += superQLength;
		}
		result[pos++] = IRubyIndexConstants.SEPARATOR;
		if (simpleLength > 0)
		{
			System.arraycopy(simpleName.toCharArray(), 0, result, pos, simpleLength);
			pos += simpleLength;
		}
		result[pos++] = IRubyIndexConstants.SEPARATOR;
		if (enclosingLength > 0)
		{
			System.arraycopy(enclosingTypeName.toCharArray(), 0, result, pos, enclosingLength);
			pos += enclosingLength;
		}

		result[pos++] = IRubyIndexConstants.SEPARATOR;
		result[pos++] = superClassOrModule;
		result[pos++] = classOrModule;
		int modifiers = 0;
		if (isModule)
		{
			modifiers = 1;
		}
		result[pos] = (char) modifiers;
		return new String(result);
	}

	private String join(String[] enclosingTypeNames, String string)
	{
		StringBuilder builder = new StringBuilder();
		for (String part : enclosingTypeNames)
		{
			builder.append(part).append(string);
		}
		if (builder.length() > 0)
		{
			builder.delete(builder.length() - string.length(), builder.length());
		}
		return builder.toString();
	}

	private String getSimpleName(String name)
	{
		return lastSegment(name, NAMESPACE_DELIMETER);
	}

	@Override
	public void acceptMethodVisibilityChange(String methodName, Visibility visibility)
	{
	}

	@Override
	public void acceptMethodReference(String name, int argCount, int offset)
	{
		addIndex(IRubyIndexConstants.METHOD_REF, createMethodKey(name, argCount));
	}

	private String createMethodKey(String name, int argCount)
	{
		return name + IRubyIndexConstants.SEPARATOR + String.valueOf(argCount);
	}

	@Override
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

	@Override
	public void acceptFieldReference(String name, int offset)
	{
		addIndex(IRubyIndexConstants.REF, name);
	}

	@Override
	public void acceptConstructorReference(String name, int argCount, int offset)
	{
		String simpleTypeName = getSimpleName(name);
		addIndex(IRubyIndexConstants.REF, simpleTypeName);
		addIndex(IRubyIndexConstants.CONSTRUCTOR_REF, createMethodKey(simpleTypeName, argCount));
	}

	@Override
	public void acceptBlock(int startOffset, int endOffset)
	{
	}
}
