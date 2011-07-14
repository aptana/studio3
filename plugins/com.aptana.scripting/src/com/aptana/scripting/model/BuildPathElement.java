/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import com.aptana.core.util.SourcePrinter;

/**
 * BuildPathElement
 */
public class BuildPathElement extends AbstractBundleElement
{
	/**
	 * @param path
	 */
	public BuildPathElement(String path)
	{
		super(path);
	}

	/* (non-Javadoc)
	 * @see com.aptana.scripting.model.AbstractElement#getElementName()
	 */
	@Override
	protected String getElementName()
	{
		return "buildPath";
	}

	/* (non-Javadoc)
	 * @see com.aptana.scripting.model.AbstractElement#printBody(com.aptana.core.util.SourcePrinter)
	 */
	@Override
	protected void printBody(SourcePrinter printer)
	{
		// output path and scope
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this.getScope()); //$NON-NLS-1$
	}
}
