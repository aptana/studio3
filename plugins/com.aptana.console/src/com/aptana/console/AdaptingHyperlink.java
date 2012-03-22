/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.console;

import org.eclipse.ui.console.IHyperlink;

/**
 * Adapts a JFace hyperlink to a console hyperlink.
 * 
 * @author cwilliams
 */
public class AdaptingHyperlink implements IHyperlink
{

	private org.eclipse.jface.text.hyperlink.IHyperlink wrapped;

	public AdaptingHyperlink(org.eclipse.jface.text.hyperlink.IHyperlink wrapped)
	{
		this.wrapped = wrapped;
	}

	public void linkEntered()
	{
		// TODO Show a hover with the link text?

	}

	public void linkExited()
	{
		// TODO Hide the hover?
	}

	public void linkActivated()
	{
		wrapped.open();
	}

}
