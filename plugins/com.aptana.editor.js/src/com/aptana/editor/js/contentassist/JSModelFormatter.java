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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.StringUtil;
import com.aptana.core.util.URIUtil;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.SinceElement;

public class JSModelFormatter
{
	private static final Map<String, Image> TYPE_IMAGE_MAP;
	// used for mixed types
	private static final Image PROPERTY = JSPlugin.getImage("/icons/js_property.png"); //$NON-NLS-1$
	private static final String NEW_LINE = "<br>"; //$NON-NLS-1$
	private static final String DOUBLE_NEW_LINE = NEW_LINE + NEW_LINE;
	private static final char BULLET = '\u2022';

	/**
	 * static initializer
	 */
	static
	{
		TYPE_IMAGE_MAP = new HashMap<String, Image>();
		TYPE_IMAGE_MAP.put(JSTypeConstants.ARRAY_TYPE, JSPlugin.getImage("/icons/array-literal.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.BOOLEAN_TYPE, JSPlugin.getImage("/icons/boolean.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.FUNCTION_TYPE, JSPlugin.getImage("/icons/js_function.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.NULL_TYPE, JSPlugin.getImage("/icons/null.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.NUMBER_TYPE, JSPlugin.getImage("/icons/number.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.OBJECT_TYPE, JSPlugin.getImage("/icons/object-literal.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.REG_EXP_TYPE, JSPlugin.getImage("/icons/regex.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.STRING_TYPE, JSPlugin.getImage("/icons/string.png")); //$NON-NLS-1$
	}

	/**
	 * formatDefiningFiles
	 * 
	 * @param property
	 * @param projectURI
	 */
	private static String formatDefiningFiles(PropertyElement property, URI projectURI)
	{
		StringBuilder buffer = new StringBuilder();
		List<String> documents = property.getDocuments();

		if (documents != null && documents.isEmpty() == false)
		{
			String prefix = (projectURI != null) ? URIUtil.decodeURI(projectURI.toString()) : null;

			// back up one segment so we include the project name in the document
			if (prefix != null && prefix.length() > 2)
			{
				int index = prefix.lastIndexOf('/', prefix.length() - 2);

				if (index != -1 && index > 0)
				{
					prefix = prefix.substring(0, index - 1);
				}
			}

			buffer.append(DOUBLE_NEW_LINE);
			buffer.append("<b>").append(Messages.JSModelFormatter_Defined_Section_Header).append("</b>"); //$NON-NLS-1$ //$NON-NLS-2$
			buffer.append(NEW_LINE);

			boolean first = true;

			for (String document : documents)
			{
				document = URIUtil.decodeURI(document);

				if (prefix != null && document.startsWith(prefix))
				{
					document = document.substring(prefix.length() + 1);
				}

				if (first)
				{
					first = false;
				}
				else
				{
					buffer.append(NEW_LINE);
				}

				buffer.append("- ").append(document); //$NON-NLS-1$
			}
		}

		return buffer.toString();
	}

	/**
	 * formatDescription
	 * 
	 * @param property
	 */
	private static String formatDescription(PropertyElement property)
	{
		StringBuilder buffer = new StringBuilder();
		String description = property.getDescription();

		if (description != null && description.length() > 0)
		{
			buffer.append(DOUBLE_NEW_LINE);
			buffer.append(description);
		}

		return buffer.toString();
	}

	/**
	 * formatExamples
	 * 
	 * @param examples
	 */
	private static String formatExamples(List<String> examples)
	{
		StringBuilder buffer = new StringBuilder();

		if (examples != null && examples.size() > 0)
		{
			buffer.append(DOUBLE_NEW_LINE);
			buffer.append("<b>").append(Messages.JSModelFormatter_Exampes_Section_Header).append("</b>");//$NON-NLS-1$ //$NON-NLS-2$
			buffer.append(NEW_LINE);

			// emit list
			buffer.append(StringUtil.join(DOUBLE_NEW_LINE, examples));
		}

		return buffer.toString();
	}

	/**
	 * formatSpecifications
	 * 
	 * @param property
	 */
	private static String formatSpecifications(PropertyElement property)
	{
		StringBuilder buffer = new StringBuilder();
		List<SinceElement> sinceList = property.getSinceList();

		if (sinceList != null && sinceList.isEmpty() == false)
		{
			buffer.append(DOUBLE_NEW_LINE);
			buffer.append("<b>").append(Messages.JSModelFormatter_Specification_Header).append("</b>"); //$NON-NLS-1$ //$NON-NLS-2$
			buffer.append(NEW_LINE);

			for (SinceElement since : property.getSinceList())
			{
				buffer.append("- ").append(since.getName()); //$NON-NLS-1$

				String version = since.getVersion();

				if (version != null && version.length() > 0)
				{
					buffer.append(" ").append(since.getVersion()); //$NON-NLS-1$
				}

				buffer.append(NEW_LINE);
			}
		}

		return buffer.toString();
	}

	/**
	 * formatTypes
	 * 
	 * @param types
	 */
	private static String formatTypes(List<String> types)
	{
		StringBuilder buffer = new StringBuilder();

		buffer.append(" : "); //$NON-NLS-1$

		if (types != null && types.size() > 0)
		{
			List<String> typeDisplayNames = new ArrayList<String>();

			for (String type : types)
			{
				typeDisplayNames.add(getTypeDisplayName(type));
			}

			buffer.append(StringUtil.join(",", typeDisplayNames)); //$NON-NLS-1$
		}
		else
		{
			buffer.append(JSTypeConstants.NO_TYPE);
		}

		return buffer.toString();
	}

	/**
	 * getContextInfo
	 * 
	 * @param function
	 * @return
	 */
	public static String getContextInfo(FunctionElement function)
	{
		StringBuilder buffer = new StringBuilder();

		// function name with argument names
		List<String> paramNameAndType = new ArrayList<String>();
		buffer.append(function.getName());
		buffer.append("("); //$NON-NLS-1$

		for (ParameterElement parameter : function.getParameters())
		{
			paramNameAndType.add(parameter.getName() + formatTypes(parameter.getTypes()));
		}

		buffer.append(StringUtil.join(", ", paramNameAndType)); //$NON-NLS-1$
		buffer.append(")"); //$NON-NLS-1$
		buffer.append(formatTypes(function.getReturnTypeNames()));

		return buffer.toString();
	}

	/**
	 * getContextLines
	 * 
	 * @param function
	 * @return
	 */
	public static List<String> getContextLines(FunctionElement function)
	{
		List<String> result = new ArrayList<String>();

		if (function != null)
		{
			StringBuilder buffer = new StringBuilder();

			// line 1: function name with argument names
			result.add(getContextInfo(function));

			// line 2..n: one line for each argument description
			for (ParameterElement parameter : function.getParameters())
			{
				buffer.setLength(0);

				buffer.append(BULLET).append("\t").append(parameter.getName()).append(":\n"); //$NON-NLS-1$ //$NON-NLS-2$
				buffer.append(" \t").append(parameter.getDescription()); //$NON-NLS-1$
				result.add(buffer.toString());
			}
		}

		return result;
	}

	/**
	 * formatFunction
	 * 
	 * @param function
	 * @param projectURI
	 * @return
	 */
	public static String getDescription(FunctionElement function, URI projectURI)
	{
		StringBuilder buffer = new StringBuilder();

		buffer.append(function.getName());
		buffer.append("(").append(StringUtil.join(", ", function.getParameterTypes())).append(")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		buffer.append(formatTypes(function.getReturnTypeNames()));
		buffer.append(formatDescription(function));
		buffer.append(formatExamples(function.getExamples()));
		buffer.append(formatDefiningFiles(function, projectURI));
		buffer.append(formatSpecifications(function));

		return buffer.toString();
	}

	/**
	 * formatProperty
	 * 
	 * @param property
	 * @param projectURI
	 * @return
	 */
	public static String getDescription(PropertyElement property, URI projectURI)
	{
		if (property instanceof FunctionElement)
		{
			return getDescription((FunctionElement) property, projectURI);
		}

		StringBuilder buffer = new StringBuilder();
		buffer.append(property.getName());

		buffer.append(formatTypes(property.getTypeNames()));
		buffer.append(formatDescription(property));
		buffer.append(formatExamples(property.getExamples()));
		buffer.append(formatDefiningFiles(property, projectURI));
		buffer.append(formatSpecifications(property));

		return buffer.toString();
	}

	/**
	 * getDocumentDisplayName
	 * 
	 * @param document
	 * @return
	 */
	public static String getDocumentDisplayName(String document)
	{
		String result = null;

		if (document != null)
		{
			int index = document.lastIndexOf('/');

			if (index != -1)
			{
				result = document.substring(index + 1);
			}
			else
			{
				result = document;
			}

			result = URIUtil.decodeURI(result);
		}

		return result;
	}

	/**
	 * getImage
	 * 
	 * @param property
	 * @return
	 */
	public static Image getImage(PropertyElement property)
	{
		Image result = (property instanceof FunctionElement) ? TYPE_IMAGE_MAP.get(JSTypeConstants.FUNCTION_TYPE) : PROPERTY;

		if (property != null)
		{
			List<String> types = property.getTypeNames();

			if (types != null && types.size() == 1)
			{
				String type = types.get(0);

				if (TYPE_IMAGE_MAP.containsKey(type))
				{
					result = TYPE_IMAGE_MAP.get(type);
				}
				else if (type.startsWith(JSTypeConstants.DYNAMIC_CLASS_PREFIX))
				{
					result = TYPE_IMAGE_MAP.get(JSTypeConstants.OBJECT_TYPE);
				}
				else if (type.startsWith(JSTypeConstants.FUNCTION_TYPE))
				{
					result = TYPE_IMAGE_MAP.get(JSTypeConstants.FUNCTION_TYPE);
				}
			}
		}

		return result;
	}

	/**
	 * getDisplayTypeName
	 * 
	 * @param type
	 * @return
	 */
	public static String getTypeDisplayName(String type)
	{
		String result = null;

		if (type != null)
		{
			if (type.startsWith(JSTypeConstants.GENERIC_CLASS_OPEN) && type.endsWith(JSTypeConstants.GENERIC_CLOSE))
			{
				result = type.substring(JSTypeConstants.GENERIC_CLASS_OPEN.length(), type.length() - 1);
			}
			else if (type.startsWith(JSTypeConstants.DYNAMIC_CLASS_PREFIX))
			{
				result = JSTypeConstants.USER_TYPE;
			}
			else if (type.startsWith(JSTypeConstants.GENERIC_FUNCTION_OPEN) && type.endsWith(JSTypeConstants.GENERIC_CLOSE))
			{
				result = type.substring(JSTypeConstants.GENERIC_FUNCTION_OPEN.length(), type.length() - 1);
			}
			else
			{
				result = type;
			}
		}

		return result;
	}

	private JSModelFormatter()
	{
	}
}
