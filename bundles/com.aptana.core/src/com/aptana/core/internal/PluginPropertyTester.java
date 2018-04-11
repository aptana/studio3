/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal;

import org.eclipse.core.expressions.PropertyTester;

import com.aptana.core.util.EclipseUtil;

/**
 * A property tester that checks the existence (or absence) or a plugin.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class PluginPropertyTester extends PropertyTester
{
	/**
	 * Test for the property. The given args array is expected to hold plugin-id's. The tester will check for all or
	 * them and will return true/false if all the plugins match the criteria.<br>
	 * The expected value may be 'true' or 'false' to check for the existence of the absence of the plugin(s).
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[],
	 *      java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		if ("hasPlugin".equals(property)) //$NON-NLS-1$
		{
			boolean expected = Boolean.parseBoolean(expectedValue.toString());
			for (Object pluginId : args)
			{
				String detectedVersion = EclipseUtil.getPluginVersion(pluginId.toString());
				if ((expected && detectedVersion == null) || (!expected && detectedVersion != null))
				{
					return false;
				}
			}
		}
		return true;
	}

}
