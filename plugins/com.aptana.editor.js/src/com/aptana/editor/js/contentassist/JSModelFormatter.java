package com.aptana.editor.js.contentassist;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSTypes;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.SinceElement;

public class JSModelFormatter
{
	private static final String GENERIC_CLASS_CLOSE = ">"; //$NON-NLS-1$
	private static final String GENERIC_CLASS_OPEN = "Class<"; //$NON-NLS-1$

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
			String prefix = (projectURI != null) ? decodeURI(projectURI.toString()) : null;

			// back up one segment so we include the project name in the document
			if (prefix != null && prefix.length() > 2)
			{
				int index = prefix.lastIndexOf('/', prefix.length() - 2);

				if (index != -1 && index > 0)
				{
					prefix = prefix.substring(0, index - 1);
				}
			}

			buffer.append("<br><br>"); //$NON-NLS-1$
			buffer.append("Defined In:"); //$NON-NLS-1$
			buffer.append("<br>"); //$NON-NLS-1$

			boolean first = true;

			for (String document : documents)
			{
				document = decodeURI(document);

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
					buffer.append("<br>");
				}

				buffer.append(document);
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
			buffer.append("<br><br>"); //$NON-NLS-1$
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
			buffer.append("<br><br>"); //$NON-NLS-1$
			buffer.append("<b>").append("Examples:").append("</b>");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buffer.append("<br>"); //$NON-NLS-1$

			// emit list
			buffer.append("<pre>");
			buffer.append(StringUtil.join("<br><br>", examples)); //$NON-NLS-1$
			buffer.append("</pre>");
		}
	}
	
	/**
	 * addReturnTypes
	 * 
	 * @param buffer
	 * @param returnTypes
	 * @param defaultType
	 */
	private static void addReturnTypes(StringBuilder buffer, ReturnTypeElement[] returnTypes, String defaultType)
	{
		boolean first;

		if (returnTypes != null && returnTypes.length > 0)
		{
			buffer.append(" : "); //$NON-NLS-1$

			first = true;

			for (ReturnTypeElement returnType : returnTypes)
			{
				if (first == false)
				{
					buffer.append("|"); //$NON-NLS-1$
				}
				else
				{
					first = false;
				}

				String type = getTypeDisplayName(returnType.getType());

				buffer.append(type);
			}
		}
		else
		{
			buffer.append(" : ").append(defaultType); //$NON-NLS-1$
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
			buffer.append("<br><br>");
			buffer.append("<b>").append("Specifications:").append("</b>");
			buffer.append("<br>");

			for (SinceElement since : property.getSinceList())
			{
				buffer.append("- ").append(since.getName());
				
				String version = since.getVersion();
				
				if (version != null && version.length() > 0)
				{
					buffer.append(" ").append(since.getVersion());
				}
				
				buffer.append("<br>");
			}
		}
	}

	/**
	 * decodeURI
	 * 
	 * @param uri
	 * @return
	 */
	private static String decodeURI(String uri)
	{
		String result = null;

		if (uri != null)
		{
			try
			{
				result = URLDecoder.decode(uri.toString(), "utf-8");
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
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

		// title
		buffer.append("<b>").append(function.getName()).append("</b>").append("("); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// append parameters
		boolean first = true;

		for (ParameterElement parameter : function.getParameters())
		{
			if (first == false)
			{
				buffer.append(", "); //$NON-NLS-1$
			}
			else
			{
				first = false;
			}

			String usage = parameter.getUsage();
			boolean isOptional = ("zero-or-more".equals(usage) || "optional".equals(usage)); //$NON-NLS-1$ //$NON-NLS-2$
			boolean isRepeating = ("zero-or-more".equals(usage) || "one-or-more".equals(usage)); //$NON-NLS-1$ //$NON-NLS-2$

			if (isOptional)
			{
				buffer.append("["); //$NON-NLS-1$
			}

			buffer.append("<b>").append(parameter.getName()).append("</b>"); //$NON-NLS-1$ //$NON-NLS-2$

			if (isRepeating)
			{
				buffer.append("+"); //$NON-NLS-1$
			}

			buffer.append(" : ").append(StringUtil.join("|", parameter.getTypes())); //$NON-NLS-1$ //$NON-NLS-2$

			if (isOptional)
			{
				buffer.append("]"); //$NON-NLS-1$
			}
		}

		buffer.append(")"); //$NON-NLS-1$

		addReturnTypes(buffer, function.getReturnTypes(), "void"); //$NON-NLS-1$
		addDescription(buffer, function);
		addDefiningFiles(buffer, function, projectURI);
		addExamples(buffer, function.getExamples());
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

		// title
		buffer.append("<b>").append(property.getName()).append("</b>"); //$NON-NLS-1$ //$NON-NLS-2$

		addReturnTypes(buffer, property.getTypes(), "undefined"); //$NON-NLS-1$
		addDescription(buffer, property);
		addDefiningFiles(buffer, property, projectURI);
		addExamples(buffer, property.getExamples());
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
			if (type.startsWith(GENERIC_CLASS_OPEN) && type.endsWith(GENERIC_CLASS_CLOSE))
			{
				result = type.substring(GENERIC_CLASS_OPEN.length(), type.length() - 1);
			}
			else if (type.startsWith(JSTypeWalker.DYNAMIC_CLASS_PREFIX))
			{
				result = JSTypes.OBJECT;
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
