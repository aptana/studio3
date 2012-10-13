/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;

public abstract class AbstractProjectWizardContributor implements IProjectWizardContributor
{

	String natureId = StringUtil.EMPTY;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.projects.wizards.IProjectWizardContributor#setNatureId(java.lang.String)
	 */
	public void setNatureId(String natureId)
	{
		this.natureId = natureId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.projects.wizards.IProjectWizardContributor#hasNatureId(java.lang.String[])
	 */
	public boolean hasNatureId(String[] natureIds)
	{
		if (ArrayUtil.isEmpty(natureIds))
		{
			return false;
		}

		return CollectionsUtil.newList(natureIds).contains(natureId);
	}
}
