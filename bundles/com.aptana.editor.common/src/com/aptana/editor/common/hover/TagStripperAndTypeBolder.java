/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.hover;

import com.aptana.core.IMap;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.replace.RegexPatternReplacer;

/**
 * @author Kevin Lindsey
 */
public class TagStripperAndTypeBolder extends RegexPatternReplacer
{

	public static final String BOLD_CLOSE_TAG = "</b>"; //$NON-NLS-1$
	public static final String BOLD_OPEN_TAG = "<b>"; //$NON-NLS-1$

	private boolean useHTML;

	public TagStripperAndTypeBolder()
	{
		// @formatter:off
		addPattern(
			"<[\\p{Alpha}$_][\\p{Alnum}_$]*(?:\\.[\\p{Alpha}$_][\\p{Alnum}_$]*)+>", //$NON-NLS-1$
			new IMap<String, String>()
			{
				public String map(String item)
				{
					String typeName = item.substring(1, item.length() - 1);

					if (useHTML)
					{
						return StringUtil.concat(BOLD_OPEN_TAG, typeName, BOLD_CLOSE_TAG);
					}
					else
					{
						return typeName;
					}
				}
			}
		);
		// @formatter:on
		addPattern("</?p>"); //$NON-NLS-1$
	}

	public void setUseHTML(boolean value)
	{
		useHTML = value;
	}
}