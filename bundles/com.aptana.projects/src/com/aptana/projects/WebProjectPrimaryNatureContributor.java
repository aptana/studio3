/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects;

import org.eclipse.core.runtime.IPath;

import com.aptana.projects.primary.natures.AbstractPrimaryNatureContributor;
import com.aptana.projects.primary.natures.IPrimaryNatureContributor;

public class WebProjectPrimaryNatureContributor extends AbstractPrimaryNatureContributor
{
	private static final String WEB_PERSPECTIVE_ID = "com.aptana.ui.WebPerspective"; //$NON-NLS-1$

	public int getPrimaryNatureRank(IPath projectPath)
	{
		// FIXME This is the default primary nature if no other contributor vote to be a primary one.
		// Since the web perspective is overridden by Titanium Studio, it is not valid to rely on the
		// web perspective. Is there is any specific condition to enforce web primary nature ?
		return IPrimaryNatureContributor.NOT_PRIMARY;
	}

}
