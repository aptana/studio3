/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import java.util.List;

import com.aptana.editor.css.contentassist.model.ICSSMetadataElement;
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
	public static String getDescription(ICSSMetadataElement element)
	{
		StringBuilder buffer = new StringBuilder();
		
		// emit name
		buffer.append("<b>").append(element.getName()).append("</b><br>"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// emit description
		String description = element.getDescription();
		
		if (description != null && description.length() > 0)
		{
			buffer.append(description).append("<br>"); //$NON-NLS-1$
		}
		
		// emit support browsers
		List<UserAgentElement> userAgents = element.getUserAgents();
		
		if (userAgents != null && userAgents.size() > 0)
		{
			buffer.append("<br>"); //$NON-NLS-1$
			buffer.append("<b>").append(Messages.CSSModelFormatter_Supported_User_Agents).append("</b><br>"); //$NON-NLS-1$ //$NON-NLS-2$
			
			for (int i = 0; i < userAgents.size(); i++)
			{
				UserAgentElement userAgent = userAgents.get(i);
				
				if (i > 0)
				{
					buffer.append(", "); //$NON-NLS-1$
				}
				
				buffer.append(userAgent.getPlatform()).append(" ").append(userAgent.getVersion()); //$NON-NLS-1$
			}
		}
		
		return buffer.toString();
	}
}
