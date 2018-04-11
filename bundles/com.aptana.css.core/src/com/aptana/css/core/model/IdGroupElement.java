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
 * IdGroupElement
 */
public class IdGroupElement extends BaseElement
{

	private Index index;
	private List<String> ids;

	public IdGroupElement(Index index)
	{
		this.index = index;
		setName(Messages.IdGroupElement_IdGroupName);
	}

	/**
	 * getIds
	 * 
	 * @return
	 */
	public List<String> getIds()
	{
		if (ids == null)
		{
			CSSIndexQueryHelper queryHelper = new CSSIndexQueryHelper();
			Map<String, String> members = queryHelper.getIDs(index);

			if (!CollectionsUtil.isEmpty(members))
			{
				ids = new ArrayList<String>(members.keySet());
			}
			else
			{
				ids = Collections.emptyList();
			}
		}

		return ids;
	}
}
