/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class CommonOutlineLabelProvider extends LabelProvider
{

	public CommonOutlineLabelProvider()
	{
	}

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return getImage(((CommonOutlineItem) element).getReferenceNode());
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return getText(((CommonOutlineItem) element).getReferenceNode());
		}
		return super.getText(element);
	}
}
