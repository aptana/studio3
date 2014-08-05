/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import com.aptana.core.IFilter;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.model.ClassElement;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.ParameterElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.ReturnTypeElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSAssignmentNode;
import com.aptana.js.core.parsing.ast.JSDeclarationNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetElementNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSNameValuePairNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSThisNode;
import com.aptana.js.internal.core.parsing.sdoc.model.DocumentationBlock;
import com.aptana.js.internal.core.parsing.sdoc.model.ExampleTag;
import com.aptana.js.internal.core.parsing.sdoc.model.ParamTag;
import com.aptana.js.internal.core.parsing.sdoc.model.ReturnTag;
import com.aptana.js.internal.core.parsing.sdoc.model.Tag;
import com.aptana.js.internal.core.parsing.sdoc.model.TagType;
import com.aptana.js.internal.core.parsing.sdoc.model.Type;
import com.aptana.js.internal.core.parsing.sdoc.model.TypeTag;
import com.aptana.parsing.ast.IParseNode;

public class JSTypeUtil
{
	/**
	 * try to do some validation on type name, because somehow we're getting very whacked-out type names in our index
	 * strings for some users. See https://jira.appcelerator.org/browse/APSTUD-7366
	 */
	private static final Pattern TYPE_NAME_PATTERN = Pattern.compile("[\\$a-zA-Z\\-_]+[\\.\\w\\$\\-/<>]*"); //$NON-NLS-1$

	private static final Set<String> FILTERED_TYPES;

	/**
	 * static initializer
	 */
	static
	{
		// @formatter:off
		FILTERED_TYPES = CollectionsUtil.newSet(
			JSTypeConstants.ARRAY_TYPE,
			JSTypeConstants.BOOLEAN_TYPE,
			JSTypeConstants.FUNCTION_TYPE,
			JSTypeConstants.NUMBER_TYPE,
			JSTypeConstants.OBJECT_TYPE,
			JSTypeConstants.REG_EXP_TYPE,
			JSTypeConstants.STRING_TYPE,
			JSTypeConstants.UNDEFINED_TYPE,
			JSTypeConstants.VOID_TYPE,
			JSTypeConstants.WINDOW_TYPE,
			JSTypeConstants.WINDOW_PROPERTY
		);
		// @formatter:on
	}

	/**
	 * applyDocumentation
	 * 
	 * @param function
	 * @param block
	 */
	public static void applyDocumentation(FunctionElement function, JSNode node, DocumentationBlock block)
	{
		if (block != null)
		{
			// apply description
			function.setDescription(block.getText());

			// apply parameters
			if (block.hasTag(TagType.PARAM))
			{
				for (Tag tag : block.getTags(TagType.PARAM))
				{
					ParamTag paramTag = (ParamTag) tag;
					ParameterElement parameter = new ParameterElement();

					parameter.setName(paramTag.getName());
					parameter.setDescription(paramTag.getText());
					parameter.setUsage(paramTag.getUsage().getName());

					for (Type type : paramTag.getTypes())
					{
						parameter.addType(type.toSource());
					}

					function.addParameter(parameter);
				}
			}
			else
			{
				if (node instanceof JSFunctionNode)
				{
					JSFunctionNode functionNode = (JSFunctionNode) node;

					for (IParseNode parameterNode : functionNode.getParameters())
					{
						ParameterElement parameterElement = new ParameterElement();

						parameterElement.setName(parameterNode.getText());
						parameterElement.addType(JSTypeConstants.OBJECT_TYPE);

						function.addParameter(parameterElement);
					}
				}
				else
				{
					// @formatter:off
					String message = MessageFormat.format(
						"Expected JSFunction node when applying documentation; however, the node type was ''{0}'' instead. Source ={1}{2}",
						(node != null) ? node.getClass().getName() : "null",
						FileUtil.NEW_LINE,
						node
					);
					// @formatter:on

					IdeLog.logError(JSCorePlugin.getDefault(), message);
				}
			}

			// apply return types
			for (Tag tag : block.getTags(TagType.RETURN))
			{
				ReturnTag returnTag = (ReturnTag) tag;

				for (Type type : returnTag.getTypes())
				{
					ReturnTypeElement returnType = new ReturnTypeElement();

					returnType.setType(type.toSource());
					returnType.setDescription(returnTag.getText());

					function.addReturnType(returnType);
				}
			}

			// apply examples
			for (Tag tag : block.getTags(TagType.EXAMPLE))
			{
				ExampleTag exampleTag = (ExampleTag) tag;

				function.addExample(exampleTag.getText());
			}
		}
	}

	/**
	 * applyDocumentation
	 * 
	 * @param property
	 * @param block
	 */
	public static void applyDocumentation(PropertyElement property, JSNode node, DocumentationBlock block)
	{
		if (property instanceof FunctionElement)
		{
			applyDocumentation((FunctionElement) property, node, block);
		}
		else
		{
			if (block != null)
			{
				// apply description
				property.setDescription(block.getText());

				// apply types
				for (Tag tag : block.getTags(TagType.TYPE))
				{
					TypeTag typeTag = (TypeTag) tag;

					for (Type type : typeTag.getTypes())
					{
						ReturnTypeElement returnType = new ReturnTypeElement();

						returnType.setType(type.toSource());
						returnType.setDescription(typeTag.getText());

						property.addType(returnType);
					}
				}

				// apply examples
				for (Tag tag : block.getTags(TagType.EXAMPLE))
				{
					ExampleTag exampleTag = (ExampleTag) tag;

					property.addExample(exampleTag.getText());
				}
			}
		}
	}

	/**
	 * applySignature
	 * 
	 * @param property
	 * @param typeName
	 */
	public static void applySignature(PropertyElement property, String typeName)
	{
		if (property instanceof FunctionElement)
		{
			applySignature((FunctionElement) property, typeName);
		}
		else
		{
			property.addType(typeName);
		}
	}

	/**
	 * Takes a "Type,..,Function<ReturnType,ReturnType,...>" type string and unwraps it to apply the type information to
	 * the function and the return type info as well.
	 * 
	 * @param function
	 * @param typeName
	 */
	public static void applySignature(FunctionElement function, String typeName)
	{
		if (function != null && typeName != null)
		{
			// Look for "Function"
			int index = findFunctionType(typeName);
			if (index != -1)
			{
				// Grab substring up to that point, split by commas. Add the values as types.
				String types = typeName.substring(0, index);
				for (String type : types.split(JSTypeConstants.RETURN_TYPE_DELIMITER))
				{
					if (type.length() > 0)
					{
						function.addType(type);
					}
				}
				// Always make sure "Function" type is added to a function.
				function.addType(JSTypeConstants.FUNCTION_TYPE);
				// Grab from "Function" to end of string, extract return types and add them
				String functionWithReturnTypes = typeName.substring(index);
				for (String returnType : getFunctionSignatureReturnTypeNames(functionWithReturnTypes))
				{
					function.addReturnType(returnType);
				}
			}
		}
	}

	private static int findFunctionType(String typeName)
	{
		// Don't look inside <>!
		int index = -1;
		while (true)
		{
			// look for Function again start at index + 1
			index = typeName.indexOf(JSTypeConstants.FUNCTION_TYPE, index + 1);
			if (index == -1)
			{
				return -1;
			}
			if (getStack(typeName, '<', '>', index) == 0)
			{
				return index;
			}
		}
	}

	private static int getStack(String string, final char open, char close, int offset)
	{
		int stack = 0;
		int end = Math.min(offset, string.length());
		for (int i = 0; i < end; i++)
		{
			char c = string.charAt(i);
			if (c == open)
			{
				stack++;
			}
			else if (c == close)
			{
				stack--;
			}
		}
		return stack;
	}

	/**
	 * Wraps a given type with "Array<" and ">", effectively creating the type string used to denote an Array holding
	 * items of type {@code elementType}
	 * 
	 * @param elementType
	 * @return
	 */
	public static String createGenericArrayType(String elementType)
	{
		return JSTypeConstants.GENERIC_ARRAY_OPEN + elementType + JSTypeConstants.GENERIC_CLOSE;
	}

	/**
	 * Attempts to determine what type the members of an Array are. Handles syntax like "Anchor[]", "Array&lt;Object>".
	 * A simple "Array" assumes members are of type "Object". Null or empty string returns null.
	 * 
	 * @param type
	 * @return
	 */
	public static String getArrayElementType(String type)
	{
		String result = null;

		if (type != null && type.length() > 0)
		{
			if (type.endsWith(JSTypeConstants.ARRAY_LITERAL))
			{
				result = type.substring(0, type.length() - 2);
			}
			else if (type.startsWith(JSTypeConstants.GENERIC_ARRAY_OPEN)
					&& type.endsWith(JSTypeConstants.GENERIC_CLOSE))
			{
				result = type.substring(JSTypeConstants.GENERIC_ARRAY_OPEN.length(), type.length() - 1);
			}
			else if (type.equals(JSTypeConstants.ARRAY_TYPE))
			{
				result = JSTypeConstants.OBJECT_TYPE;
			}
		}

		return result;
	}

	/**
	 * Unwraps the type in a "Class&lt;TypeName>" type string.
	 * 
	 * @param typeName
	 * @return
	 */
	public static String getClassType(String typeName)
	{
		String result = null;

		if (isClassType(typeName))
		{
			result = typeName.substring(JSTypeConstants.GENERIC_CLASS_OPEN.length(), typeName.length()
					- JSTypeConstants.GENERIC_CLOSE.length());
		}

		return result;
	}

	/**
	 * Wraps the given type in between "Class<" and ">".
	 * 
	 * @param typeName
	 * @return
	 */
	public static String toClassType(String typeName)
	{
		return JSTypeConstants.GENERIC_CLASS_OPEN + typeName + JSTypeConstants.GENERIC_CLOSE;
	}

	/**
	 * Given a type string starting with "Function&lt;" and ending in ">". We parse out the string between the brackets
	 * and split it by commas. This should extract the return types of a function.
	 * 
	 * @param typeName
	 * @return
	 */
	public static List<String> getFunctionSignatureReturnTypeNames(String typeName)
	{
		if (typeName != null && typeName.startsWith(JSTypeConstants.GENERIC_FUNCTION_OPEN))
		{
			int startingIndex = JSTypeConstants.GENERIC_FUNCTION_OPEN.length();
			int endingIndex = typeName.lastIndexOf(JSTypeConstants.GENERIC_CLOSE);

			if (endingIndex != -1)
			{
				String returnTypes = typeName.substring(startingIndex, endingIndex);
				// If no comma in the string, can we just skip ahead to return the string in a list?
				if (returnTypes.indexOf(',') == -1)
				{
					return CollectionsUtil.newList(returnTypes);
				}

				// Split the return types up. We need to track the pairs of <>
				List<String> returnTypeNames = new ArrayList<String>();
				int length = returnTypes.length();
				int pointer = 0;
				int stack = 0;
				for (int i = 0; i < length; i++)
				{
					char c = returnTypes.charAt(i);
					switch (c)
					{
						case '<':
							stack++;
							break;
						case '>':
							stack--;
							break;
						case ',':
							if (stack == 0)
							{
								returnTypeNames.add(returnTypes.substring(pointer, i));
								pointer = i + 1;
							}
							break;

						default:
							break;
					}
				}
				returnTypeNames.add(returnTypes.substring(pointer));
				return returnTypeNames;
			}
		}

		return Collections.emptyList();
	}

	/**
	 * This method attempts to "fix" bad type names. This is a good starting point for tracking down busted type names,
	 * but ultimately the meat is in the JSNodeTypeInferrer and JSSymbolTypeInferrer.
	 * 
	 * @param type
	 * @return
	 */
	public static String validateTypeName(String type)
	{
		if (StringUtil.isEmpty(type))
		{
			IdeLog.logError(JSCorePlugin.getDefault(), new IllegalArgumentException(
					"Null or Empty type name attempting to be recorded for a return type.")); //$NON-NLS-1$
			return StringUtil.EMPTY;
		}

		// Function<>, Array<>, or Class<> type.
		int genericOpenIndex = type.indexOf('<');
		if (genericOpenIndex != -1)
		{
			// ideally we'd verify the types inside! We need to split out the types like in
			// getFunctionSignatureReturnTypeNames and validate each element.
			if (type.charAt(type.length() - 1) != '>')
			{
				String baseType = type.substring(0, genericOpenIndex);
				IdeLog.logError(JSCorePlugin.getDefault(),
						new IllegalArgumentException(MessageFormat.format("{0} type missing end '>'", baseType))); //$NON-NLS-1$
				return baseType;
			}
		}
		else if (!TYPE_NAME_PATTERN.matcher(type).matches())
		{
			// Look for Array types in "bad" format
			if (type.endsWith(JSTypeConstants.ARRAY_LITERAL))
			{
				// convert to Array<Type>
				return validateTypeName(JSTypeUtil.createGenericArrayType(JSTypeUtil.getArrayElementType(type)));
			}

			IdeLog.logWarning(
					JSCorePlugin.getDefault(),
					new IllegalArgumentException(MessageFormat.format(
							"Bad type name being set, something is going haywire: ''{0}''", type))); //$NON-NLS-1$

			int index = type.indexOf(',');
			if (index != -1)
			{
				return type.substring(0, index);
			}
			return StringUtil.EMPTY;
		}
		return type;
	}

	/**
	 * getName
	 * 
	 * @param node
	 * @return
	 */
	public static String getName(JSNode node)
	{
		String result = null;

		if (node != null)
		{
			List<String> parts = new ArrayList<String>();
			JSNode current = node;

			while (current != null)
			{
				switch (current.getNodeType())
				{
					case IJSNodeTypes.IDENTIFIER:
						parts.add(current.getText());
						break;

					case IJSNodeTypes.FUNCTION:
						JSFunctionNode function = (JSFunctionNode) current;
						IParseNode functionName = function.getName();

						if (!functionName.isEmpty())
						{
							parts.add(functionName.getText());
						}
						break;

					case IJSNodeTypes.NAME_VALUE_PAIR:
						JSNameValuePairNode entry = (JSNameValuePairNode) current;
						IParseNode entryName = entry.getName();
						String name = entryName.getText();

						if (entryName.getNodeType() == IJSNodeTypes.STRING)
						{
							name = name.substring(1, name.length() - 1);
						}

						parts.add(name);
						break;

					case IJSNodeTypes.DECLARATION:
						JSDeclarationNode declaration = (JSDeclarationNode) current;
						IParseNode declarationName = declaration.getIdentifier();

						parts.add(declarationName.getText());
						break;

					case IJSNodeTypes.ASSIGN:
						JSAssignmentNode assignment = (JSAssignmentNode) current;
						IParseNode lhs = assignment.getLeftHandSide();

						if (lhs instanceof JSIdentifierNode)
						{
							parts.add(lhs.getText());
						}
						else if (lhs instanceof JSGetPropertyNode)
						{
							JSGetPropertyNode getProp = (JSGetPropertyNode) lhs;
							if (getProp.getChild(0) instanceof JSThisNode)
							{
								parts.add(getProp.getChild(1).getText());
							}
						}
						else if (lhs instanceof JSGetElementNode)
						{
							JSGetElementNode getElement = (JSGetElementNode) lhs;
							// add the property name held in the string
							parts.add(StringUtil.stripQuotes(getElement.getChild(1).getText()));
							IParseNode left = getElement.getFirstChild();
							// could be invoke, getElement, getProperty, construct
							if (left instanceof JSGetPropertyNode)
							{
								JSGetPropertyNode getProp = (JSGetPropertyNode) left;
								if (getProp.getChild(0) instanceof JSThisNode)
								{
									parts.add(getProp.getChild(1).getText());
								}
							}
						}
						break;

					default:
						break;
				}

				IParseNode parent = current.getParent();

				current = (parent instanceof JSNode) ? (JSNode) parent : null;
			}

			if (parts.size() > 0)
			{
				Collections.reverse(parts);

				result = StringUtil.join(".", parts); //$NON-NLS-1$
			}
		}

		// Don't allow certain names to avoid confusion and to prevent overwriting
		// of core types
		if (FILTERED_TYPES.contains(result))
		{
			result = null;
		}

		return result;
	}

	/**
	 * Generates a random unique type name.
	 * 
	 * @return
	 */
	public static String getUniqueTypeName()
	{
		return JSTypeConstants.DYNAMIC_CLASS_PREFIX + UUID.randomUUID();
	}

	/**
	 * Used to generate a unique type name to hold module definitions.
	 * 
	 * @param string
	 * @return
	 */
	public static String getUniqueTypeName(String string)
	{
		if (string == null)
		{
			return "$module_" + UUID.randomUUID(); //$NON-NLS-1$
		}
		return "$module_" + UUID.nameUUIDFromBytes(string.getBytes()); //$NON-NLS-1$
	}

	/**
	 * isClassType
	 * 
	 * @param typeName
	 * @return
	 */
	public static boolean isClassType(String typeName)
	{
		return typeName != null && typeName.startsWith(JSTypeConstants.GENERIC_CLASS_OPEN)
				&& typeName.endsWith(JSTypeConstants.GENERIC_CLOSE);
	}

	/**
	 * Does this type string look like it's a function that returns types?
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isFunctionPrefix(String type)
	{
		boolean result = false;

		if (type != null)
		{
			Matcher m = JSTypeConstants.FUNCTION_PREFIX.matcher(type);

			result = m.find();
		}

		return result;
	}

	public static String toFunctionType(Collection<String> returnTypes)
	{
		if (CollectionsUtil.isEmpty(returnTypes))
		{
			return JSTypeConstants.FUNCTION_TYPE;
		}
		return JSTypeConstants.GENERIC_FUNCTION_OPEN
				+ StringUtil.join(JSTypeConstants.RETURN_TYPE_DELIMITER, returnTypes) + JSTypeConstants.GENERIC_CLOSE;
	}

	/**
	 * Wraps the given string (assumed to be a single type name, or a list of return types delimited by
	 * {@link JSTypeConstants#RETURN_TYPE_DELIMITER}) in between "Function<" and ">". Use {@link #toFunctionType(List)}
	 * if you have a collection of type names.
	 * 
	 * @param type
	 * @return
	 */
	public static String toFunctionType(String type)
	{
		if (type == null || type.length() == 0)
		{
			return JSTypeConstants.FUNCTION_TYPE;
		}
		return JSTypeConstants.GENERIC_FUNCTION_OPEN + type + JSTypeConstants.GENERIC_CLOSE;
	}

	/**
	 * Given a list of type elements, return a list of class elements. We divide class members and instance members into
	 * Class\<Type\> and Type, respectively. A ClassElement recombines these separate items into a single item with
	 * members tagged appropriately.
	 * 
	 * @param types
	 * @return
	 */
	public static List<ClassElement> typesToClasses(Collection<TypeElement> types)
	{
		List<ClassElement> classes = new ArrayList<ClassElement>();

		if (types != null)
		{
			Map<String, ClassElement> classesByName = new HashMap<String, ClassElement>();

			for (TypeElement type : types)
			{
				String typeName = type.getName();
				boolean isClassType = isClassType(typeName);
				String baseName = isClassType ? getClassType(type.getName()) : typeName;

				if (!classesByName.containsKey(baseName))
				{
					ClassElement clss = new ClassElement();

					clss.setName(baseName);

					classesByName.put(baseName, clss);
				}

				ClassElement clss = classesByName.get(baseName);

				if (isClassType)
				{
					clss.addClassType(type);
				}
				else
				{
					clss.addInstanceType(type);
				}
			}

			classes = new ArrayList<ClassElement>(classesByName.values());
		}

		return classes;
	}

	/**
	 * JSContentAssistUtil
	 */
	private JSTypeUtil()
	{
	}

	public static String getGlobalType(IProject project, String fileName)
	{
		// How are we really supposed to determine this? for PHP/HTML/ERB we _know_ it's Window. For JS/JSCA/SDOCML, we
		// don't really ever know.
		// For now, we can just assume any types defined in files hang off "Global", but when looking up APIs we need to
		// determine the correct one...

		// If the file is HTML/PHP/ERB, assume Window. We cheat by checking if _not_ associated with JS
		if (fileName != null)
		{
			IContentType type = Platform.getContentTypeManager().getContentType(IJSConstants.CONTENT_TYPE_JS);
			if (!type.isAssociatedWith(fileName) && !fileName.endsWith("jsca") && !fileName.endsWith("json") //$NON-NLS-1$ //$NON-NLS-2$
					&& !fileName.endsWith("sdocml")) //$NON-NLS-1$
			{
				return JSTypeConstants.WINDOW_TYPE;
			}
		}

		// If project is Node.ACS or TiMobile, assume Global
		// FIXME Use an extension point or something for this?
		if (project != null)
		{
			try
			{
				String[] natureIds = project.getDescription().getNatureIds();
				String matchingNature = CollectionsUtil.find(Arrays.asList(natureIds), new IFilter<String>()
				{
					public boolean include(String item)
					{
						return item.startsWith("com.appcelerator."); //$NON-NLS-1$
					}
				});
				if (matchingNature != null)
				{
					return JSTypeConstants.GLOBAL_TYPE;
				}
			}
			catch (CoreException e)
			{
				IdeLog.logInfo(JSCorePlugin.getDefault(), "Failed to get project description", e, null); //$NON-NLS-1$
			}
		}

		// Otherwise default to Window.
		return JSTypeConstants.WINDOW_TYPE;
	}

	public static TypeElement createGlobalType(String globalTypeName)
	{
		TypeElement window = new TypeElement();
		window.setName(globalTypeName);
		if (JSTypeConstants.WINDOW_TYPE.equals(globalTypeName))
		{
			window.addParentType(JSTypeConstants.GLOBAL_TYPE);
		}
		return window;
	}
}
