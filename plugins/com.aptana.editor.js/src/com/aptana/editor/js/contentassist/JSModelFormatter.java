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
package com.aptana.editor.js.contentassist;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.StringUtil;
import com.aptana.core.util.URIUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.SinceElement;

public class JSModelFormatter
{
	private static final Map<String, Image> TYPE_IMAGE_MAP;

	// used for mixed types
	private static final Image PROPERTY = Activator.getImage("/icons/js_property.png"); //$NON-NLS-1$

	private static final String NEW_LINE = "<br>"; //$NON-NLS-1$
	private static final String DOUBLE_NEW_LINE = NEW_LINE + NEW_LINE;

	/**
	 * static initializer
	 */
	static
	{
		TYPE_IMAGE_MAP = new HashMap<String, Image>();
		TYPE_IMAGE_MAP.put(JSTypeConstants.ARRAY_TYPE, Activator.getImage("/icons/array-literal.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.BOOLEAN_TYPE, Activator.getImage("/icons/boolean.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.FUNCTION_TYPE, Activator.getImage("/icons/js_function.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.NULL_TYPE, Activator.getImage("/icons/null.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.NUMBER_TYPE, Activator.getImage("/icons/number.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.OBJECT_TYPE, Activator.getImage("/icons/object-literal.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.REG_EXP_TYPE, Activator.getImage("/icons/regex.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.STRING_TYPE, Activator.getImage("/icons/string.png")); //$NON-NLS-1$
	}

	/**
	 * addDefiningFiles
	 * 
	 * @param buffer
	 * @param property
	 */
	private static void addDefiningFiles(StringBuilder buffer, PropertyElement property, URI projectURI)
	{
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
	}

	/**
	 * addDescription
	 * 
	 * @param buffer
	 * @param property
	 */
	private static void addDescription(StringBuilder buffer, PropertyElement property)
	{
		String description = property.getDescription();

		if (description != null && description.length() > 0)
		{
			buffer.append(DOUBLE_NEW_LINE);
			buffer.append(description);
		}
	}

	/**
	 * addExamples
	 * 
	 * @param buffer
	 * @param examples
	 */
	private static void addExamples(StringBuilder buffer, List<String> examples)
	{
		if (examples != null && examples.size() > 0)
		{
			buffer.append(DOUBLE_NEW_LINE);
			buffer.append("<b>").append(Messages.JSModelFormatter_Exampes_Section_Header).append("</b>");//$NON-NLS-1$ //$NON-NLS-2$
			buffer.append(NEW_LINE);

			// emit list
			buffer.append(StringUtil.join(DOUBLE_NEW_LINE, examples));
		}
	}

	/**
	 * addSpecifications
	 * 
	 * @param buffer
	 * @param property
	 */
	private static void addSpecifications(StringBuilder buffer, PropertyElement property)
	{
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
	}

	/**
	 * addTypes
	 * 
	 * @param buffer
	 * @param types
	 */
	private static void addTypes(StringBuilder buffer, List<String> types)
	{
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

		addTypes(buffer, function.getReturnTypeNames());
		addDescription(buffer, function);
		addExamples(buffer, function.getExamples());
		addDefiningFiles(buffer, function, projectURI);
		addSpecifications(buffer, function);

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

		addTypes(buffer, property.getTypeNames());
		addDescription(buffer, property);
		addExamples(buffer, property.getExamples());
		addDefiningFiles(buffer, property, projectURI);
		addSpecifications(buffer, property);

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
