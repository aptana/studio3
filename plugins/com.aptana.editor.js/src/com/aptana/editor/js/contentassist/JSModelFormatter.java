package com.aptana.editor.js.contentassist;

import java.util.List;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;

public class JSModelFormatter
{
	private static final String GENERIC_CLASS_CLOSE = ">"; //$NON-NLS-1$
	private static final String GENERIC_CLASS_OPEN = "Class<"; //$NON-NLS-1$

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
	 * formatFunction
	 * 
	 * @param function
	 * @return
	 */
	public static String getDescription(FunctionElement function)
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
		
		// return type
		addReturnTypes(buffer, function.getReturnTypes(), "void"); //$NON-NLS-1$
		
		// description
		buffer.append("<br><br>"); //$NON-NLS-1$
		buffer.append(function.getDescription());
		
		return buffer.toString();
	}
	
	/**
	 * formatProperty
	 * 
	 * @param property
	 * @return
	 */
	public static String getDescription(PropertyElement property)
	{
		if (property instanceof FunctionElement)
		{
			return getDescription((FunctionElement) property);
		}
		
		StringBuilder buffer = new StringBuilder();
		
		// title
		buffer.append("<b>").append(property.getName()).append("</b>"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// type
		addReturnTypes(buffer, property.getTypes(), "undefined"); //$NON-NLS-1$
		
		// description
		buffer.append("<br><br>"); //$NON-NLS-1$
		buffer.append(property.getDescription());
		
		return buffer.toString();
	}
	
	/**
	 * getDescription
	 * 
	 * @param header
	 * @param items
	 * @return
	 */
	public static String getDescription(String header, List<String> items)
	{
		StringBuilder buffer = new StringBuilder();
		
		// emit header
		buffer.append("<b>").append(header).append("</b><br><br>"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// emit list
		buffer.append(StringUtil.join("<br>", items)); //$NON-NLS-1$
		
		return buffer.toString();
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
				result = "Object";
			}
			else
			{
				result = type;
			}
		}
		
		return result;
	}
	
	/**
	 * getName
	 * 
	 * @param function
	 * @return
	 */
	public static String getName(FunctionElement function)
	{
		return function.getName() + "()"; //$NON-NLS-1$
	}
	
	/**
	 * getName
	 * 
	 * @param property
	 * @return
	 */
	public static String getName(PropertyElement property)
	{
		if (property instanceof FunctionElement)
		{
			return getName((FunctionElement) property);
		}
		else
		{
			return property.getName();
		}
	}
	
	private JSModelFormatter()
	{
	}
}
