/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.css.core.index.CSSIndexQueryHelper;
import com.aptana.index.core.Index;

/**
 * ColorGroupElement
 */
public class ColorGroupElement extends BaseElement
{

	private Index index;
	private List<String> members;

	public ColorGroupElement(Index index)
	{
		this.index = index;
		setName(Messages.ColorGroupElement_ColorElementName);
	}

	/**
	 * getColors
	 * 
	 * @return
	 */
	public List<String> getColors()
	{
		if (members == null)
		{
			CSSIndexQueryHelper queryHelper = new CSSIndexQueryHelper();
			Set<String> colors = queryHelper.getColors(index);

			if (!CollectionsUtil.isEmpty(colors))
			{
				members = new ArrayList<String>(colors);
			}
			else
			{
				members = Collections.emptyList();
			}
		}

		return members;
	}
}
