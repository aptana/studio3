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
import java.util.Map;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.css.core.index.CSSIndexQueryHelper;
import com.aptana.index.core.Index;

/**
 * ClassGroupElement
 */
public class ClassGroupElement extends BaseElement
{

	private Index index;
	private List<String> classes;

	public ClassGroupElement(Index index)
	{
		this.index = index;
		setName(Messages.ClassGroupElement_ClassGroupElementName);
	}

	/**
	 * getClasses
	 * 
	 * @return
	 */
	public List<String> getClasses()
	{
		if (classes == null)
		{
			CSSIndexQueryHelper queryHelper = new CSSIndexQueryHelper();
			Map<String, String> members = queryHelper.getClasses(index);

			if (!CollectionsUtil.isEmpty(members))
			{
				classes = new ArrayList<String>(members.keySet());
			}
			else
			{
				classes = Collections.emptyList();
			}
		}

		return classes;
	}
}
