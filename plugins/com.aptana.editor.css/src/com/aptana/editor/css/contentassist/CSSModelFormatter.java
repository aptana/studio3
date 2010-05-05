package com.aptana.editor.css.contentassist;

import java.util.List;

import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.UserAgentElement;

public class CSSModelFormatter
{
	/**
	 * CSSModelFormatter
	 */
	private CSSModelFormatter()
	{
	}
	
	/**
	 * getDescription
	 * 
	 * @param element
	 * @return
	 */
	public static String getDescription(ElementElement element)
	{
		StringBuilder buffer = new StringBuilder();
		
		// emit name
		buffer.append("<b>").append(element.getName()).append("</b><br>");
		
		// emit description
		String description = element.getDescription();
		
		if (description != null && description.length() > 0)
		{
			buffer.append(description).append("<br>");
		}
		
		// emit support browsers
		List<UserAgentElement> userAgents = element.getUserAgents();
		
		if (userAgents != null && userAgents.size() > 0)
		{
			buffer.append("<br>");
			buffer.append("<b>").append("Supported User Agents:").append("</b><br>");
			
			for (int i = 0; i < userAgents.size(); i++)
			{
				UserAgentElement userAgent = userAgents.get(i);
				
				if (i > 0)
				{
					buffer.append(", ");
				}
				
				buffer.append(userAgent.getPlatform()).append(" ").append(userAgent.getVersion());
			}
		}
		
		return buffer.toString();
	}
	
	/**
	 * getDescription
	 * 
	 * @param property
	 * @return
	 */
	public static String getDescription(PropertyElement property)
	{
		StringBuilder buffer = new StringBuilder();
		
		// emit name
		buffer.append("<b>").append(property.getName()).append("</b><br>");
		
		// emit description
		String description = property.getDescription();
		
		if (description != null && description.length() > 0)
		{
			buffer.append(description).append("<br>");
		}
		
		// emit support browsers
		List<UserAgentElement> userAgents = property.getUserAgents();
		
		if (userAgents != null && userAgents.size() > 0)
		{
			buffer.append("<br>");
			buffer.append("<b>").append("Supported User Agents:").append("</b><br>");
			
			for (int i = 0; i < userAgents.size(); i++)
			{
				UserAgentElement userAgent = userAgents.get(i);
				
				if (i > 0)
				{
					buffer.append(", ");
				}
				
				buffer.append(userAgent.getPlatform()).append(" ").append(userAgent.getVersion());
			}
		}
		
		return buffer.toString();
	}
}
