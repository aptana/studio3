/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.net.URL;

import com.aptana.core.util.SourcePrinter;

/**
 * Represents a snippet category used to group snippets
 * 
 * @author nle
 */
public class SnippetCategoryElement extends AbstractBundleElement
{
	private String fIconPath;
	private URL fIconURL;

	public SnippetCategoryElement(String path)
	{
		super(path);
	}

	@Override
	protected String getElementName()
	{
		return "snippet_category"; //$NON-NLS-1$
	}

	@Override
	protected void printBody(SourcePrinter printer, boolean includeBlocks)
	{
		printer.printWithIndent("name: ").println(this.getDisplayName()); //$NON-NLS-1$
		// output path and scope
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this.getScope()); //$NON-NLS-1$
		printer.printWithIndent("icon: ").println(this.getIconPath()); //$NON-NLS-1$
	}

	/**
	 * getIconURL
	 * 
	 * @return
	 */
	public URL getIconURL()
	{
		if (fIconURL != null)
		{
			return fIconURL;
		}

		fIconURL = getURLFromPath(fIconPath);
		return fIconURL;

	}

	/**
	 * setIconPath
	 * 
	 * @param iconPath
	 */
	public void setIconPath(String iconPath)
	{
		fIconPath = iconPath;
	}

	/**
	 * getIconPath
	 * 
	 * @return
	 */
	public String getIconPath()
	{
		return fIconPath;
	}
}
