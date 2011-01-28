/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.browser.internal;

import org.eclipse.jface.resource.ImageDescriptor;

public class BrowserBackgroundImage
{

	private final String id;
	private final ImageDescriptor imageDescriptor;
	private final int horizontalIndent;
	private final int verticalIndent;
	private final boolean blackBackground;

	public BrowserBackgroundImage(String id, ImageDescriptor imageDescriptor, int horizontalIndent, int verticalIndent,
			boolean blackBackground)
	{
		this.id = id;
		this.imageDescriptor = imageDescriptor;
		this.horizontalIndent = horizontalIndent;
		this.verticalIndent = verticalIndent;
		this.blackBackground = blackBackground;
	}

	public String getId()
	{
		return id;
	}

	public ImageDescriptor getImageDescriptor()
	{
		return imageDescriptor;
	}

	public int getHorizontalIndent()
	{
		return horizontalIndent;
	}

	public int getVerticalIndent()
	{
		return verticalIndent;
	}

	public boolean isBlackBackground()
	{
		return blackBackground;
	}
}
